package com.romelus_tran.cottoncandymonitor.monitor.collectors;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;
import com.romelus_tran.cottoncandymonitor.utils.CCMConstants;
import com.romelus_tran.cottoncandymonitor.utils.CCMUtils;
import com.romelus_tran.cottoncandymonitor.utils.Pair;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * An object capable of retrieving cpu related metrics.
 *
 * @author Woody Romelus
 */
public class CPUCollector implements IMetricCollector {
    private static final Logger logger = CCMUtils.getLogger(CPUCollector.class);
    private final Runtime ENV = Runtime.getRuntime();
    private final String USAGE_CMD = "top -m 1 -n 1 -d .01 | grep User.*System";
    private final String SHELL_CMD = "/system/bin/sh";

    @Override
    public List<MetricUnit> collectData(final Context context) {
        final List<MetricUnit> retVal = new ArrayList<>();
        retVal.addAll(getCPUUsage());
        return retVal;
    }

    /**
     * Retrieves the time the CPU has spent processing instructions.
     * @see <a href="http://linux.die.net/man/1/top">top manual page</a>
     *
     * @return the usage metrics collected
     */
    public List<MetricUnit> getCPUUsage() {
        List<MetricUnit> retVal = new ArrayList<>();
        Pair<Boolean, String> results = executeCommand(
                new String[] {SHELL_CMD, "-c", USAGE_CMD});

        if (results.getLeft()) {
            retVal = convertUsageToMetricUnit(results.getRight());
        } else {
            logger.error("Failed to collect CPU Usage. " + results.getRight());
        }
        return retVal;
    }

    /**
     * Returns a list of running processes, as well as their icon and label.
     *
     * @param context the application context
     * @return the list of processes
     */
    public List<MetricUnit> getRunningProcesses(final Context context) {
        final ActivityManager am = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        final PackageManager pm = context.getPackageManager();
        final List<MetricUnit> retVal = new ArrayList<>();
        ApplicationInfo ai;

        for (final ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            final String processName = info.processName;
            try {
                ai = pm.getApplicationInfo(processName, 0);
                retVal.add(new MetricUnit(new Date(System.currentTimeMillis()),
                CCMConstants.PROCESSES_ID, pm.getApplicationIcon(processName),
                        (String) pm.getApplicationLabel(ai)));
            } catch (final PackageManager.NameNotFoundException e) {
                logger.warn("Could not find package. ", e);
            }
        }
        return retVal;
    }

    /**
     * Translates the raw output from the <em>top</em> shell command into
     * an list of {@link MetricUnit}.
     *
     * @param usageOutput the output from the top command
     * @return the results represented by a list of {@link MetricUnit}
     */
    private List<MetricUnit> convertUsageToMetricUnit(final String usageOutput) {
        final List<MetricUnit> retVal = new ArrayList<>();

        if (!usageOutput.isEmpty()) {
            final String[] dataPoints = usageOutput.split(",");
            for (String metric : dataPoints) {
                String[] data;
                metric = metric.trim().replace("%", "");
                data = metric.split(" ");
                retVal.add(new MetricUnit(new Date(System.currentTimeMillis()),
                        CCMConstants.USAGE_ID, (Float.parseFloat(data[1]) / 100),
                        data[0]));
            }
        }
        return retVal;
    }

    /**
     * Executes a command via a new process to the native OS.
     *
     * @param cmd the array of arguments (including the command)
     * @return a {@link Pair} containing a boolean to indicate if the process
     * failed to execute, as well as the raw output from the execution
     */
    private Pair<Boolean, String> executeCommand(final String[] cmd) {
        Pair<Boolean, String> retVal = null;
        if (cmd.length != 0) {
            try {
                logger.info("Preparing to execute command ['"
                        + Arrays.toString(cmd) + "']");
                final Process p = ENV.exec(cmd);
                p.waitFor(); // Ensure process terminates before continuing
                if (p.exitValue() != 0) {
                    retVal = new Pair<>(false,
                            CCMUtils.convertStreamToString(p.getErrorStream()));
                } else {
                    retVal = new Pair<>(true,
                            CCMUtils.convertStreamToString(p.getInputStream()));
                }
            } catch (final IOException e) {
                logger.error("The command could not be executed.", e);
            } catch (final InterruptedException e) {
                logger.error("The executing process was interrupted.", e);
            }
        }
        return retVal;
    }
}