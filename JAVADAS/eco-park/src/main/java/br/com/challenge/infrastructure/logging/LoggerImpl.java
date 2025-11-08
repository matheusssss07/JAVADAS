package br.com.challenge.infrastructure.logging;

import br.com.challenge.domain.logging.Logger;
import java.util.logging.Level;


class LoggerImpl implements Logger {

    private final java.util.logging.Logger jdkLogger;

    public LoggerImpl(Class<?> clazz) {
        this.jdkLogger = java.util.logging.Logger.getLogger(clazz.getName());
    }

    @Override
    public void info(String message) {
        jdkLogger.info(message);
    }

    @Override
    public void warn(String message) {
        jdkLogger.warning(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (throwable != null) {
            jdkLogger.log(Level.SEVERE, message, throwable);
        } else {
            jdkLogger.severe(message);
        }
    }

    @Override
    public void debug(String message) {
        jdkLogger.fine(message);
    }

}