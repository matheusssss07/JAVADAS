package br.com.challenge.infrastructure.logging;

import br.com.challenge.domain.logging.Logger;


public class LoggerFactory {


    public static Logger getLogger(Class<?> clazz) {
        return new LoggerImpl(clazz);
    }


    public static Logger getLogger(String name) {

        try {
            return new LoggerImpl(Class.forName(name));
        } catch (ClassNotFoundException e) {
            return new LoggerImpl(LoggerFactory.class);
        }
    }
}