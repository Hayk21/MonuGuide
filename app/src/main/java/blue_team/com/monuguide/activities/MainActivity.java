package blue_team.com.monuguide.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;


import blue_team.com.monuguide.R;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.fragments.MapStatueFragment;
import blue_team.com.monuguide.fragments.SearchFragment;
import blue_team.com.monuguide.models.Monument;

import static blue_team.com.monuguide.activities.SettingsActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainActivity";
    public static final String SEARCH_FRAGMENT = "SearchFragment";
    public static final String MAP_FRAGMENT = "MapFragment";
    public static final String FAVORITE_FRAGMENT = "FavoriteFragment";
    public static final String ARGUMENT_FOR_FAVORITE = "Favorite";
    public static final int LOCATION_REQUEST = 1;
    Fragment mMapStatueFragment;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    Context mContext;
    Toolbar mToolbar;
    FrameLayout mFrameLayout;
    Animation animation_open;
    Animation animation_close;
    private SearchView searchView;
    private MenuItem item;
    private FireHelper mFireHelper = new FireHelper();
    private NavigationView navigationView;
    private SearchFragment favoriteFragment;
    private locBR  mLocBR = new locBR();
    private Boolean mLocationStatus = false;
    private AlertDialog mAlertDialog;
    ConnectivityManager cm;
    LocationManager locationManager;

    private Snackbar snackbar;

    private BroadcastReceiver mBroadcastReceiver;
    final IntentFilter mIntentFilter = new IntentFilter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_LOCALE_CHANGED));

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFrameLayout = (FrameLayout) findViewById(R.id.search_container);
        setSupportActionBar(mToolbar);
        animation_open = AnimationUtils.loadAnimation(this, R.anim.open_down);
        animation_close = AnimationUtils.loadAnimation(this, R.anim.close_up);
        animation_close.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFrameLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mContext = this;

        if (!mLocationStatus) {
            getLocation();
        }
        mFragmentManager = getFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mMapStatueFragment = new MapStatueFragment();
        mFragmentTransaction.add(R.id.container, mMapStatueFragment, MAP_FRAGMENT);
        mFragmentTransaction.commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFireHelper.getCurrentUid() == null) {
            navigationView.getMenu().findItem(R.id.login).setTitle(this.getString(R.string.log_in));
            navigationView.getMenu().findItem(R.id.fav_mon).setVisible(false);
        } else {
            navigationView.getMenu().findItem(R.id.login).setTitle(this.getString(R.string.log_out));
            navigationView.getMenu().findItem(R.id.fav_mon).setVisible(true);
        }

        mIntentFilter.addAction("android.location.PROVIDERS_CHANGED");
        mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(mLocBR, mIntentFilter);

        //getLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
            unregisterReceiver(mLocBR);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mFrameLayout.getVisibility() == View.VISIBLE) {
                if(mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT)!=null) {
                    item.collapseActionView();
                    searchView.setIconified(true);
                    searchView.clearFocus();
                    invalidateOptionsMenu();
                    FragmentTransaction fragmentTransaction1 = mFragmentManager.beginTransaction();
                    fragmentTransaction1.remove(mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT));
                    fragmentTransaction1.commit();
                    mFrameLayout.startAnimation(animation_close);
                }
                else if(mFragmentManager.findFragmentByTag(FAVORITE_FRAGMENT)!=null){
                    FragmentTransaction fragmentTransaction1 = mFragmentManager.beginTransaction();
                    fragmentTransaction1.remove(mFragmentManager.findFragmentByTag(FAVORITE_FRAGMENT));
                    fragmentTransaction1.commit();
                    mToolbar.setTitle(getString(R.string.name_of_app));
                    mFrameLayout.startAnimation(animation_close);
                    favoriteFragment = null;
                }
            } else {
                this.finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        item = menu.findItem(R.id.menuSearch);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) item.getActionView();


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (mFrameLayout.getVisibility() == View.VISIBLE) {
                    FragmentTransaction fragmentTransaction1 = mFragmentManager.beginTransaction();
                    fragmentTransaction1.remove(mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT));
                    fragmentTransaction1.commit();
                    if(favoriteFragment == null) {
                        mFrameLayout.startAnimation(animation_close);
                    }
                }

                return false;
            }
        });


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation_open_left = AnimationUtils.loadAnimation(MainActivity.this, R.anim.open_to_left);
                searchView.startAnimation(animation_open_left);
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setOnSearchViewChangeListener(new SearchFragment.onSearchViewChangeListener() {
                    @Override
                    public void ViewChanged() {
                        View view = MainActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        item.collapseActionView();
                        searchView.setIconified(true);
                        searchView.clearFocus();
                        invalidateOptionsMenu();
                    }
                });
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                if(mFragmentManager.findFragmentByTag(FAVORITE_FRAGMENT)!=null){
                    transaction.remove(mFragmentManager.findFragmentByTag(FAVORITE_FRAGMENT));
                    transaction.add(R.id.search_container, searchFragment, SEARCH_FRAGMENT);
                    transaction.commit();
                    mFrameLayout.setVisibility(View.INVISIBLE);
                    mFrameLayout.setVisibility(View.VISIBLE);
                }else {
                transaction.add(R.id.search_container, searchFragment, SEARCH_FRAGMENT);
                transaction.commit();
                mFrameLayout.setVisibility(View.VISIBLE);
                mFrameLayout.startAnimation(animation_open);}
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    System.out.println("datark");
                    ((SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT)).setText();
                    ((SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT)).monumentList.clear();
                    ((SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT)).mAdapter.setMonumentList(((SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT)).monumentList);
                    ((SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT)).mAdapter.notifyDataSetChanged();
                } else {
                    System.out.println("baza");
                    String text = newText.toLowerCase();
                    ((SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT)).getFh().getSearchMonument(text);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getParcelableExtra(StartActivity.ARGUMENT_WITH_MONUMENT) != null) {
            ((MapStatueFragment) mFragmentManager.findFragmentByTag(MAP_FRAGMENT)).setMonumentFromSearch(((Monument) intent.getParcelableExtra(StartActivity.ARGUMENT_WITH_MONUMENT)));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.login) {
            Intent intent = new Intent(MainActivity.this, FacebookLoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha_up, R.anim.alpha_down);
        } else if (id == R.id.fav_mon) {
            favoriteFragment = new SearchFragment();
            Bundle args = new Bundle();
            args.putString(ARGUMENT_FOR_FAVORITE, "Favorite");
            favoriteFragment.setArguments(args);
            View view = MainActivity.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            if (mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT) != null) {
                item.collapseActionView();
                searchView.setIconified(true);
                searchView.clearFocus();
                invalidateOptionsMenu();
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.remove(mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT));
                mFragmentTransaction.add(R.id.search_container, favoriteFragment, FAVORITE_FRAGMENT);
                mToolbar.setTitle(getString(R.string.favorite_monuments));
                mFragmentTransaction.commit();
            } else {
                if (mFragmentManager.findFragmentByTag(FAVORITE_FRAGMENT) == null) {
                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.add(R.id.search_container, favoriteFragment, FAVORITE_FRAGMENT);
                    mFragmentTransaction.commit();
                    mToolbar.setTitle(getString(R.string.favorite_monuments));
                    mFrameLayout.setVisibility(View.VISIBLE);
                    mFrameLayout.startAnimation(animation_open);
                }
            }

            } else if (id == R.id.nav_manage) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.alpha_up, R.anim.alpha_down);

            } else if (id == R.id.nav_share) {

            } else if (id == R.id.nav_send) {

            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

    private void checkNetworkLocationInfo(){
        if (mBroadcastReceiver == null){
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int level = intent.getIntExtra(Intent.ACTION_LOCALE_CHANGED, 0);
                    Log.v(TAG, level + "");
                }
            };
            //mIntentFilter.addAction();
        }
    }

    private void getConnections() {
        System.out.println("inGetConnection = " + cm.getActiveNetworkInfo());

        if (cm.getActiveNetworkInfo() == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Attention");
            builder.setIcon(R.drawable.ic_info_black_24dp);
            builder.setMessage(getString(R.string.connect_internet))
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

    private void  getLocation()  {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        else
            mLocationStatus = true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            builder.setIcon(R.drawable.ic_info_black_24dp);
            builder.setTitle("Attention");
            builder.setMessage(getString(R.string.turn_on_GPS))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_REQUEST);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                            if(cm.getActiveNetworkInfo() == null || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                            snackbar = Snackbar
                                    .make((View) findViewById(R.id.mapId), getString(R.string.snack_bar_text), Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();}else {
                                snackbar.dismiss();
                            }
                        }
                    });
            mAlertDialog = builder.create();
            mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            mAlertDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_REQUEST) {

            System.out.println("requestCode = ");
            // Make sure the request was successful
            switch (resultCode) {
                case 1: mLocationStatus = true;
                    break;
                case 0: mLocationStatus = false;
                    snackbar = Snackbar
                        .make((View) findViewById(R.id.mapId), getString(R.string.snack_bar_text), Snackbar.LENGTH_INDEFINITE);
                        snackbar.show();
                    System.out.println("show snackbar");
                    break;
            }
        }
    }

    private class locBR extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && cm.getActiveNetworkInfo() != null){
                    if (snackbar != null) {
                        snackbar.dismiss();
                    }
            }else {
                snackbar = Snackbar
                        .make((View) findViewById(R.id.mapId), getString(R.string.snack_bar_text), Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(mContext, "Eccept permission to use app", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
