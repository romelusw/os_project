package com.romelus_tran.cottoncandymonitor.monitor;

import java.util.List;

/**
 * An object capable of bundling data sets for a particular context.
 */
public interface IDataBundler {
    /**
     * Retrieves a list of pertinent {@code MetricUnit} for some context.
     * @return the list of data metrics
     */
    List<MetricUnit> collectData();

    /**
     * Retrieves the gap of time in between collection cycles.
     * @return the polling intervals measured in milliseconds
     */
    int pollingInterval();
}