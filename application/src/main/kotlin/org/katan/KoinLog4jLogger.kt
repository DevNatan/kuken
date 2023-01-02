package org.katan

import org.apache.logging.log4j.LogManager
import org.koin.core.Koin
import org.koin.core.logger.MESSAGE
import org.koin.core.logger.Level as KoinLogLevel
import org.koin.core.logger.Logger as KoinLogger

typealias Log4jLogger = org.apache.logging.log4j.Logger
typealias Log4jLogLevel = org.apache.logging.log4j.Level

/**
 * Implementation of Koin's Logger backed by a Log4j2 Logger
 */
internal class KoinLog4jLogger : KoinLogger(KoinLogLevel.DEBUG) {
	
    companion object {
        private val backingLogger: Log4jLogger = LogManager.getLogger(Koin::class.java)
    }
	
    override fun log(level: KoinLogLevel, msg: MESSAGE) {
        backingLogger.log(transformLevel(level), msg)
    }
	
    /**
     * Transform a Koin log level to Log4J log level.
     * @param level The Koin log level.
     */
    private fun transformLevel(level: KoinLogLevel): Log4jLogLevel {
        return when (level) {
            KoinLogLevel.DEBUG -> Log4jLogLevel.DEBUG
            KoinLogLevel.INFO -> Log4jLogLevel.INFO
            KoinLogLevel.ERROR -> Log4jLogLevel.ERROR
            else -> Log4jLogLevel.ALL
        }
    }
}
