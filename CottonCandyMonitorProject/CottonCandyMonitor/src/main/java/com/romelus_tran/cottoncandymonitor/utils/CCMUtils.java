package com.romelus_tran.cottoncandymonitor.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for common functionality.
 *
 * @author Woody Romelus
 */
public class CCMUtils {
    private static final String APP_NAME = "com.romelus_tran.cottoncandymonitor.";

    /**
     * Creates a logger facade.
     * @param clazz the object for which to name the logger
     * @return the logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(APP_NAME + clazz.getSimpleName());
    }
}