package com.romelus_tran.cottoncandymonitor.activities;

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
import com.romelus_tran.cottoncandymonitor.graphs.CPUUsageGraph;
import com.romelus_tran.cottoncandymonitor.graphs.Point;
import com.romelus_tran.cottoncandymonitor.monitor.CottonCandyMonitor;
import com.romelus_tran.cottoncandymonitor.monitor.CottonCandyMonitorException;
import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;
import com.romelus_tran.cottoncandymonitor.monitor.collectors.CPUCollector;
import com.romelus_tran.cottoncandymonitor.utils.CCMUtils;
import com.romelus_tran.cottoncandymonitor.utils.Pair;

import org.achartengine.GraphicalView;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    private final Logger logger = CCMUtils.getLogger(MainActivity.class);
    ProcessesAdapter processesAdapter;
    List<MetricUnit> processesList;
    TextView numProcesses;
    CPUUsageGraph cpuUsageGraph;

    GraphicalView cpuUsageView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureLogger();

        if (CottonCandyMonitor.getInstance().register(new CPUCollector())) {
            logger.info("Successfully registered CPUCollector");
        } else {
            logger.error("Collector was not registered");
        }

        setContentView(R.layout.activity_main);

        cpuUsageGraph = new CPUUsageGraph();
        cpuUsageView = cpuUsageGraph.getView(this);
        ((LinearLayout) findViewById(R.id.chart)).addView(cpuUsageView);

        // store the TextView that displays total processes running on the device
        numProcesses = (TextView) findViewById(R.id.num_processes);

        // initialize processesList
        processesList = new ArrayList<>();

        // Create the adapter based on processesList. Now whenever processesList is updated,
        // the adapter will notify the ListView to update.
        processesAdapter = new ProcessesAdapter(this, R.layout.processes_list, processesList);

        // Get the processesListView and set the arrayAdapter
        ListView processesListView = (ListView) findViewById(R.id.list_processes);
        processesListView.setAdapter(processesAdapter);

        // Register onClick event for every item in the list.
        processesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MetricUnit selectedProcess = processesList.get(position);

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
            case R.id.action_settings:
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
        processesList.clear();

        try {
            // Instead of re-setting processesList to getData, we just add all contents from getData
            processesList.addAll(CottonCandyMonitor.getInstance().getData(CPUCollector.class,
                    "getRunningProcesses", new Object[]{this.getApplicationContext()}, Context.class));
            logger.info("refreshList(): Successfully received data");
        } catch (ExecutionException | InterruptedException | CottonCandyMonitorException e) {
            logger.error("refreshList():", e);
        }

        // notify the adapter that we just updated the processes list
        processesAdapter.notifyDataSetChanged();

        // update the text showing how many processes are running
        numProcesses.setText(getResources().getString(R.string.num_processes) + processesList.size());

        // NOTE: The following will be placed inside a listener class
        // TODO: Setup a listener to do the following: get the data and call setGraph passing the appropriate data and then repaint
        cpuUsageGraph.setGraph(getRandomData()); // getRandomData() is a fake call to populate a random array
        cpuUsageView.repaint();
    }
	
	private ArrayList<Point> getRandomData() {
        ArrayList<Point> retVal = new ArrayList<>();

        Random r = new Random();

        for (int i = 0; i < 20; ++i) {
            retVal.add(new Point(i, r.nextInt(100)));
        }

        return retVal;
    }

    /**
     * Configures the logger system with default settings.
     */
    public void configureLogger() {
        final LogConfigurator logConfigurator = new LogConfigurator();

        logConfigurator.setFileName(Environment.getExternalStorageDirectory()
                + File.separator + getResources().getString(R.string.app_name)
                .toLowerCase().replace(" ", "_") + ".log");
        logConfigurator.setRootLevel(Level.DEBUG);
        // Set log level of a specific logger
        logConfigurator.setLevel("org.apache.log4j.Logger", Level.ERROR);
        logConfigurator.configure();
        logger.info("Logger is configured.");
    }
}