package com.romelus_tran.cottoncandymonitor.monitor;

import android.content.Context;

import com.romelus_tran.cottoncandymonitor.monitor.collectors.IMetricCollector;
import com.romelus_tran.cottoncandymonitor.monitor.listeners.IResultListener;
import com.romelus_tran.cottoncandymonitor.utils.CCMUtils;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An object that orchestrates the execution and persistence of
 * {@link IMetricCollector}.
 *
 * @author Woody Romelus
 */
public class CottonCandyMonitor {

    private static CottonCandyMonitor instance;
    private final ScheduledExecutorService scheduler;
    private final int schedluerShutdownPeriod = 12;
    private final Logger logger = CCMUtils.getLogger(CottonCandyMonitor.class);
    private Context context;
    private int threadPoolSize = 1;

    /**
     * Default Constructor.
     */
    private CottonCandyMonitor() {
        // Instantiation not allowed
        logger.info("Starting Monitor with thread pool size of [{}]",
                threadPoolSize);
        scheduler = Executors.newScheduledThreadPool(threadPoolSize);
    }

    /**
     * Returns the static reference to the singleton instance.
     *
     * @return the singleton
     */
    public static CottonCandyMonitor getInstance() {
        if (instance == null) {
            instance = new CottonCandyMonitor();
        }
        return instance;
    }

    /**
     * Verifies that the {@code IMetricCollector} qualifies to be registered
     * into the monitor. If so adds the collector to the scheduling service.
     *
     * @param collector the metric retriever
     * @param listeners the objects that desire the results of the collection
     * @return flag indicating if registration was successful
     */
    public boolean register(final IMetricCollector collector,
                            final List<IResultListener> listeners) {
        boolean retVal = false;

        if (collector != null && isValidCollector(collector)) {
            logger.debug("Scheduling [{}] to run periodically every [{}]"
                    + " seconds", collector.getClass().getSimpleName(),
                    collector.pollingInterval());
            scheduler.schedule(new DataBundlerRunner(collector, listeners),
                    collector.pollingInterval(), TimeUnit.SECONDS);
            retVal = true;
        }

        return retVal;
    }

    /**
     * Ensures that the {@link IMetricCollector#collectData} method returns a
     * non-null result.
     *
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
     *
     * @return flag indicating if service was shut down
     */
    public boolean shutdown() {

        try {
            logger.info("Shutting down the scheduling service.");
            scheduler.shutdown();
            TimeUnit.SECONDS.sleep(schedluerShutdownPeriod);
            scheduler.shutdownNow();
            logger.info("Scheduler service has been terminated successfully.");
        } catch (final InterruptedException e) {
            logger.error("Failed shutting down scheduling service",
                    e.getMessage());
        }
        return scheduler.isShutdown();
    }

    /**
     * A worker that executes the collection and persistence of the
     * {@code IMetricCollector} objects.
     */
    class DataBundlerRunner implements Runnable {
        private IMetricCollector collectorObj;
        private List<MetricUnit> data;
        private List<IResultListener> listenersList;

        /**
         * Default Constructor.
         *
         * @param collector the collector
         * @param listeners the list of listeners to send data to
         */
        DataBundlerRunner(final IMetricCollector collector,
                          final List<IResultListener> listeners) {
            collectorObj = collector;
            listenersList = listeners;
        }

        @Override
        public void run() {
            final StopWatch sw = new StopWatch();

            try {
                sw.start();
                data = collectorObj.collectData(getContext());
                sw.split();
                notifyListeners(data);
                sw.stop();
                logger.info("Start time: [{}], Collection Time: [{}] Total"
                        + " time: [{}]", sw.getStartTime(),
                        sw.getStartTime() - sw.getSplitTime(), sw.getTime());
            } catch (final CottonCandyMonitorException e) {
                logger.error("Could not send data to listener(s).",
                        e.getMessage());
            }
        }

        /**
         * Sends the acquired data to the listeners.
         *
         * @param dataObj the data to persist
         * @throws CottonCandyMonitorException when the listeners can not
         *                                     be reached
         */
        private void notifyListeners(final List<MetricUnit> dataObj)
                throws CottonCandyMonitorException {
            if (listenersList != null) {
                for (final IResultListener listener : listenersList) {
                    logger.info("[{}] :: [{}]", listener.getClass().getSimpleName(),
                            listener.receive(dataObj).getMessage());
                }
            } else {
                throw new CottonCandyMonitorException("Listener list is null.");
            }
        }
    }

    /**
     * Getter for the context.
     *
     * @return the context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Setter for the context.
     *
     * @param ctx the context to set
     */
    public void setContext(final Context ctx) {
        // To prevent memory leaks we attain the context for the application
        // which will only exist as long as the application's life.
        // Afterward it will be be out of scope, which qualifies it for
        // garbage collection.
        context = ctx.getApplicationContext();
    }
}