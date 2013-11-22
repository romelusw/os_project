package com.romelus_tran.cottoncandymonitor.monitor.listeners;

import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;

import java.util.List;

/**
 * An object capable of receiving a list of metrics.
 *
 * @author Woody Romelus
 */
public interface IResultListener {
    /**
     * Handles the data event from {@code com.romelus_tran.cottoncandymonitor.monitor.CottonCandyMonitor}
     * @param metrics the data that was collected
     * @return a response indicating if the data was handled
     */
    ResultListenerResponse receive(List<MetricUnit> metrics);
}