package com.romelus_tran.cottoncandymonitor.monitor.collectors;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;
import com.romelus_tran.cottoncandymonitor.utils.CCMConstants;
import com.romelus_tran.cottoncandymonitor.utils.CCMUtils;
import com.romelus_tran.cottoncandymonitor.utils.Pair;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object capable of retrieving cpu related metrics.
 *
 * @author Woody Romelus
 */
public class CPUCollector implements IMetricCollector {
    private static final Logger logger = CCMUtils.getLogger(CPUCollector.class);
    private final Runtime ENV = Runtime.getRuntime();
    private final String USAGE_CMD = "top -m 1 -n 1 -d .01";
    private final String PROC_INFO = "echo \"$(cat /proc/{0}/status)\"";
    private final String SHELL_CMD = "/system/bin/sh";

    @Override
    public List<MetricUnit> collectData(final Context context) {
        final List<MetricUnit> retVal = new ArrayList<>();
        retVal.addAll(getCPUUsage());
        retVal.addAll(getRunningProcesses(context));
        return retVal;
    }

    /**
     * Retrieves the process status information provided by reading the linux
     * /proc/{pid}/status file.
     *
     * @param pid the process id
     * @return the status info
     */
    public List<MetricUnit> getProcessInfo(final String pid) {
        List<MetricUnit> retVal = new ArrayList<>();
        if (!pid.isEmpty()) {
            final MessageFormat mf = new MessageFormat(PROC_INFO);
            final String command = mf.format(new Object[] {pid});
            Pair<Boolean, String> results = executeCommand(
                    new String[] {SHELL_CMD, "-c", command});

            if (results.getLeft()) {
                retVal.add(new MetricUnit(new Date(System.currentTimeMillis()),
                        CCMConstants.PROC_INFO, results.getRight(), pid));
            } else {
                logger.error("Failed to collect process info. "
                        + results.getRight());
            }
        }
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
            final Pattern p = Pattern.compile("(^.*(?=User))");
            final Matcher m = p.matcher(results.getRight());

            if (m.find()) {
                retVal = convertUsageToMetricUnit(m.group(1));
            }
        } else {
            logger.error("Failed to collect CPU Usage. " + results.getRight());
        }
        return retVal;
    }

    /**
     * Returns a list of running processes, as well as their icon, pid and label.
     *
     * @param context the application context
     * @return the list of processes
     */
    public List<MetricUnit> getRunningProcesses(final Context context) {
        final List<MetricUnit> retVal = new ArrayList<>();
        if (context != null) {
            final ActivityManager am = (ActivityManager)
                    context.getSystemService(Context.ACTIVITY_SERVICE);
            final PackageManager pm = context.getPackageManager();
            final List<ActivityManager.RunningAppProcessInfo> rApps =
                    am.getRunningAppProcesses();
            ApplicationInfo ai;

            for (final ActivityManager.RunningAppProcessInfo info : rApps) {
                final String processName = info.processName;
                try {
                    ai = pm.getApplicationInfo(processName, 0);
                    retVal.add(new MetricUnit(new Date(System.currentTimeMillis()),
                            CCMConstants.PROCESSES_ID,
                            new Pair<>(pm.getApplicationIcon(processName),
                                    String.valueOf(info.pid)),
                            (String) pm.getApplicationLabel(ai)));
                } catch (final PackageManager.NameNotFoundException e) {
                    logger.warn("Could not find package. ", e);
                }
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