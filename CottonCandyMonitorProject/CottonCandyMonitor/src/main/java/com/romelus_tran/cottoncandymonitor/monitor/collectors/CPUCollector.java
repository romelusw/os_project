package com.romelus_tran.cottoncandymonitor.monitor.collectors;

import android.app.ActivityManager;
import android.content.Context;

import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;

import java.util.List;

/**
 * An Object capable of retrieving cpu related metrics.
 *
 * @author Woody Romelus
 */
public class CPUCollector implements IMetricCollector {
    private int pollingPeriod = 2;
    private ActivityManager am;


    @Override
    public List<MetricUnit> collectData(final Context context) {
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.getRunningAppProcesses();
        return null;
    }

    @Override
    public int pollingInterval() {
        return pollingPeriod;
    }
}