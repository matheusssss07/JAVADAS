package br.com.challenge.domain.logging;

public interface Logger {
    void info(String message);
    void warn(String message);
    void error(String message, Throwable throwable);
    void debug(String message);

    default void info(String message, Object... args) {
        info(String.format(message, args));
    }

    default void warn(String message, Object... args) {
        warn(String.format(message, args));
    }

    default void debug(String message, Object... args) {
        debug(String.format(message, args));
    }

    default void error(String message, Object... args) {
        error(String.format(message, args), (Throwable) null);
    }
}