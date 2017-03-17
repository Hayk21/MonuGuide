package blue_team.com.monuguide.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import blue_team.com.monuguide.services.LocationService;
import blue_team.com.monuguide.activities.SettingsActivity;

import static blue_team.com.monuguide.activities.MainActivity.NAME_OF_PREFERENCE;

public class InternetStatusReceiver extends BroadcastReceiver {
    Context context;
    LocationManager locationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean answer = sharedPreferences.getBoolean(SettingsActivity.KEY_OF_FUNCTION, false);
        boolean answer_two = sharedPreferences.getBoolean(NAME_OF_PREFERENCE, false);
        Intent intent_for_service = new Intent(context, LocationService.class);
        if (answer) {
            if (isConnect()) {
                if (!answer_two) {
                    context.startService(intent_for_service);
                }
            } else context.stopService(intent_for_service);

        }
    }

    private boolean isConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else
            return false;
    }
}
