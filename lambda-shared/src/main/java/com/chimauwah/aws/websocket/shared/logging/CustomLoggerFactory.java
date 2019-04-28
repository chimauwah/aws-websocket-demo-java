package com.chimauwah.aws.websocket.shared.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * CustomLoggerFactory to be used to get an instance of {@link Logger}
 * <p>
 * For more info: http://logging.apache.org/log4j/2.x/manual/configuration.html
 */
public final class CustomLoggerFactory {

    private CustomLoggerFactory() {
    }

    /**
     * Returns a Logger with the specified name
     *
     * @param name The logger name. If null the name of the calling class will be used.
     * @return The Logger
     */
    public static Logger getLogger(String name) {
        return LogManager.getLogger(name);
    }

    /**
     * Returns a Logger with the specified name
     *
     * @param clz The class to be set as Logger name
     * @return The Logger
     */
    public static Logger getLogger(Class<?> clz) {
        return getLogger(clz.getSimpleName());
    }
}
