package com.example.android.popularmovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/*
 * A Settings activity that allows the user to change their preferred
 * sort order.
 */
public class SettingsActivity extends PreferenceActivity
    implements Preference.OnPreferenceChangeListener {

  private final String LOG_TAG = SettingsActivity.class.getSimpleName();

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);
    bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_movie_sort_key)));

  }

  private void bindPreferenceSummaryToValue(Preference preference) {

    preference.setOnPreferenceChangeListener(this);

    onPreferenceChange(preference,
        PreferenceManager
            .getDefaultSharedPreferences(preference.getContext())
            .getString(preference.getKey(), ""));
  }

  @Override
  public boolean onPreferenceChange(Preference preference, Object value) {
    String stringValue = value.toString();

    if (preference instanceof ListPreference) {
      ListPreference listPreference = (ListPreference) preference;
      int prefIndex = listPreference.findIndexOfValue(stringValue);
      if (prefIndex >= 0) {
        preference.setSummary(listPreference.getEntries()[prefIndex]);
      }
    } else {
      preference.setSummary(stringValue);
    }

    return true;
  }

}