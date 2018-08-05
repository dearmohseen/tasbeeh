package com.mkhan.tasbeeh;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatPreferenceActivity  {

    static SharedPreferences sharedPreferences;
    static Context context;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            /*System.out.println("Mohseen : OnPreferenceChangeListener Key : " + preference.getKey()  + " preference : "+ preference
                    + " stringValue : " + stringValue);*/

            /*if("pref_goal_key".equalsIgnoreCase(preference.getKey())){
                //preference.setSummary(stringValue);
            }
            else */if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }else {
               /* System.out.println("Mohseen : OnPreferenceChangeListener Key : " + preference.getKey()  + " preference : "+ preference
                        + " stringValue : " + stringValue);
*/
               preference.setSummary(stringValue);
                /*if("pref_goal_key".equalsIgnoreCase(preference.getKey())){
                    Long goalValue = Long.valueOf(stringValue);
                    preference.setSummary(stringValue);
                   if(goalValue > 99999999999l){
                        //Toast.makeText(context, "Goal not updated. value should be less than 99999999999", Toast.LENGTH_LONG).show();
                        preference.setSummary("99999999999");
                        //return false;
                    } else {
                        preference.setSummary(stringValue);
                        //Toast.makeText(context, "Goal Successfully Set to " + stringValue, Toast.LENGTH_LONG).show();
                    }
                }*/
            }
            return true;
        }



    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        //System.out.println("Mohseen : bindPreferenceSummaryToValue ");
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        if(context == null){
            context = preference.getContext();
        }

        // Trigger the listener immediately with the preference's current value.
        if(sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
        }
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,sharedPreferences.getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Mohseen : SettingActivity : onCreate");
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
        setupActionBar();

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        //System.out.println("Mohseen : SettingActivity : onMenuItemSelected");

        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
                //|| BackgroundColorPreferenceFragment.class.getName().equals(fragmentName);
                //|| NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //System.out.println("Mohseen : GeneralPreferenceFragment : onCreate");

            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("pref_goal_key"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            //System.out.println("Mohseen : GeneralPreferenceFragment : onOptionsItemSelected");
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class BackgroundColorPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //System.out.println("Mohseen : BackgroundColorPreferenceFragment : onCreate");
            //addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            //System.out.println("Mohseen : BackgroundColorPreferenceFragment : onOptionsItemSelected");
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();
       getSharedPreferences(registerOnSharedPreferenceChangeListener());
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }*/
}





