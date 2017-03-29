package blue_team.com.monuguide.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.activities.MainActivity;
import blue_team.com.monuguide.activities.SettingsActivity;
import blue_team.com.monuguide.activities.StartActivity;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;


public class LocationService extends Service {
    private static final int ID_FOR_FOREGROUND = 127;
    public static final String SERVICE_CONNECTION = "Service_runing";
    public static final String SHOWING_MONUMENT = "ShowingMonument";
    public static final String ACTION = "action ";

    private LocationManager mLocationManager;
    private NotificationManager mNotManager;
    private NotificationCompat.Builder mBuilder;
    private boolean isConnected = false;
    private FireHelper mFireHelper = new FireHelper();
    private List<Monument> mListOfMonument, mListOfFindedMonuments, mShowMonuments;
    private boolean isEqual = false;
    private int notifID;
    private long[] mVibrateTime = {300, 200, 300, 500, 300, 200, 300};

    private FireHelper.IOnSuccessListener onSuccessListener = new FireHelper.IOnSuccessListener() {
        @Override
        public void onSuccess(HashMap<String, Monument> mMap) {
            mListOfMonument.clear();
            mListOfMonument.addAll(mMap.values());
            testForNotification();
        }
    };


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocationService.this);
            Log.d("Log_Tag2", sharedPreferences.getString(SettingsActivity.KEY_OF_LIST_RADIUS, "0"));
            mFireHelper.getMonuments(location.getLatitude(), location.getLongitude(), Double.valueOf(sharedPreferences.getString(SettingsActivity.KEY_OF_LIST_RADIUS, "0")));

            testForNotification();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            if (!isConnect()) {
                stopForeground(true);
                stopSelf();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        isConnected = false;
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (isConnect()) {
            startServiceOperation();
        } else {
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else if (isConnect()) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 20, locationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 20, locationListener);
            Toast.makeText(this, "Location Service Run", Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isConnected)
            mLocationManager.removeUpdates(locationListener);
        stopForeground(true);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SERVICE_CONNECTION, false);
        editor.apply();

    }

    private void startServiceOperation() {
        Notification foregroundNotification;
        mFireHelper.setOnSuccessListener(onSuccessListener);
        mListOfMonument = new ArrayList<>();
        mNotManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mShowMonuments = new ArrayList<>();
        mListOfFindedMonuments = new ArrayList<>();
        isConnected = true;
        notifID = 0;
        Intent foregroundIntent = new Intent(this, SettingsActivity.class);
        PendingIntent foregroundPendingIntent = PendingIntent.getActivity(this, 0, foregroundIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.header_notif_icon).setContentText("AutoFind Is Run").setContentTitle("MonuGuide").setAutoCancel(true).setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND).setContentIntent(foregroundPendingIntent);
        foregroundNotification = mBuilder.build();
        startForeground(ID_FOR_FOREGROUND, foregroundNotification);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SERVICE_CONNECTION, true);
        editor.apply();
    }

    private boolean isConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if ((connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) && mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else
            return false;
    }


    public void testForNotification() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                mShowMonuments = null;
                if (!mListOfMonument.isEmpty()) {
                    if (mListOfFindedMonuments.isEmpty()) {
                        for (Monument monument : mListOfMonument)
                            mListOfFindedMonuments.add(monument);
                        mShowMonuments = mListOfFindedMonuments;
                        showNotification();


                    } else {
                        for (Monument monument : mListOfMonument) {
                            for (Monument findMon : mListOfFindedMonuments) {
                                if (monument.getId().equals(findMon.getId())) {
                                    isEqual = true;
                                    break;
                                }
                            }
                            if (!isEqual) {
                                mListOfFindedMonuments.add(monument);
                                if (mShowMonuments == null)
                                    mShowMonuments = new ArrayList<>();
                                mShowMonuments.add(monument);
                            }
                            isEqual = false;
                        }
                        if (mShowMonuments != null) {
                            showNotification();
                        }
                    }
                }

            }
        });
        thread.run();
    }

    private void showNotification() {
        Notification monumentNotification;
        for (Monument monument : mShowMonuments) {
            mBuilder = new NotificationCompat.Builder(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocationService.this);
            if (sharedPreferences.getBoolean(SettingsActivity.KEY_OF_VIBRATE, false))
                mBuilder.setVibrate(mVibrateTime);
            Log.d("Log_Tag", sharedPreferences.getString(SettingsActivity.KEY_OF_RINGTONE, ""));
            if (!sharedPreferences.getString(SettingsActivity.KEY_OF_RINGTONE, "").equals("")) {
                mBuilder.setSound(Uri.parse(sharedPreferences.getString(SettingsActivity.KEY_OF_RINGTONE, "")));
            }
            mBuilder.setContentTitle("Theare is Monument").setSmallIcon(R.drawable.notif_icon).setContentText(monument.getName() + " is finded near you.").setWhen(System.currentTimeMillis()).setAutoCancel(true);
            Intent monumentIntent = new Intent(LocationService.this, StartActivity.class);
            monumentIntent.setAction(ACTION + monument.getId());
            monumentIntent.putExtra(SHOWING_MONUMENT, monument);
            PendingIntent monumentPendingIntent = PendingIntent.getActivity(LocationService.this, notifID, monumentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(monumentPendingIntent);
            monumentNotification = mBuilder.build();
            mNotManager.notify(notifID, monumentNotification);
            notifID++;
        }
    }

}
