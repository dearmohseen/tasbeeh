package com.mkhan.tasbeeh;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        ShareActionProvider.OnShareTargetSelectedListener{

    private TextView  textViewCounter;
    private Button btnAdd;
    private Button btnSubstract;
    private Button btnReset;
    private int counterValue = 0;

    private Configuration config;
    public SharedPreferences sharedPref;

    private TextView  goalTextView;
    private TextView  goalValueTextView;
    private TextView  goalRemainTextView;
    private TextView  goalRemainValueTextView;
    private int goalValue = 0;
    private int goalRemainValue = 0;
    private int width , height;
    private ConstraintLayout mainLayout;

    private ShareActionProvider mShareActionProvider;
    Intent shareIntent=new Intent(Intent.ACTION_SEND);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        config = getResources().getConfiguration();
        width = config.screenWidthDp;
        height = config.screenHeightDp;

        mainLayout = (ConstraintLayout) findViewById(R.id.mainLayoutID);

        prepareSharedPreference();

        textViewCounter = (TextView) findViewById(R.id.textViewCounter);

        goalTextView = (TextView) findViewById(R.id.goalText);
        goalValueTextView = (TextView) findViewById(R.id.goalValueText);
        goalRemainTextView = (TextView) findViewById(R.id.goalRemainText);
        goalRemainValueTextView = (TextView) findViewById(R.id.goalRemainValue);

        readCounterFromSharedPref();
        updateCounterUI();
        //updateGoalUI();

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

        setTextSizes();
}

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source,
                                         Intent intent) {
        Toast.makeText(this, intent.getComponent().toString(),
                Toast.LENGTH_LONG).show();

        return(false);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, Utility.APP_STORE_URL + this.getPackageName());
        return shareIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item=menu.findItem(R.id.menu_item_share  );
        mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //System.out.println(" onCreateOptionsMenu : mShareActionProvider " + mShareActionProvider);

        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         //Handle item selection
        switch (item.getItemId()) {
            case R.id.action_rate_app:
                //System.out.println("Mohseen : Rate App Clicked ");
                Utility.rateApp(this);
                return true;
            case R.id.action_settings:
                //System.out.println("Mohseen : Action setting Clicked ");
                this.startActivity(new Intent(this,SettingsActivity.class));
                return true;
            case R.id.menu_item_share:
                //System.out.println("Mohseen : Action setting Clicked ");
                this.startActivity(new Intent(this,SettingsActivity.class));
                return true;
            default:
                this.startActivity(new Intent(this,SettingsActivity.class));
                return true;
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
        updateGoalUI();
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
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public void updateGoalUI(){
            goalValue = Integer.valueOf(sharedPref.getString(getString(R.string.pref_goal_key),"0"));
            if(goalValue == 0){
                 goalTextView.setText("No Goal Set");
                goalValueTextView.setVisibility(View.INVISIBLE);
                  goalRemainTextView.setVisibility(View.INVISIBLE);
                  goalRemainValueTextView.setVisibility(View.INVISIBLE);

            } else {
                //System.out.println("Mohseen : updateGoalUI Else  " + goalTextView);

                goalTextView.setText("Goal :");
                goalValueTextView.setText(Integer.toString(goalValue));
                goalValueTextView.setVisibility(View.VISIBLE);

                updateRemainingGoalValue();

            }

    }

    private void updateRemainingGoalValue(){
        goalRemainValue = goalValue - counterValue >= 1 ? goalValue - counterValue : 0;
        if(goalRemainValue > 0){
            goalRemainValueTextView.setText(Integer.toString(goalRemainValue));
            goalRemainTextView.setText("Remain :");
            goalRemainTextView.setVisibility(View.VISIBLE);
            goalRemainValueTextView.setVisibility(View.VISIBLE);
        } else {
            goalRemainValueTextView.setVisibility(View.INVISIBLE);
            goalRemainTextView.setVisibility(View.VISIBLE);
            goalRemainTextView.setText("MashaAllah ! Goal Achieved.");
        }


    }


    public void updateTextColor(int color){
        btnSubstract.setTextColor(color);
        btnReset.setTextColor(color);
        btnAdd.setTextColor(color);
        goalTextView.setTextColor(color);
        goalValueTextView.setTextColor(color);
        goalRemainTextView.setTextColor(color);
        goalRemainValueTextView.setTextColor(color);
        textViewCounter.setTextColor(color);
    }

    public void updateTheme(){

        String color = sharedPref.getString(getString(R.string.pref_background_color),"#000000");
        GradientDrawable bgShape = (GradientDrawable)mainLayout.getBackground();
       // System.out.println("Mohseen : updateTheme " +  color + " :: " + sharedPref.getAll());
        bgShape.setColor(Color.parseColor(color));

        boolean darkText = sharedPref.getBoolean(getString(R.string.pref_text_color),false);
        if(darkText){
            updateTextColor(Color.BLACK);
            if(color.equals("#000000")){
                bgShape.setColor(Color.WHITE);
            }
        }else {
            if(color.equals("#ffffff")){
                updateTextColor(Color.BLACK);
            } else {
                updateTextColor(Color.WHITE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       //System.out.println("Mohseen : MainActivity Resume");
        updateGoalUI();
        updateTheme();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //System.out.println("Mohseen : MainActivity onPause");
    }

    private void setTextSizes(){
        //System.out.println("Mohseen : setTextSizes " + width + " : " +height + " Orientation : " + config.orientation);

        if(config.orientation == 1) {

            if (width > 500 && height > 600) {
                btnReset.getLayoutParams().width = 400;
                btnReset.getLayoutParams().height = 150;
                btnSubstract.getLayoutParams().width = 400;
                btnSubstract.getLayoutParams().height = 150;
                btnAdd.getLayoutParams().width = width - 100;
                btnAdd.getLayoutParams().height = 150;
                goalTextView.setTextSize(30);
                goalValueTextView.setTextSize(30);
                goalRemainTextView.setTextSize(30);
                goalRemainValueTextView.setTextSize(30);
            } else if(width > 310 && height > 500){
                int size  = 20;
                goalTextView.setTextSize(size);
                goalValueTextView.setTextSize(size);
                goalRemainTextView.setTextSize(size);
                goalRemainValueTextView.setTextSize(size);
                final float scale = this.getResources().getDisplayMetrics().density;
                int pixels = (int) (100 * scale + 0.5f);
                btnAdd.getLayoutParams().height = pixels;
            }
        } else {
            if (width > 700 && height > 450) {
                btnReset.getLayoutParams().width = 400;
                btnReset.getLayoutParams().height = 150;
                btnSubstract.getLayoutParams().width = 400;
                btnSubstract.getLayoutParams().height = 150;
                btnAdd.getLayoutParams().width = width - 100;
                btnAdd.getLayoutParams().height = 150;
                goalTextView.setTextSize(30);
                goalValueTextView.setTextSize(30);
                goalRemainTextView.setTextSize(30);
                goalRemainValueTextView.setTextSize(30);
                textViewCounter.setTextSize(100);
            }
        }

    }


}
