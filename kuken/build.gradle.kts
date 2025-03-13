plugins {
    application
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.atomicfu)
}

application {
    mainClass.set("gg.kuken.LauncherKt")
}

dependencies {
    annotationProcessor(libs.validator.processor)
    implementation(libs.validator)
    implementation(libs.ktx.coroutines.core)
    implementation(libs.ktx.atomicfu)
    implementation(libs.ktx.serialization.hocon)
    implementation(libs.ktx.serialization.json)
    implementation(libs.dockerKotlin)
    implementation(libs.bcprov)
    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4j2)
    implementation(libs.hocon)
    implementation(libs.bundles.ktor)
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.bundles.exposed)
    implementation(libs.postgresql)
    testImplementation(libs.ktx.coroutines.test)
    testImplementation(kotlin("test"))
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass.get()
            attributes["Implementation-Version"] = project.version
        }
    }
}