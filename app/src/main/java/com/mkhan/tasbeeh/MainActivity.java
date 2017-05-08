package com.mkhan.tasbeeh;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView  textViewCounter;
    private Button btnAdd;
    private Button btnSubstract;
    private Button btnReset;
    private int counterValue = 0;

    private Configuration config;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        config = getResources().getConfiguration();
        /*width = config.screenWidthDp;
        height = config.screenHeightDp;*/

        prepareSharedPreference();

        textViewCounter = (TextView) findViewById(R.id.textViewCounter);
        readCounterFromSharedPref();
        updateCounterUI();

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementCounter();
            }
        });

        btnSubstract = (Button) findViewById(R.id.btnMinus);
        btnSubstract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementCounter();
            }
        });


        btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         //Handle item selection
        switch (item.getItemId()) {
            case R.id.action_rate_app:
                System.out.println("Mohseen : Rate App Clicked ");
                rateApp();
                return true;
            case R.id.action_settings:
                System.out.println("Mohseen : Action setting Clicked ");
                this.startActivity(new Intent(this,SettingsActivity.class));
                return true;
            default:
                this.startActivity(new Intent(this,SettingsActivity.class));
                return true;
        }
    }

    private void rateApp(){
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    private void reset() {
        counterValue = 0;
        updateCounterUI();
    }

    public void updateCounterUI(){
        textViewCounter.setText(String.valueOf(counterValue));

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.counter_value), counterValue);
        editor.commit();
    }

    public void incrementCounter() {
        counterValue = counterValue +1;
        updateCounterUI();
    }

    public void decrementCounter() {
        if(counterValue > 0) {
            counterValue = counterValue - 1;
            updateCounterUI();
        }
    }

    private int readCounterFromSharedPref(){
        counterValue = sharedPref.getInt(getString(R.string.counter_value), 0);
        return counterValue;
    }

    public void prepareSharedPreference(){
        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), this.MODE_PRIVATE);
    }

}
