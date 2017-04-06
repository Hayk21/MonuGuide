package blue_team.com.monuguide.activities;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.Services.LocationService;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.fragments.DetailsFragment;
import blue_team.com.monuguide.fragments.WebFragment;
import blue_team.com.monuguide.models.Monument;

import static blue_team.com.monuguide.activities.MainActivity.LOCATION_REQUEST;

public class StartActivity extends AppCompatActivity implements DetailsFragment.OnFragmentInteractionListener {

    public static final String ARGUMENT_WITH_MONUMENT = "CurrentMonument";
    public static final String HEADER_BACKSTACK = "HeaderBackStack";
    public static final String WEB_FRAGMENT = "WebFragment";
    public static final String DETAILS_FRAGMENT = "DeatilsFragment";
    private Monument mMonument;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private FireHelper mFireHalper = new FireHelper();
    private AlertDialog mAlertDialog;
    private Animation open, close, close2;
    LocationManager locationManager;
    Handler handler;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setupActionBar();
        open = AnimationUtils.loadAnimation(this, R.anim.push_effect);
        close = AnimationUtils.loadAnimation(this, R.anim.pull_effect);
        close2 = AnimationUtils.loadAnimation(this, R.anim.pull_effect);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (getIntent() != null) {
            if (getIntent().getParcelableExtra(LocationService.SHOWING_MONUMENT) != null) {
                mMonument = getIntent().getParcelableExtra(LocationService.SHOWING_MONUMENT);
            }
        }
        mFragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        DetailsFragment detailsFragment = new DetailsFragment();
        if (mMonument != null) {
            mMonument = getIntent().getExtras().getParcelable(LocationService.SHOWING_MONUMENT);
            args.putParcelable(ARGUMENT_WITH_MONUMENT, mMonument);
            detailsFragment.setArguments(args);
            mFragmentTransaction.add(R.id.start_activity_container, detailsFragment, DETAILS_FRAGMENT);
            mFragmentTransaction.addToBackStack(HEADER_BACKSTACK);
            getSupportActionBar().setTitle(mMonument.getName());
        }
        mFragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
            overridePendingTransition(R.anim.alpha_up, R.anim.alpha_down);
        } else {
            if (mFragmentManager.findFragmentByTag(WEB_FRAGMENT) != null) {
                if (((WebFragment) mFragmentManager.findFragmentByTag(WEB_FRAGMENT)).getWebView().canGoBack()) {
                    ((WebFragment) mFragmentManager.findFragmentByTag(WEB_FRAGMENT)).getWebView().goBack();
                } else mFragmentManager.popBackStack();
            } else
                mFragmentManager.popBackStack();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onFragmentInteraction(int ID, final Monument monument, final ImageView view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        switch (ID) {
            case R.id.location_img:
                Intent intentForMap = new Intent(StartActivity.this, MainActivity.class);
                intentForMap.putExtra(ARGUMENT_WITH_MONUMENT, monument);
                startActivity(intentForMap);
                this.finish();
                overridePendingTransition(R.anim.alpha_up, R.anim.alpha_down);
                break;
            case R.id.heart_img:
                close.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.setImageDrawable(getResources().getDrawable(R.mipmap.star_icon7));
                        view.startAnimation(open);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                close2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.setImageDrawable(getResources().getDrawable(R.mipmap.pressed_star_icon));
                        view.startAnimation(open);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                final String myuser = mFireHalper.getCurrentUid();
                if (myuser != null) {
                    if (connectivityManager.getActiveNetworkInfo() != null) {
                        if (view.getTag().toString().equals("default")) {
                            mFireHalper.addFavoriteMon(monument, myuser);
                            view.setTag("pressed");
                            view.startAnimation(close2);
                        } else {
                            mFireHalper.removeFavoriteMon(monument, myuser);
                            view.setTag("default");
                            view.startAnimation(close);
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Please connect with internet")
                                .setPositiveButton("CONNECT", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                                        if (!wifiManager.isWifiEnabled())
                                            wifiManager.setWifiEnabled(true);
                                        else
                                            wifiManager.setWifiEnabled(true);
                                    }
                                })
                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        builder.create();
                        builder.show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                    builder.setTitle("Attention").setMessage("If you want to have your own list of favorite monuments,log in with facebook.");
                    builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mAlertDialog.cancel();
                        }
                    });
                    builder.setNegativeButton(R.string.login_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(StartActivity.this, FacebookLoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    mAlertDialog = builder.create();
                    mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                    mAlertDialog.show();
                }

                break;
            case R.id.comment_img:
                if (connectivityManager.getActiveNetworkInfo() != null) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        }
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_REQUEST);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        dialog.cancel();
                                        Toast.makeText(StartActivity.this, "GPS not enabled", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Please connect with internet")
                            .setPositiveButton("CONNECT", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                                    if (!wifiManager.isWifiEnabled())
                                        wifiManager.setWifiEnabled(true);
                                    else
                                        wifiManager.setWifiEnabled(true);
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    builder.create();
                    builder.show();
                }
                break;
            case R.id.wiki_img:
                WebFragment webFragment = new WebFragment();
                Bundle args = new Bundle();
                args.putParcelable(ARGUMENT_WITH_MONUMENT, monument);
                webFragment.setArguments(args);
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                mFragmentTransaction.replace(R.id.start_activity_container, webFragment, WEB_FRAGMENT);
                mFragmentTransaction.addToBackStack(HEADER_BACKSTACK);
                mFragmentTransaction.commit();
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double x = mMonument.getLatitude() - location.getLatitude();
            x = x * x;
            double y = mMonument.getLongitude() - location.getLongitude();
            y = y * y;
            double result = x + y;
            if(result<=0.00000196){
                Intent intent = new Intent(StartActivity.this, PagerActivity.class);
                intent.putExtra(ARGUMENT_WITH_MONUMENT, mMonument);
                startActivity(intent);
                overridePendingTransition(R.anim.alpha_up, R.anim.alpha_down);
            }else {
                Toast.makeText(StartActivity.this, "You are far from monument to see notes", Toast.LENGTH_LONG).show();
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

        }
    };



}
