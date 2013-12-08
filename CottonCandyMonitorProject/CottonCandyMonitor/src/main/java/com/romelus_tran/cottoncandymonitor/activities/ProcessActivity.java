package com.romelus_tran.cottoncandymonitor.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.romelus_tran.cottoncandymonitor.R;
import com.romelus_tran.cottoncandymonitor.monitor.MonitorUtil;
import com.romelus_tran.cottoncandymonitor.monitor.MonitorUtilException;
import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;
import com.romelus_tran.cottoncandymonitor.monitor.collectors.CPUCollector;
import com.romelus_tran.cottoncandymonitor.monitor.utils.MUUtils;
import com.romelus_tran.cottoncandymonitor.utils.FontUtils;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This activity displays all the pertinent information for a passed in process.
 *
 * Created by Brian on 11/24/13.
 */
public class ProcessActivity extends Activity {

    private final Logger logger = MUUtils.getLogger(ProcessActivity.class);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        String processId = getIntent().getStringExtra("processId");

        TextView textView = (TextView) findViewById(R.id.data);
        textView.setTypeface(FontUtils.loadFontFromAssets(FontUtils.FONT_CAVIAR_DREAMS));
        textView.setMovementMethod(new ScrollingMovementMethod());

        List<MetricUnit> muList = new ArrayList<>();

        try {
            muList = MonitorUtil.getInstance().getData(CPUCollector.class,
                    CPUCollector.GET_PROCESS_INFO,new Object[]{processId},String.class);

            logger.info("Successfully retrieved process information");
        } catch (ExecutionException | InterruptedException | MonitorUtilException e) {
            logger.error(e);
        }

        if (muList != null) {
            textView.setText((String) muList.get(0).getMetricValue());
        }
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
