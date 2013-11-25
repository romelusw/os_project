package com.romelus_tran.cottoncandymonitor.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.romelus_tran.cottoncandymonitor.R;

/**
 * Created by Brian on 11/24/13.
 */
public class ProcessActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        Intent intent = getIntent();
        String processName = intent.getStringExtra("processName");

        TextView textView = (TextView) findViewById(R.id.data);
        textView.setText(processName + " is running on your device!");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
