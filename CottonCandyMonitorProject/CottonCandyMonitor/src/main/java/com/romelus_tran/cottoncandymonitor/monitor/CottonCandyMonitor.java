package com.romelus_tran.cottoncandymonitor.monitor;

import com.romelus_tran.cottoncandymonitor.monitor.collectors.IMetricCollector;
import com.romelus_tran.cottoncandymonitor.monitor.listeners.IResultListener;
import com.romelus_tran.cottoncandymonitor.utils.CCMUtils;

import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An object that orchestrates the execution and persistence of {@code IMetricCollector}
 *
 * @author Woody Romelus
 */
public class CottonCandyMonitor {

    private static CottonCandyMonitor instance;
    private final ScheduledExecutorService scheduler;
    private final Logger logger = CCMUtils.getLogger(CottonCandyMonitor.class);
    private int THREAD_POOL_SIZE = 1;

    /**
     * Default Constructor.
     */
    protected CottonCandyMonitor() {
        // Instantiation not allowed

        logger.info("Starting Monitor with thread pool size of [{}]", THREAD_POOL_SIZE);
        scheduler = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
    }

    /**
     * Returns the static reference to the singleton instance.
     * @return the singleton
     */
    public static CottonCandyMonitor getInstance() {
        if(instance == null) {
            instance = new CottonCandyMonitor();
        }
        return instance;
    }

    public boolean register(final IMetricCollector collector, final List<IResultListener> listeners) {
        boolean retVal = false;
        if(true) {
            scheduler.schedule(new DataBundlerRunner(collector, listeners),
                    collector.pollingInterval(), TimeUnit.SECONDS);
        }
        return true;
    }

    public boolean shutdown() {

        try {
            logger.info("Shutting Down Scheduler Service.");
            scheduler.shutdown();
            TimeUnit.SECONDS.sleep(12);
            scheduler.shutdownNow();
        } catch (final InterruptedException e) {
            logger.error("Failed", e.getMessage());
        }
        return scheduler.isShutdown();
    }

    /**
     *
     */
    class DataBundlerRunner implements Runnable {
        private IMetricCollector collectorObj;

        DataBundlerRunner(final IMetricCollector collector, final List<IResultListener> listeners) {
            collectorObj = collector;
        }

        @Override
        public void run() {
            collectorObj.collectData();
        }

    }
}