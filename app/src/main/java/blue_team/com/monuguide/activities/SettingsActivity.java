package blue_team.com.monuguide.activities;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.service.LocationService;


public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 17;
    public static final String KEY_OF_LIST_RADIUS = "notifications_new_message_radius";
    public static final String KEY_OF_FUNCTION = "notifications_new_message";
    public static final String KEY_OF_VIBRATE = "notifications_new_message_vibrate";
    public static final String KEY_OF_RINGTONE = "notifications_new_message_ringtone";
    private Intent mIntent;
    private SwitchPreference mSwitchPreference;


    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.xml.pref_notification);
        mSwitchPreference = (SwitchPreference) findPreference(KEY_OF_FUNCTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        mSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    SharedPreferences.Editor editor = preference.getSharedPreferences().edit();
                    editor.putBoolean(KEY_OF_FUNCTION, false);
                    ((SwitchPreference) preference).setChecked(false);
                    editor.apply();
                    ActivityCompat.requestPermissions(SettingsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } else {
                    if (((boolean) o)) {
                        ((SwitchPreference) preference).setChecked(true);
                    } else if (!((boolean) o)) {
                        ((SwitchPreference) preference).setChecked(false);
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
            overridePendingTransition(R.anim.alpha_up, R.anim.alpha_down);
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            Log.d("Log_Tag", s);
            mIntent = new Intent(SettingsActivity.this, LocationService.class);
            if (s.equals(KEY_OF_FUNCTION)) {
                if (sharedPreferences.getBoolean(s, false)) {
                    startService(mIntent);
                } else stopService(mIntent);
            } else if (s.equals(KEY_OF_LIST_RADIUS)) {
                Log.d("Log_Tag1", sharedPreferences.getString(s, "1"));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Preference preference = findPreference(KEY_OF_FUNCTION);
                SharedPreferences.Editor editor = preference.getSharedPreferences().edit();
                editor.putBoolean(KEY_OF_FUNCTION, true);
                ((SwitchPreference) preference).setChecked(true);
                editor.apply();
                mIntent = new Intent(SettingsActivity.this, LocationService.class);
                startService(mIntent);
            } else {
                Preference preference = findPreference(KEY_OF_FUNCTION);
                SharedPreferences.Editor editor = preference.getSharedPreferences().edit();
                editor.putBoolean(KEY_OF_FUNCTION, false);
                ((SwitchPreference) preference).setChecked(false);
                editor.apply();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
        overridePendingTransition(R.anim.alpha_up, R.anim.alpha_down);
    }
}

