package com.soccerapp.eyeonsoccer.View.Activities;

import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.os.Bundle;

import com.soccerapp.eyeonsoccer.GlobalClasses.Constants;
import com.soccerapp.eyeonsoccer.R;

/**
 * Preferences class
 */
public class Preferences extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    private SharedPreferences mPreferences;
    private ListPreference mListPreference;
    private SharedPreferences.Editor mEditor;

    /**
     * On create, load preferences
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.preferences);
        addPreferencesFromResource(R.xml.preference);
        loadPreferences();

    }

    /**
     * Loads preferences
     */
    private void loadPreferences() {
        mListPreference = (ListPreference) findPreference(Constants.LIST_PREFERENCE_KEY);
        mListPreference.setOnPreferenceChangeListener(Preferences.this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(Preferences.this);
        mListPreference.setSummary(mPreferences.getString(Constants.PREF_DATE_FORMAT, Constants.DEFAULT_PREF_SUMMARY));
    }

    /**
     * Manage preference change event
     *
     * @param preference
     * @param newValue
     * @return
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {
            //Date formats
            String[] formats = getResources().getStringArray(R.array.date_formats);
            int indexValue = Integer.parseInt((String) newValue);
            preference.setSummary(formats[indexValue]);
            mEditor = mPreferences.edit();
            mEditor.putString(Constants.PREF_DATE_FORMAT, formats[indexValue]);
            mEditor.commit();
        }
        return false;

    }
}
