package com.example.android.newsapp;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    // Instantiation of the news preference fragment, extending from PreferenceFragment
    public static class NewsPreferenceFragment
            extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        // In the oncreate method add the preference from resource,
        // defined in res/xml/settings_main
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            // Preference which sets the stories per page
            Preference storiesPerPage = findPreference(getString(R.string.settings_stories_per_page_key));
            bindPreferenceSummaryToValue(storiesPerPage);

            // Preference which sets the Order-by method
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
        }

        // Method that gets called every time a user changes a preference
        // In this case we want to update the UI in a way that the
        // changed preference gets shown
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0){
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        // Method to help us bind the value that's in SharedPreferences
        // to what will show up in the preference summary
        private void bindPreferenceSummaryToValue(Preference preference){
            // We use setOnPreferenceChangeListener to set the current NewsPreferenceFragment
            // instance to listen for changes to the preference we pass in using:
            preference.setOnPreferenceChangeListener(this);

            // We also read the current value of the preference stored in the
            // SharedPreferences on the device and display that in the preference summary
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(),"");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
