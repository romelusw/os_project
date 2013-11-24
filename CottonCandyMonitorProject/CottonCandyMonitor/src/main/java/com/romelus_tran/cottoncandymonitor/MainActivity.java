package com.romelus_tran.cottoncandymonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.romelus_tran.cottoncandymonitor.graphs.LineGraph;

import java.util.ArrayList;

/**
 * The primary activity that displays the graph button and the current processes running on the
 * device. This activity extends ActionBarActivity, allowing for a menu panel to be displayed
 * whenever the menu button is selected.
 */
public class MainActivity extends ActionBarActivity {

    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> processesList;
    TextView numProcesses;

    /**
     * Construct the MainActivity. This is where we get the
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: register metrics here:
        // NOTE: CottonCandyMonitor has an error (need to import "StopWatch"), which is why this is
        // commented out.
        // CottonCandyMonitor.getInstance.register(new CPUCollector, <listener>);

        setContentView(R.layout.activity_main);

        // store the TextView that displays total processes running on the device
        numProcesses = (TextView)findViewById(R.id.num_processes);

        // initialize processesList
        processesList = new ArrayList<String>();

        // Create the adapter based on processesList. Now whenever processesList is updated,
        // the adapter will notify the ListView to update.
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, processesList);

        // Get the processesListView and set the arrayAdapter
        ListView processesListView = (ListView)findViewById(R.id.list_processes);
        processesListView.setAdapter(arrayAdapter);

        // Register onClick event for every item in the list.
        // NOTE: This is currently sending a "Toast" to the user at the moment.
        // TODO: The listener should create an intent, which should be passed to the processorView
        processesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedProcess = processesList.get(position);
                Toast.makeText(getApplicationContext(), "Process Selected: " + selectedProcess,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Whenever we return to MainActivity, onResume() will be called. Call refreshList()..
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    /**
     * Create the main menu.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu. This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        processesList.add(getRandomProcess());
        numProcesses.setText("Total Running Processes: " + processesList.size());
        arrayAdapter.notifyDataSetChanged();
    }

    /**
     * This starts a new activity displaying a demo line graph.
     * TODO: Update the line graph to show real data and consider displaying the graph in MainActivity
     * @param view
     */
    public void lineGraphHandler(View view) {
        LineGraph line = new LineGraph();
        Intent lineIntent = line.getIntent(this);
        startActivity(lineIntent);
    }

    /**
     * Test function to populate processes list.
     * TODO: Remove this function when we get real data.
     */
    private String getRandomProcess() {
        String[] s = new String[]{"Jet Pack Joyride", "Chrome", "Facebook", "Fruit Ninja", "Internet"};
        return s[(int)Math.round(Math.random()* s.length)];
    }
}