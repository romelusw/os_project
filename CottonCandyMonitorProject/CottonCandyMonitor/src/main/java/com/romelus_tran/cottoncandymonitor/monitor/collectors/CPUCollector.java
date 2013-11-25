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
public class CPUCollector {
    private ActivityManager am;

    public List<MetricUnit> collectData(final Context context) {
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.getRunningAppProcesses();
        return null;
    }
}