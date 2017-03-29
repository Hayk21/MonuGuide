package blue_team.com.monuguide.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.fragments.MapStatueFragment;
import blue_team.com.monuguide.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String SEARCH_FRAGMENT = "SearchFragment";
    public static final String MAP_FRAGMENT = "MapFragment";
    Fragment mMapStatueFragment;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    Context mContext;
    Toolbar mToolbar;
    FrameLayout mFrameLayout;
    Animation animation_open;
    Animation animation_close;
    private SearchView searchView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFrameLayout = (FrameLayout) findViewById(R.id.search_container);
        setSupportActionBar(mToolbar);
        animation_open = AnimationUtils.loadAnimation(this, R.anim.open_down);
        animation_close = AnimationUtils.loadAnimation(this, R.anim.close_up);

        mContext = this;

        mFragmentManager = getFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mMapStatueFragment = new MapStatueFragment();
        getLocation();
        mFragmentTransaction.add(R.id.container, mMapStatueFragment, MAP_FRAGMENT);
        mFragmentTransaction.commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) item.getActionView();


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mFrameLayout.setVisibility(View.INVISIBLE);
                FragmentTransaction fragmentTransaction1 = mFragmentManager.beginTransaction();
                        fragmentTransaction1.remove(mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT));
                        fragmentTransaction1.commit();
                mFrameLayout.setVisibility(View.INVISIBLE);
                mFrameLayout.startAnimation(animation_close);

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
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                });
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                transaction.add(R.id.search_container,searchFragment,SEARCH_FRAGMENT);
                transaction.commit();
                mFrameLayout.setVisibility(View.VISIBLE);
                mFrameLayout.startAnimation(animation_open);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.onActionViewCollapsed();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    System.out.println("datark");
                    ((SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT)).monumentList.clear();
                    ((SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT)).mAdapter.setMonumentList(((SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT)).monumentList);
                    ((SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT)).mAdapter.notifyDataSetChanged();
                }else {
                    System.out.println("baza");
                    ((SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT)).getFh().getSearchMonument(newText);
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



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.login) {
            Intent intent = new Intent(MainActivity.this, FacebookLoginActivity.class);
            startActivity(intent);

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

    private void getConnections() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        System.out.println("inGetConnection = " + cm.getActiveNetworkInfo());

        if (cm.getActiveNetworkInfo() == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Please connect with internet")
                    .setPositiveButton("CONNECT", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            WifiManager wifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
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

    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
