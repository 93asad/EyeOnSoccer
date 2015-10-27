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

public class Preferences extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    private SharedPreferences mPreferences;
    private ListPreference mListPreference;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.preferences);
        addPreferencesFromResource(R.xml.preference);
        loadPreferences();

    }

    private void loadPreferences() {
        mListPreference = (ListPreference)findPreference(Constants.LIST_PREFERENCE_KEY);
        mListPreference.setOnPreferenceChangeListener(Preferences.this);
        CheckBoxPreference mCheckBoxPreference = (CheckBoxPreference)findPreference("checkbox");
        mCheckBoxPreference.setOnPreferenceChangeListener(Preferences.this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(Preferences.this);
        mListPreference.setSummary(mPreferences.getString(Constants.PREF_DATE_FORMAT, Constants.DEFAULT_PREF_SUMMARY));
        mCheckBoxPreference.setChecked(mPreferences.getBoolean("checkbox", false));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {
            String [] formats = getResources().getStringArray(R.array.date_formats);
            int indexValue = Integer.parseInt((String) newValue);
            preference.setSummary(formats[indexValue]);
            mEditor = mPreferences.edit();
            mEditor.putString(Constants.PREF_DATE_FORMAT, formats[indexValue]);
            mEditor.commit();
        }
        else if (preference instanceof CheckBoxPreference) {
            ((CheckBoxPreference) preference).setChecked((boolean)newValue);
            mEditor = mPreferences.edit();
            mEditor.putBoolean("checkbox", (boolean)newValue);
            mEditor.commit();
        }
        return false;

    }
}
