package com.mkhan.tasbeeh;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        ShareActionProvider.OnShareTargetSelectedListener{

    private TextView  textViewCounter;
    private Button btnAdd;
    private Button btnSubstract;
    private Button btnReset;
    private Button buttonBulkAdd;
    private int counterValue = 0;

    private Configuration config;
    public SharedPreferences sharedPref;

    private TextView  goalTextView;
    private TextView  goalValueTextView;
    private TextView  goalRemainTextView;
    private TextView  goalRemainValueTextView;
    private long goalValue = 0;
    private long goalRemainValue = 0;
    private int width , height;
    private ConstraintLayout mainLayout;

    private ShareActionProvider mShareActionProvider;
    Intent shareIntent=new Intent(Intent.ACTION_SEND);

    AudioManager audioManager;
    ContentResolver mContentResolver;
    Vibrator vibrator ;
    AlertDialog.Builder alert;
    //Spinner tasbeehSpinner;

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

        //tasbeehSpinner =  (Spinner) findViewById(R.id.spinner);

        readCounterFromSharedPref();
        updateCounterUI();
        //updateGoalUI();
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mContentResolver = this.getContentResolver();

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setSoundEffectsEnabled(true);
        btnAdd.setOnTouchListener(new HapticListner());
        /*btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("********Mohseen OnClick************");

            }
        });*/

        buttonBulkAdd = (Button) findViewById(R.id.buttonBulkAdd);
        buttonBulkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementInBulk();
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

    private class HapticListner implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event){

            if(event.getAction() == MotionEvent.ACTION_DOWN){
                incrementCounter(1);

                boolean isTouchSoundsEnabled = Settings.System.getInt(mContentResolver,
                        Settings.System.SOUND_EFFECTS_ENABLED, 1) != 0;

                //v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING,HapticFeedbackConstants.KEYBOARD_TAP);
                if(!isTouchSoundsEnabled){
                    audioManager.playSoundEffect(SoundEffectConstants.CLICK,AudioManager.FLAG_VIBRATE);
                } else {
                    v.playSoundEffect(SoundEffectConstants.CLICK);
                }

                if(Utility.readBooleanFromSharedPref(sharedPref,getString(R.string.pref_vibrate))){
                    boolean isHapticEnabled = Settings.System.getInt(mContentResolver,
                            Settings.System.HAPTIC_FEEDBACK_ENABLED, 1) != 0;
                    if(isHapticEnabled){
                        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                        //v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,HapticFeedbackConstants.KEYBOARD_TAP);
                    } else {
//                        System.out.println("Mohseen *********vibrating=");
                         vibrator.vibrate(100);
                    }

  //                  System.out.println("Mohseen *********hapticEnabled=" + isHapticEnabled + " isTouchSoundsEnabled ="+isTouchSoundsEnabled);
                }

            }
            return true;

        }
    }

    public void incrementInBulk(){
        if(alert == null) {
            alert = new AlertDialog.Builder(this);
        }

        final EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(7);
        edittext.setFilters(FilterArray);
        //alert.setMessage("Enter Your Message");
        alert.setTitle("Enter Number to Add to Counter");
        alert.setView(edittext);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                Editable YouEditTextValue = edittext.getText();

                if(YouEditTextValue != null && !YouEditTextValue.toString().isEmpty()) {
                    //System.out.println("Mohseen incrementInBulk YouEditTextValue : " + YouEditTextValue);
                    int oldValue = counterValue;
                    incrementCounter(Integer.valueOf(YouEditTextValue.toString()));
                    Utility.showToast(getApplicationContext(),YouEditTextValue.toString() + " Successfully added to " + oldValue);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();

        //counterValue = counterValue +1;
        updateCounterUI();
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

    public void incrementCounter(int newValue) {
        counterValue = counterValue +newValue;
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
            try {
                goalValue = Long.valueOf(sharedPref.getString(getString(R.string.pref_goal_key), "0"));
            } catch (Exception e){
                goalValue = 0;
                Utility.writeLongToSharedPref(sharedPref,getString(R.string.pref_goal_key),0);
            }

            if(goalValue == 0){
                 goalTextView.setText("No Goal Set");
                goalValueTextView.setVisibility(View.INVISIBLE);
                  goalRemainTextView.setVisibility(View.INVISIBLE);
                  goalRemainValueTextView.setVisibility(View.INVISIBLE);

            } else {
                //System.out.println("Mohseen : updateGoalUI Else  " + goalTextView);

                goalTextView.setText("Goal :");
                goalValueTextView.setText(Long.toString(goalValue));
                goalValueTextView.setVisibility(View.VISIBLE);

                updateRemainingGoalValue();

            }

    }

    private void updateRemainingGoalValue(){
        goalRemainValue = goalValue - counterValue >= 1 ? goalValue - counterValue : 0;
        if(goalRemainValue > 0){
            goalRemainValueTextView.setText(Long.toString(goalRemainValue));
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
        buttonBulkAdd.setTextColor(color);
        goalTextView.setTextColor(color);
        goalValueTextView.setTextColor(color);
        goalRemainTextView.setTextColor(color);
        goalRemainValueTextView.setTextColor(color);
        textViewCounter.setTextColor(color);
       /* View v = tasbeehSpinner.getSelectedView();

        System.out.println("****** mohseen tasbeehSpinner.getEmptyView() *****" + tasbeehSpinner.getEmptyView());
        if( v != null){
            ((TextView)v).setTextColor(color);
        }*/

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
           // System.out.println("Mohseen : height " + btnAdd.getLayoutParams().height + " width " + btnAdd.getLayoutParams().width);
            if(config.densityDpi >= 400 ){
                final float scale = this.getResources().getDisplayMetrics().density;
                int pixels = (int) (100 * scale + 0.5f);
             //   System.out.println("Mohseen : density " + config.densityDpi + " pixel = " + pixels);
                //btnAdd.getLayoutParams().width = ( width / 2 ) * pixels;
                btnAdd.getLayoutParams().height = height;
            }
            if (width > 500 && height > 600) {
                btnReset.getLayoutParams().width = 350;
                btnReset.getLayoutParams().height = 150;
                btnSubstract.getLayoutParams().width = 250;
                btnSubstract.getLayoutParams().height = 150;
                //btnAdd.getLayoutParams().width = 1000;
                //btnAdd.getLayoutParams().height = 350;
                buttonBulkAdd.getLayoutParams().width = 250;
                buttonBulkAdd.getLayoutParams().height = 150;
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
                //btnAdd.getLayoutParams().height = pixels;
            }
        } else {
            if (width > 700 && height > 450) {
                btnReset.getLayoutParams().width = 350;
                btnReset.getLayoutParams().height = 150;
                btnSubstract.getLayoutParams().width = 250;
                btnSubstract.getLayoutParams().height = 150;
                btnAdd.getLayoutParams().width = width - 100;
                btnAdd.getLayoutParams().height = 150;
                buttonBulkAdd.getLayoutParams().width = 250;
                buttonBulkAdd.getLayoutParams().height = 150;
                goalTextView.setTextSize(30);
                goalValueTextView.setTextSize(30);
                goalRemainTextView.setTextSize(30);
                goalRemainValueTextView.setTextSize(30);
                textViewCounter.setTextSize(100);
            }
        }

    }


}
