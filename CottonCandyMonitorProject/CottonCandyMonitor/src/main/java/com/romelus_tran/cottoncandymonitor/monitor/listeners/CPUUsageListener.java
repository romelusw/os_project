package com.romelus_tran.cottoncandymonitor.monitor.listeners;

import com.romelus_tran.cottoncandymonitor.graphs.CPUUsageGraph;
import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;

import java.util.List;

/**
 * This is the listener for CPUUsage. Every time we receive data on CPU Usage,
 * receive is called.
 * If the data is valid, then we pass the data to the CPUUsageGraph to update the chart.
 *
 * Created by Brian on 12/4/13.
 */
public class CPUUsageListener implements IResultListener {

    // store reference to the instance of CPUUsageGraph
    CPUUsageGraph _cpuUsageGraph;

    public CPUUsageListener(CPUUsageGraph cpuUsageGraph) {
        _cpuUsageGraph = cpuUsageGraph;
    }

    /**
     * Receive is called when MonitorUtil has data for the listener.
     * @param metrics the data that was collected
     * @return whether the results were valid or not
     */
    public ResultListenerResponse receive(List<MetricUnit> metrics) {
        if (metrics.isEmpty()) {
            return ResultListenerResponse.FAILURE;
        }

        _cpuUsageGraph.updateGraph(metrics);
        return ResultListenerResponse.SUCCESS;
    }
}
