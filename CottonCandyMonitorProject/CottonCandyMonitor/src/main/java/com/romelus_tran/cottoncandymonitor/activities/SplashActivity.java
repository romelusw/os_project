package com.romelus_tran.cottoncandymonitor.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.romelus_tran.cottoncandymonitor.R;
import com.romelus_tran.cottoncandymonitor.utils.FontUtils;

/**
 * This displays the icon and after a few seconds it transitions into the main activity
 * Created by Brian on 12/8/13.
 */
public class SplashActivity extends Activity {

    // splash screen duration
    private static int SPLASH_DURATION = 1500; // seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        FontUtils.getContext(getApplicationContext());

        // set correct font
        ((TextView) findViewById(R.id.splash_title)).setTypeface(
                FontUtils.loadFontFromAssets(FontUtils.FONT_CAVIAR_DREAMS_BOLD));

        new Handler().postDelayed(new Runnable() {
            // Showing splash screen with a timer. This will be useful when you
            // want to show case your app logo / company
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_DURATION);
    }
}
