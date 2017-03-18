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
import blue_team.com.monuguide.activities.SettingsActivity;
import blue_team.com.monuguide.activities.StartActivity;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

import static blue_team.com.monuguide.activities.MainActivity.NAME_OF_PREFERENCE;

public class LocationService extends Service {
    private static final int ID_FOR_FOREGROUND = 127;
    public static final String SHOWING_MONUMENT = "ShowingMonument";

    LocationManager locationManager;
    NotificationManager mNotManager;
    NotificationCompat.Builder mBuilder;
    Notification foregroundNotification, monumentNotification;
    Intent foregroundIntent, monumentIntent;
    PendingIntent foregroundPendingIntent, monumentPendingIntent;
    boolean isConnected = false;
    FireHelper fireHelper = new FireHelper();
    List<Monument> listOfMonument, listOfFindedMonuments, showMonuments;
    boolean isEqual = false;
    Thread thread;
    int notifID;
    long[] mVibrateTime = {300, 200, 300, 200, 300};

    private FireHelper.IOnSuccessListener onSuccessListener = new FireHelper.IOnSuccessListener() {
        @Override
        public void onSuccess(HashMap<String, Monument> mMap) {
            listOfMonument.clear();
            listOfMonument.addAll(mMap.values());
            testForNotification();
            // listi het gorcoxutyunner@ anel aystex
        }
    };


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocationService.this);
            Log.d("Log_Tag2", sharedPreferences.getString(SettingsActivity.KEY_OF_LIST_RADIUS, "0"));
            fireHelper.getMonuments(location.getLatitude(), location.getLongitude(), Double.valueOf(sharedPreferences.getString(SettingsActivity.KEY_OF_LIST_RADIUS, "0")));

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
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 5, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5, locationListener);
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
            locationManager.removeUpdates(locationListener);
        stopForeground(true);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(NAME_OF_PREFERENCE, false);
        editor.apply();

    }

    private void startServiceOperation() {
        fireHelper.setOnSuccessListener(onSuccessListener);
        listOfMonument = new ArrayList<>();
        mNotManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        showMonuments = new ArrayList<>();
        listOfFindedMonuments = new ArrayList<>();
        isConnected = true;
        notifID = 0;
        foregroundIntent = new Intent(this, SettingsActivity.class);
        foregroundPendingIntent = PendingIntent.getActivity(this, 0, foregroundIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher).setContentText("Location Service is run").setContentTitle("MonuGuide").setAutoCancel(true).setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND).setContentIntent(foregroundPendingIntent);
        foregroundNotification = mBuilder.build();
        startForeground(ID_FOR_FOREGROUND, foregroundNotification);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(NAME_OF_PREFERENCE, true);
        editor.apply();
    }

    private boolean isConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else
            return false;
    }


    public void testForNotification() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                showMonuments = null;
                if (!listOfMonument.isEmpty()) {
                    if (listOfFindedMonuments.isEmpty()) {
                        for (Monument monument : listOfMonument)
                            listOfFindedMonuments.add(monument);
                        showMonuments = listOfFindedMonuments;
                        showNotification();


                    } else {
                        for (Monument monument : listOfMonument) {
                            for (Monument findMon : listOfFindedMonuments) {
                                if (monument.getId().equals(findMon.getId())) {
                                    isEqual = true;
                                    break;
                                }
                            }
                            if (!isEqual) {
                                listOfFindedMonuments.add(monument);
                                if (showMonuments == null)
                                    showMonuments = new ArrayList<>();
                                showMonuments.add(monument);
                            }
                            isEqual = false;
                        }
                        if (showMonuments != null) {
                            showNotification();
                        }
                    }
                }

            }
        });
        thread.run();
    }

    private void showNotification() {
        for (Monument monument : showMonuments) {
            mBuilder = new NotificationCompat.Builder(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocationService.this);
            if (sharedPreferences.getBoolean(SettingsActivity.KEY_OF_VIBRATE, false))
                mBuilder.setVibrate(mVibrateTime);
            Log.d("Log_Tag", sharedPreferences.getString(SettingsActivity.KEY_OF_RINGTONE, ""));
            if (sharedPreferences.getString(SettingsActivity.KEY_OF_RINGTONE, "") != "") {
                mBuilder.setSound(Uri.parse(sharedPreferences.getString(SettingsActivity.KEY_OF_RINGTONE, "")));
            }
            mBuilder.setContentTitle("Theare is Monument").setSmallIcon(R.mipmap.brush_icon).setContentText(monument.getName() + " is finded near you.").setWhen(System.currentTimeMillis()).setAutoCancel(true);
            monumentIntent = new Intent(LocationService.this, StartActivity.class);
            monumentIntent.putExtra(SHOWING_MONUMENT, monument);
            monumentPendingIntent = PendingIntent.getActivity(LocationService.this, 0, monumentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(monumentPendingIntent);
            monumentNotification = mBuilder.build();
            mNotManager.notify(notifID, monumentNotification);
            notifID++;
        }
    }

}
