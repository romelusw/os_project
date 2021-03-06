package com.romelus_tran.cottoncandymonitor.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.romelus_tran.cottoncandymonitor.R;
import com.romelus_tran.cottoncandymonitor.adapters.ProcessesAdapter;
import com.romelus_tran.cottoncandymonitor.dialogs.AboutDialogFragment;
import com.romelus_tran.cottoncandymonitor.graphs.CPUUsageGraph;
import com.romelus_tran.cottoncandymonitor.monitor.MonitorUtil;
import com.romelus_tran.cottoncandymonitor.monitor.MonitorUtilException;
import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;
import com.romelus_tran.cottoncandymonitor.monitor.collectors.CPUCollector;
import com.romelus_tran.cottoncandymonitor.monitor.listeners.CPUUsageListener;
import com.romelus_tran.cottoncandymonitor.monitor.listeners.IResultListener;
import com.romelus_tran.cottoncandymonitor.monitor.utils.MUUtils;
import com.romelus_tran.cottoncandymonitor.utils.FontUtils;
import com.romelus_tran.cottoncandymonitor.monitor.utils.Pair;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * The primary activity that displays the graph button and the current processes
 * running on the device. This activity extends ActionBarActivity, allowing for
 * a menu panel to be displayed whenever the menu button is selected.
 *
 * @author Woody Romelus, Brian Tran
 */
public class MainActivity extends ActionBarActivity {

    private static final int POLLING_INTERVAL = 1;
    private final Logger logger = MUUtils.getLogger(MainActivity.class);

    private MonitorUtil _mu = MonitorUtil.getInstance();

    private ProcessesAdapter _processesAdapter;
    private List<MetricUnit> _processesList;
    private TextView _numProcesses;

    private CPUUsageGraph _cpuUsageGraph;

    private AboutDialogFragment _aboutDialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // configureLogger(); Disable for deployment

        CPUCollector cpuCollector = new CPUCollector();

        if (_mu.register(cpuCollector)) {
            logger.info("Successfully registered CPUCollector");
        } else {
            logger.error("Collector was not registered or is already registered");
        }

        setContentView(R.layout.activity_main);

        // create cpu usage graph and setup listener with polling
        _cpuUsageGraph = new CPUUsageGraph();
        List<IResultListener> listeners = new ArrayList<>();
        listeners.add(new CPUUsageListener(_cpuUsageGraph));
        _mu.registerPollingCollector(cpuCollector, listeners, POLLING_INTERVAL);

        // add the gpu view to the chart
        ((LinearLayout) findViewById(R.id.chart)).addView(_cpuUsageGraph.getView(this));

        ((TextView) findViewById(R.id.cpu_usage_history)).setTypeface(
                FontUtils.loadFontFromAssets(FontUtils.FONT_CAVIAR_DREAMS));

        // store the TextView that displays total processes running on the device
        _numProcesses = (TextView) findViewById(R.id.num_processes);
        _numProcesses.setTypeface(FontUtils.loadFontFromAssets(FontUtils.FONT_CAVIAR_DREAMS));

        // updating all headers with the correct typeface
        ((TextView) findViewById(R.id.header_process_icon)).setTypeface(
                FontUtils.loadFontFromAssets(FontUtils.FONT_CAVIAR_DREAMS));

        ((TextView) findViewById(R.id.header_process_name)).setTypeface(
                FontUtils.loadFontFromAssets(FontUtils.FONT_CAVIAR_DREAMS));

        ((TextView) findViewById(R.id.header_process_id)).setTypeface(
                FontUtils.loadFontFromAssets(FontUtils.FONT_CAVIAR_DREAMS));

        // initialize _processesList
        _processesList = new ArrayList<>();

        // Create the adapter based on _processesList. Now whenever _processesList is updated,
        // the adapter will notify the ListView to update.
        _processesAdapter = new ProcessesAdapter(this, R.layout.processes_list, _processesList);

        // Get the processesListView and set the arrayAdapter
        ListView processesListView = (ListView) findViewById(R.id.list_processes);
        processesListView.setAdapter(_processesAdapter);

        // Register onClick event for every item in the list.
        processesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MetricUnit selectedProcess = _processesList.get(position);

                Intent intent = new Intent(MainActivity.this, ProcessActivity.class);
                Pair<Drawable, String> pair = (Pair) selectedProcess.getMetricValue();
                intent.putExtra("processId", pair.getRight());

                startActivity(intent);
                overridePendingTransition(R.anim.abc_slide_in_bottom, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu. This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_about:
                if (_aboutDialog == null) {
                    _aboutDialog = new AboutDialogFragment();
                }

                _aboutDialog.show(getFragmentManager(), "about");
                return true;

            case R.id.refresh_list:
                refreshList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Update the processes list here and notify the adapter that the list has changed.
     */
    private void refreshList() {
        _processesList.clear();

        try {
            // Instead of re-setting _processesList to getData, we just add all contents from getData
            _processesList.addAll(_mu.getData(CPUCollector.class,
                    CPUCollector.GET_RUNNING_PROCESSES, new Object[]{this.getApplicationContext()}, Context.class));

            logger.info("refreshList(): Successfully received data");
        } catch (ExecutionException | InterruptedException | MonitorUtilException e) {
            logger.error("refreshList():", e);
        }

        // notify the adapter that we just updated the processes list
        _processesAdapter.notifyDataSetChanged();

        // update the text showing how many processes are running
        _numProcesses.setText(getResources().getString(R.string.num_processes) + _processesList.size());
    }

    /**
     * Configures the logger system with default settings.
     */
    public void configureLogger() {
//        final LogConfigurator logConfigurator = new LogConfigurator();
//
//        logConfigurator.setRootLevel(Level.ERROR);
//        // Set log level of a specific logger
//        logConfigurator.setLevel("org.apache.log4j.Logger", Level.ERROR);
//        logConfigurator.configure();
        logger.info("Logger is configured.");
    }
}
