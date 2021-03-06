package com.romelus_tran.cottoncandymonitor.monitor;

import android.content.Context;

import com.romelus_tran.cottoncandymonitor.monitor.collectors.IMetricCollector;
import com.romelus_tran.cottoncandymonitor.monitor.listeners.IResultListener;
import com.romelus_tran.cottoncandymonitor.monitor.listeners.ResultListenerResponse;
import com.romelus_tran.cottoncandymonitor.monitor.utils.MUUtils;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An object that orchestrates the execution and persistence of
 * {@link IMetricCollector}.
 *
 * @author Woody Romelus
 */
public class MonitorUtil {

    private final Logger logger = MUUtils.getLogger(MonitorUtil.class);
    private final int threadPoolSize = 1;
    private final int shutdownWaitPeriod = 12;
    private static MonitorUtil instance;
    private ScheduledExecutorService scheduler;
    private ExecutorService executors;
    private final Set<String> registered = new HashSet<>();
    private Context context;

    /**
     * Default Constructor.
     */
    private MonitorUtil() {
        // Instantiation not allowed
    }

    /**
     * Returns the static reference to the singleton instance.
     *
     * @return the singleton
     */
    public static MonitorUtil getInstance() {
        if (instance == null) {
            instance = new MonitorUtil();
        }
        return instance;
    }

    /**
     * Verifies that the {@code IMetricCollector} qualifies to be registered
     * into the monitor. If so adds the collector to the registered list.
     *
     * @param collector the metric retriever
     * @return flag indicating if registration was successful
     */
    public boolean register(final IMetricCollector collector) {
        boolean retVal = false;
        if (isValidCollector(collector)) {
            if (executors == null) {
                logger.info("Starting the single executor.");
                executors = Executors.newSingleThreadExecutor();
            }
            if (registered.add(collector.getClass().getSimpleName())) {
                retVal = true;
            } else {
                logger.warn("The collector ["
                        + collector.getClass().getSimpleName()
                        + "] is already registered!");
            }
        }
        return retVal;
    }

    /**
     * Verifies that the {@code IMetricCollector} qualifies to be registered
     * into the monitor. If so adds the collector to the scheduling service.
     *
     * @param collector       the metric retriever
     * @param listeners       the objects that desire the results of the collection
     * @param pollingInterval the polling intervals (in seconds)
     * @return flag indicating if registration was successful
     */
    public boolean registerPollingCollector(final IMetricCollector collector,
                                            final List<IResultListener> listeners,
                                            final int pollingInterval) {
        boolean retVal = false;
        if (isValidCollector(collector) && listeners != null) {
            if (scheduler == null) {
                logger.info("Starting scheduler with thread pool size of ["
                        + threadPoolSize + "]");
                scheduler = Executors.newScheduledThreadPool(threadPoolSize);
            }
            logger.debug("Scheduling [" + collector.getClass().getSimpleName()
                    + "] to run periodically every [" + pollingInterval + "]"
                    + " seconds");
            scheduler.scheduleAtFixedRate(new DataBundlerRunner(collector, listeners),
                    0, pollingInterval, TimeUnit.SECONDS);
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
        boolean retVal = false;
        if (col != null) {
            final DataBundlerRunner dbr = new DataBundlerRunner(col, null);
            final Thread t = new Thread(dbr);

            try {
                t.run();
                t.join(); // Wait for thread to complete
            } catch (final InterruptedException e) {
                logger.error("The thread was interrupted.", e);
            }
            retVal = (!dbr.data.isEmpty());
        }
        return retVal;
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

    /**
     * Invokes and retrieves data metrics for a registered collector.
     *
     * @param regCollector the collector class
     * @param methodName   the method to invoke
     * @param args         the arguments the method takes
     * @param argTypes     the argument class types
     * @return the data that was collected for the method invocation
     * @throws ExecutionException          the task was aborted prior to retrieving
     *                                     its results
     * @throws InterruptedException        if the waiting thread was interrupted
     * @throws MonitorUtilException if the collector class was not
     *                                     already registered prior to calling this method
     */
    public List<MetricUnit> getData(final Class<?> regCollector,
                                    final String methodName,
                                    final Object[] args,
                                    final Class... argTypes)
            throws ExecutionException, InterruptedException,
            MonitorUtilException {

        if (registered.contains(regCollector.getSimpleName())) {
            List<MetricUnit> retVal;
            Future<List<MetricUnit>> f = null;
            try {
                final Class cls = Class.forName(regCollector.getName());
                final Object obj = cls.newInstance();
                final Method m = cls.getDeclaredMethod(methodName, argTypes);
                f = executors.submit(
                        new Callable<List<MetricUnit>>() {

                            @Override
                            public List<MetricUnit> call() throws Exception {
                                List<MetricUnit> result = null;
                                result = (List<MetricUnit>) m.invoke(obj, args);
                                return result;
                            }
                        }
                );
            } catch (final ClassNotFoundException e) {
                logger.error("Could not find class ["
                        + regCollector.getSimpleName() + "] to create.", e);
            } catch (final NoSuchMethodException e) {
                logger.error("Could not invoke method [" + methodName + "].", e);
            } catch (final InstantiationException e) {
                logger.error("Could not instantiate ["
                        + regCollector.getSimpleName() + "].", e);
            } catch (final IllegalAccessException e) {
                logger.error("We do not have access using reflection.", e);
            }
            return f.get();
        } else {
            throw new MonitorUtilException("Collector: ["
                    + regCollector.getSimpleName()
                    + "] does not exist, please register it.");
        }
    }

    /**
     * Shuts down the scheduling service.
     *
     * @return flag indicating if services were shut down
     */
    public boolean shutdown() {

        try {
            logger.info("Shutting down the thread services.");
            scheduler.shutdown();
            executors.shutdown();
            TimeUnit.SECONDS.sleep(shutdownWaitPeriod);
            scheduler.shutdownNow();
            executors.shutdownNow();
            logger.info("Thread services have been terminated successfully.");
        } catch (final InterruptedException e) {
            logger.error("Failed shutting down thread service", e);
        }
        return scheduler.isShutdown() && executors.isShutdown();
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
                if(listenersList != null) {
                    notifyListeners(data); // Wait until receiver handles data
                }
                sw.stop();
                logger.info("Data Collection completed. Start time: ["
                        + sw.getStartTime() + " ms],"
                        + " Collection Time: ["
                        + (sw.getSplitTime() - sw.getStartTime())
                        + "ms] Total  time: [" + sw.getTime() + "ms]");
            } catch (final MonitorUtilException e) {
                logger.error("Could not send data to listener(s).", e);
            }
        }

        /**
         * Sends the acquired data to the listeners.
         *
         * @param dataObj the data to persist
         * @throws MonitorUtilException when the listeners can not
         *                                     be reached
         */
        private void notifyListeners(final List<MetricUnit> dataObj)
                throws MonitorUtilException {
            if (listenersList != null) {
                for (final IResultListener listener : listenersList) {
                    final ResultListenerResponse r = listener.receive(dataObj);
                    logger.info("[" + listener.getClass().getSimpleName()
                            + "] :: [" + r.getMessage() + "]");
                }
            } else {
                throw new MonitorUtilException("Listener list is null.");
            }
        }
    }
}