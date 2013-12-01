package com.romelus_tran.cottoncandymonitor.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for common functionality.
 *
 * @author Woody Romelus
 */
public class CCMUtils {
    private final static Logger logger = getLogger(CCMUtils.class);

    /**
     * Creates a logger facade.
     *
     *
     * @param clazz the object for which to name the logger
     * @return the logger
     */
    public static Logger getLogger(final Class<?> clazz) {
        return LogManager.getLogger(clazz.getSimpleName());
    }

    /**
     * Converts an input stream into a string.
     *
     * @param is the {@link java.io.InputStream}
     * @return the string representation
     */
    public static String convertStreamToString (final InputStream is) {
        final StringBuilder sb = new StringBuilder();
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (final IOException e) {
            logger.error("Could not read input stream.", e);
        }
        return sb.toString();
    }
}