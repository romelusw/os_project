package com.romelus_tran.cottoncandymonitor.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.romelus_tran.cottoncandymonitor.R;
import com.romelus_tran.cottoncandymonitor.graphs.LineGraph;
import com.romelus_tran.cottoncandymonitor.monitor.CottonCandyMonitor;
import com.romelus_tran.cottoncandymonitor.monitor.CottonCandyMonitorException;
import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;
import com.romelus_tran.cottoncandymonitor.monitor.collectors.CPUCollector;
import com.romelus_tran.cottoncandymonitor.utils.CCMUtils;

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

    private final Logger logger = CCMUtils.getLogger(MainActivity.class);
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> processesList;
    TextView numProcesses;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureLogger();

        // TODO: register metrics here:
        // NOTE: CottonCandyMonitor has an error (need to import "StopWatch"), which is why this is
        // commented out.
        // CottonCandyMonitor.getInstance.register(new CPUCollector, <listener>);

        setContentView(R.layout.activity_main);

        // store the TextView that displays total processes running on the device
        numProcesses = (TextView) findViewById(R.id.num_processes);

        // initialize processesList
        processesList = new ArrayList<String>();

        // Create the adapter based on processesList. Now whenever processesList is updated,
        // the adapter will notify the ListView to update.
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, processesList);

        // Get the processesListView and set the arrayAdapter
        ListView processesListView = (ListView) findViewById(R.id.list_processes);
        processesListView.setAdapter(arrayAdapter);

        // Register onClick event for every item in the list.
        // NOTE: This is currently sending a "Toast" to the user at the moment.
        // TODO: The listener should create an intent, which should be passed to the processorView
        processesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedProcess = processesList.get(position);

                Intent intent = new Intent(MainActivity.this, ProcessActivity.class);
                intent.putExtra("processName", selectedProcess);
                startActivity(intent);

//                Toast.makeText(getApplicationContext(), "Process Selected: " + selectedProcess,
//                        Toast.LENGTH_LONG).show();
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
                // refresh the list
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
        //TODO: Replace this with an actual fetch to the real process list. populate accordingly
        for (int i = 0; i < 100; ++i) {
            processesList.add(getRandomProcess());
        }

        numProcesses.setText(getResources().getString(R.string.num_processes) + processesList.size());
        arrayAdapter.notifyDataSetChanged();
    }

    /**
     * This starts a new activity displaying a demo line graph.
     * TODO: Update the line graph to show real data and consider displaying the graph in MainActivity
     * @param view The button that triggers this event.
     */
    public void lineGraphHandler(final View view) {
        LineGraph line = new LineGraph();
        Intent lineIntent = line.getIntent(getApplicationContext());
        startActivity(lineIntent);
    }

    /**
     * Test function to populate processes list.
     * TODO: Remove this function when we get real data.
     */
    private String getRandomProcess() {
        String[] s = new String[]{"Jet Pack Joyride", "Chrome", "Facebook", "Fruit Ninja", "Internet"};
        return s[(int) Math.floor(Math.random() * s.length)];
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