package co.edu.uco.backendvictus.crosscutting.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralizes the creation of loggers to avoid exposing logging frameworks to every layer.
 */
public final class LoggerHelper {

    private LoggerHelper() {
        // Utility class
    }

    public static Logger getLogger(final Class<?> source) {
        return LoggerFactory.getLogger(source);
    }
}
