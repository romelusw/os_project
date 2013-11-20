package com.romelus_tran.cottoncandymonitor.monitor;

/**
 * An object that orchestrates the execution and persistence of {@code com.romelus_tran.cottoncandymonitor.monitor.IDataBundler}
 */
public class CottonCandyMonitor {

    private static CottonCandyMonitor instance;

    /**
     * Default Constructor.
     */
    private CottonCandyMonitor() {
        // Instantiation not allowed
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

    public boolean register(final IDataBundler applicant) {
        return true;
    }

    /**
     *
     */
    class DataBuilderRunner implements Runnable {

        @Override
        public void run() {

        }

    }
}