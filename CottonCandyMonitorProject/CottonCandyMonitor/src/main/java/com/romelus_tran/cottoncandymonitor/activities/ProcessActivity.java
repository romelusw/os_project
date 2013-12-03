package com.romelus_tran.cottoncandymonitor.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.romelus_tran.cottoncandymonitor.R;
import com.romelus_tran.cottoncandymonitor.monitor.CottonCandyMonitor;
import com.romelus_tran.cottoncandymonitor.monitor.CottonCandyMonitorException;
import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;
import com.romelus_tran.cottoncandymonitor.monitor.collectors.CPUCollector;
import com.romelus_tran.cottoncandymonitor.utils.CCMUtils;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Brian on 11/24/13.
 */
public class ProcessActivity extends Activity {

    private final Logger logger = CCMUtils.getLogger(ProcessActivity.class);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        Intent intent = getIntent();
        String processName = intent.getStringExtra("processName");
        int processId = intent.getIntExtra("processId", 0);

        TextView textView = (TextView) findViewById(R.id.data);
        textView.setText(processName + " is running on your device!");

        // TODO: Uncomment code below once we get real pIds
//        List<MetricUnit> muList = new ArrayList<>();
//
//        try {
//            // Instead of re-setting processesList to getData, we just add all contents from getData
//            muList = CottonCandyMonitor.getInstance().getData(CPUCollector.class,
//                    "getProcessInfo",new Object[]{processId},String.class);
//
//            logger.info("Successfully retrieved process information");
//        } catch (ExecutionException | InterruptedException | CottonCandyMonitorException e) {
//            logger.error(e);
//        }
//
//        if (muList != null) {
//            textView.setText((String) muList.get(0).getMetricValue());
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(0, R.anim.abc_slide_out_bottom);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
