package blue_team.com.monuguide.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.fragments.PageFragment;
import blue_team.com.monuguide.models.Monument;
import blue_team.com.monuguide.models.Note;

import static blue_team.com.monuguide.activities.MainActivity.LOCATION_REQUEST;
import static blue_team.com.monuguide.activities.SettingsActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class PagerActivity extends FragmentActivity {

    public static final String EXTRA_WITH_MONUMENT = "ExtraMonumentID";
    public static final String EXTRA_WITH_SIZE = "ExtraListSize";
    int mSize;
    public static boolean mFirstCommit = false;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private Monument mMonument;
    private TextView mNothingText;
    private FireHelper mFireHelper = new FireHelper();
    private AlertDialog mAlertDialog;
    Animation animation;

    List<Note> mListOfNote;
    private FireHelper.IOnNoteSuccessListener iOnNoteSuccessListener = new FireHelper.IOnNoteSuccessListener() {
        @Override
        public void onSuccess(HashMap<String, Note> mMap) {
            mListOfNote.clear();
            mListOfNote.addAll(mMap.values());
            if (!mListOfNote.isEmpty()) {
                mViewPager.setAdapter(mPagerAdapter);
                mSize = mListOfNote.size();
            } else {
                mViewPager.setVisibility(View.GONE);
                mNothingText.setVisibility(View.VISIBLE);
            }
            mPagerAdapter.notifyDataSetChanged();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        mFireHelper.setOnNoteSuccessListener(iOnNoteSuccessListener);

        animation = AnimationUtils.loadAnimation(this,R.anim.pressed_anim);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mNothingText = (TextView) findViewById(R.id.nothing_id);
        TextView name = (TextView) findViewById(R.id.name_of_monument);
        final ImageView back = (ImageView) findViewById(R.id.home_img);
        final ImageView draw = (ImageView) findViewById(R.id.draw_img);
        mPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        if (this.getIntent() != null) {
            if (this.getIntent().getParcelableExtra(StartActivity.ARGUMENT_WITH_MONUMENT) != null) {
                mMonument = this.getIntent().getParcelableExtra(StartActivity.ARGUMENT_WITH_MONUMENT);
                name.setText(mMonument.getName());
                mListOfNote = new ArrayList<>();
                mFireHelper.getNotesList(mMonument.getId());
            }
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back.startAnimation(animation);
                PagerActivity.this.finish();
                overridePendingTransition(R.anim.alpha_up, R.anim.alpha_down);
            }
        });

        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                draw.startAnimation(animation);
                LocationManager locationManager = (LocationManager) PagerActivity.this.getSystemService(Context.LOCATION_SERVICE);
                ConnectivityManager connectivityManager = (ConnectivityManager) PagerActivity.this.getSystemService(CONNECTIVITY_SERVICE);
                if (connectivityManager.getActiveNetworkInfo() != null) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        if (mFireHelper.getCurrentUid() != null) {
                            if (ActivityCompat.checkSelfPermission(PagerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PagerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(PagerActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            } else {
                                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PagerActivity.this);
                            builder.setTitle("If you wish paint note you will be login in facebook\n continue...?");
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(PagerActivity.this, FacebookLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mAlertDialog.cancel();
                                }
                            });
                            mAlertDialog = builder.create();
                            mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                            mAlertDialog.show();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PagerActivity.this);
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
                                        Toast.makeText(PagerActivity.this, "GPS not enabled", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        mAlertDialog = builder.create();
                        mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                        mAlertDialog.show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PagerActivity.this);
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
                    mAlertDialog = builder.create();
                    mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                    mAlertDialog.show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PagerActivity.this.finish();
        overridePendingTransition(R.anim.alpha_up, R.anim.alpha_down);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mFireHelper.getNotesList(mMonument.getId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFirstCommit && mViewPager.getVisibility() == View.GONE) {
            mNothingText.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
        }
        Log.d("Log_Tag",String.valueOf(mListOfNote.size()));
        //mPagerAdapter.notifyDataSetChanged();
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position, mListOfNote.get(position), mMonument);
        }

        @Override
        public int getCount() {
            return mListOfNote.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mListOfNote.get(position).getAutorName();
        }
    }

    public static void setFirstCommit(boolean firstCommit) {
        mFirstCommit = firstCommit;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double x = mMonument.getLatitude() - location.getLatitude();
            x = x * x;
            double y = mMonument.getLongitude() - location.getLongitude();
            y = y * y;
            double result = x + y;
            if (result <= 1) {
                Intent intent = new Intent(PagerActivity.this, DrawingActivity.class);
                intent.putExtra(EXTRA_WITH_MONUMENT, mMonument);
                intent.putExtra(EXTRA_WITH_SIZE, mSize);
                startActivity(intent);
                overridePendingTransition(R.anim.draw_open_anim, R.anim.draw_alpha_down);
            } else {
                Toast.makeText(PagerActivity.this, "You are far from monument to see notes", Toast.LENGTH_LONG).show();
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
