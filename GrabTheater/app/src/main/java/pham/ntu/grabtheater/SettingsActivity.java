package pham.ntu.grabtheater;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by User on 6/10/2016.
 */

public class SettingsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }


    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private ListPreference mListPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_general);

            Preference p = getPreferenceScreen();
            if (p instanceof PreferenceGroup) {
                PreferenceGroup pGroup = (PreferenceGroup) p;
                for (int i = 0; i < pGroup.getPreferenceCount(); i++) {
                    Preference p2 = pGroup.getPreference(i);
                    if(p2 instanceof ListPreference){
                        ListPreference listPref = (ListPreference) p2;
                        p2.setSummary(listPref.getEntry());
                    }
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.pref_sort_types_key))) {
                Preference connectionPref = findPreference(key);
                if (connectionPref instanceof ListPreference) {
                    // Set summary to be the user-description for the selected value
                    ListPreference listPreference = (ListPreference) connectionPref;
                    //connectionPref.setSummary(listPreference.getEntry());
                    int prefIndex = listPreference.findIndexOfValue(listPreference.getValue());
                    if (prefIndex >= 0) {
                        connectionPref.setSummary(listPreference.getEntries()[prefIndex]);
                    }
                }
            }
        }
    }
}
