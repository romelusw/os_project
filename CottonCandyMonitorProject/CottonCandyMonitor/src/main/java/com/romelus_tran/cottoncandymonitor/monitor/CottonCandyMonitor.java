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
 * An object that orchestrates the execution and persistence of {@link IMetricCollector}
 *
 * @author Woody Romelus
 */
public class CottonCandyMonitor {

    private static CottonCandyMonitor instance;
    private final ScheduledExecutorService scheduler;
    private final int SCHEDULER_SHUTDOWN_PERIOD = 12;
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

    /**
     * Verifies that the {@code IMetricCollector} qualifies to be registered into the monitor.
     * If so adds the collector to the scheduling service.
     * @param collector the metric retriever
     * @param listeners the objects that desire the results of the collection
     * @return flag indicating if registration was successful
     */
    public boolean register(final IMetricCollector collector, final List<IResultListener> listeners) {
        boolean retVal = false;

        if(isValidCollector(collector)) {
            logger.debug("Scheduling [{}] to run periodically every [{}] seconds",
                    collector.getClass().getSimpleName(), collector.pollingInterval());
            scheduler.schedule(new DataBundlerRunner(collector, listeners),
                    collector.pollingInterval(), TimeUnit.SECONDS);
            retVal = true;
        }

        return retVal;
    }

    /**
     * Ensures that the {@link IMetricCollector#collectData} method returns a non-null result.
     * @param col the collector
     * @return flag indicating if the collector successfully retrieved data
     */
    private boolean isValidCollector(final IMetricCollector col) {
        final DataBundlerRunner dbr = new DataBundlerRunner(col, null);
        final Thread t = new Thread(dbr);

        try {
            t.run();
            t.join();
        } catch (final InterruptedException e) {
            logger.error("The thread was interrupted.", e.getMessage());
        }
        return (dbr.data != null);
    }

    /**
     * Shuts down the scheduling service.
     * @return flag indicating if service was shut down
     */
    public boolean shutdown() {

        try {
            logger.info("Shutting down scheduling service.");
            scheduler.shutdown();
            TimeUnit.SECONDS.sleep(SCHEDULER_SHUTDOWN_PERIOD);
            scheduler.shutdownNow();
            logger.info("Scheduler service has been terminated successfully.");
        } catch (final InterruptedException e) {
            logger.error("Failed to shutdown scheduling service", e.getMessage());
        }
        return scheduler.isShutdown();
    }

    /**
     *
     */
    class DataBundlerRunner implements Runnable {
        private IMetricCollector collectorObj;
        private List<MetricUnit> data;
        private List<IResultListener> listeners;

        DataBundlerRunner(final IMetricCollector collector, final List<IResultListener> listners) {
            collectorObj = collector;
            listeners = listners;

        }

        @Override
        public void run() {
            data = collectorObj.collectData();
            notifyListeners(data);
        }

        private void notifyListeners(final List<MetricUnit> data) {
            for(final IResultListener listener : listeners) {
                listener.receive(data);
            }
        }

    }
}