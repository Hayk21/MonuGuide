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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.activities.MainActivity;
import blue_team.com.monuguide.activities.SettingsActivity;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

import static blue_team.com.monuguide.activities.MainActivity.NAME_OF_PREFERENCE;

public class LocationService extends Service {
    private LocationManager locationManager;
    Notification foregroundNotification;
    Intent foregroundIntent;
    PendingIntent pendingIntent;
    private static final int ID_FOR_FOREGROUND = 1;
    boolean isConnected = false;
    FireHelper fireHelper = new FireHelper();
    List<Monument> listOfMonument, listOfFindedMonuments, showMonuments;
    boolean isEqual = false;
    Handler mHandler;
    MyTask myTask = new MyTask();
    NotificationManager manager;


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setContentTitle("Theare is Monument").setSmallIcon(R.mipmap.brush_icon);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocationService.this);
            Log.d("Log_Tag2", sharedPreferences.getString(SettingsActivity.KEY_OF_LIST_RADIUS, "0"));
            listOfMonument = fireHelper.getMonuments(location.getLatitude(), location.getLongitude(), Double.valueOf(sharedPreferences.getString(SettingsActivity.KEY_OF_LIST_RADIUS, "0")));
            if (showMonuments != null) {
                for (Monument monument:showMonuments){
                builder.setContentText(monument.getName() + " is finded near you.");
                    Intent intent = new Intent(LocationService.this,MainActivity.class);
                    intent.putExtra("monument",monument);
                    PendingIntent pendingIntent = PendingIntent.getActivity(LocationService.this,0,intent,0);
                    builder.setContentIntent(pendingIntent);
                    Notification notification = builder.build();
                manager.notify(4,notification);}
            }

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
            manager = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            mHandler = new ConsoleHandler();
            listOfFindedMonuments = new ArrayList<>();
            isConnected = true;
            foregroundIntent = new Intent(this, MainActivity.class);
            pendingIntent = PendingIntent.getActivity(this, 0, foregroundIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher).setContentText("Location Service is run").setContentTitle("MonuGuide").setAutoCancel(true).setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND).setContentIntent(pendingIntent);
            foregroundNotification = builder.build();
            startForeground(ID_FOR_FOREGROUND, foregroundNotification);
            SharedPreferences sharedPref = getSharedPreferences(NAME_OF_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(NAME_OF_PREFERENCE, true);
            editor.apply();
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
        SharedPreferences sharedPref = getSharedPreferences(NAME_OF_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(NAME_OF_PREFERENCE, false);
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

    class MyTask extends AsyncTask {
        List<Monument> resultMon;


        @Override
        protected List<Monument> doInBackground(Object[] objects) {
            resultMon = new ArrayList<>();
            if (listOfMonument != null) {
                if (listOfFindedMonuments.isEmpty()) {
                    for (Monument monument : listOfMonument)
                        listOfFindedMonuments.add(monument);
                    return listOfFindedMonuments;


                } else {
                    for (Monument monument : listOfMonument) {
                        for (Monument findMon : listOfFindedMonuments) {
                            if (monument.equals(findMon)) {
                                isEqual = true;
                                break;
                            }
                        }
                        if (!isEqual) {
                            listOfFindedMonuments.add(monument);
                            resultMon.add(monument);
                        }
                        isEqual = false;
                    }
                    if (!resultMon.isEmpty())
                        return resultMon;
                    else return null;
                }
            } else return null;

        }
    }

}
