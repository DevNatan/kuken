package gg.kuken

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigParseable
import gg.kuken.core.EventDispatcher
import gg.kuken.core.EventDispatcherImpl
import gg.kuken.core.security.BcryptHash
import gg.kuken.core.security.Hash
import gg.kuken.features.account.AccountDI
import gg.kuken.features.auth.AuthDI
import gg.kuken.features.identity.IdentityGeneratorService
import gg.kuken.http.Http
import jakarta.validation.Validation
import jakarta.validation.Validator
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.vendors.DatabaseDialect
import org.jetbrains.exposed.sql.vendors.currentDialect
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.io.File
import java.sql.SQLException
import kotlin.system.exitProcess

internal fun main() {
    val appConfig = loadConfig()
    if (appConfig.devMode) { setupDevMode() }

    val db = DatabaseFactory(appConfig).create()
    configureDI(appConfig, db)

    runBlocking {
        checkDatabaseConnection(
            database = db,
            appConfig = appConfig
        )

        Http.start()
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun loadConfig(): KukenConfig {
    val parseOptions = ConfigParseOptions.defaults().setAllowMissing(true)
    val config = ConfigFactory.parseResources("kuken.local.conf")
        .withFallback(ConfigFactory.parseFile(File("kuken.conf"), parseOptions))
        .withFallback(ConfigFactory.parseResources("kuken.conf", parseOptions))

    return Hocon {}.decodeFromConfig(config)
}

private fun setupDevMode() {
    System.setProperty(
        kotlinx.coroutines.DEBUG_PROPERTY_NAME,
        kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON,
    )
}

class DatabaseFactory(private val appConfig: KukenConfig) {


    fun create(): Database = Database.connect(
        url = "jdbc:postgresql://${appConfig.db.host}",
        user = appConfig.db.user,
        password = appConfig.db.password,
        driver = "org.postgresql.Driver",
        databaseConfig = DatabaseConfig {
            useNestedTransactions = true
            if (appConfig.devMode) {
                sqlLogger = Slf4jSqlDebugLogger
            }
        }
    )
}


private suspend fun checkDatabaseConnection(database: Database, appConfig: KukenConfig) {
    try {
        newSuspendedTransaction(db = database) {
            database.connector()
        }
    } catch (exception: SQLException) {
        if (appConfig.devMode) {
            error("Unable to establish database connection.")
            exception.printStackTrace()
        } else {
            error("Unable to establish database connection: ${exception.message}")
        }

        exitProcess(0)
    }
}

private fun configureDI(appConfig: KukenConfig, db: Database) {
    startKoin {
        val root = module {
            single { appConfig }
            single { db }
            single<Hash> { BcryptHash }
            single { IdentityGeneratorService() }
            single<Validator> {
                Validation.byDefaultProvider()
                    .configure()
                    .messageInterpolator(ParameterMessageInterpolator())
                    .buildValidatorFactory()
                    .validator
            }
            single<EventDispatcher> { EventDispatcherImpl() }
        }

        modules(root, AccountDI, AuthDI)
    }
}