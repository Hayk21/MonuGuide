package blue_team.com.monuguide.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import blue_team.com.monuguide.Services.LocationService;
import blue_team.com.monuguide.activities.SettingsActivity;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean answer = sharedPreferences.getBoolean(SettingsActivity.KEY_OF_FUNCTION, false);
        if (answer) {
            Intent intent_for_service = new Intent(context, LocationService.class);
            context.startService(intent_for_service);
        }
    }
}
