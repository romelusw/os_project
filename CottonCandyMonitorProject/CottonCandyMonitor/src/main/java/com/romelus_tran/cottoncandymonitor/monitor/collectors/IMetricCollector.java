package com.romelus_tran.cottoncandymonitor.monitor.collectors;

import android.content.Context;

import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;

import java.util.List;

/**
 * An object capable of bundling data sets for a particular context.
 *
 * @author Woody Romelus
 */
public interface IMetricCollector {
    /**
     * Retrieves a list of pertinent {@code MetricUnit} for some context.
     *
     * @param context the application's scope
     * @return the list of data metrics
     */
    List<MetricUnit> collectData(final Context context);

    /**
     * Retrieves the gap of time in between collection cycles.
     *
     * @return the polling intervals measured in seconds
     */
    int pollingInterval();
}