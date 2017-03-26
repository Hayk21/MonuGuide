package blue_team.com.monuguide.activities;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.transition.TransitionManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import java.util.HashMap;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.fragments.MapStatueFragment;
import blue_team.com.monuguide.fragments.SearchFragment;
import blue_team.com.monuguide.models.Monument;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String NAME_OF_PREFERENCE = "Service_runing";
    Fragment mMapStatueFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Context context;
    Toolbar toolbar;
    EditText search;
    FrameLayout frameLayout;
    LinearLayout linear;
    ImageView closeSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        search = (EditText) toolbar.findViewById(R.id.search_edit);
        closeSearch = (ImageView) toolbar.findViewById(R.id.cancel_search);
        linear = (LinearLayout) findViewById(R.id.search_linear);
        frameLayout = (FrameLayout) findViewById(R.id.search_container);
        setSupportActionBar(toolbar);

        context = this;

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        mMapStatueFragment = new MapStatueFragment();
        getLocation();
        fragmentTransaction.add(R.id.container, mMapStatueFragment, "MapFragment");
        fragmentTransaction.commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
//            Intent myIntent = new Intent(this, MonumentSearchActivity.class);
//            //myIntent.putExtra("key", value); //Optional parameters
//            this.startActivity(myIntent);
//            return true;

            return  true;

//            if (linear.getVisibility() == View.INVISIBLE) {
//                Animation animation = AnimationUtils.loadAnimation(this, R.anim.open_to_left);
//                Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.open_down);
//                SearchFragment searchFragment = new SearchFragment();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                linear.setVisibility(View.VISIBLE);
//                linear.startAnimation(animation);
//                frameLayout.setVisibility(View.VISIBLE);
//                frameLayout.startAnimation(animation2);
//                transaction.add(R.id.search_container,searchFragment,"search");
//                transaction.commit();
//                closeSearch.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Animation animation3 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.close_up);
//                        Animation animation4 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.close_to_right);
//                        FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
//                        fragmentTransaction1.remove(fragmentManager.findFragmentByTag("search"));
//                        fragmentTransaction1.commit();
//                        frameLayout.setVisibility(View.INVISIBLE);
//                        frameLayout.startAnimation(animation3);
//                        linear.setVisibility(View.INVISIBLE);
//                        linear.startAnimation(animation4);
//                    }
//                });
//            } else {
//                ((SearchFragment) fragmentManager.findFragmentByTag("search")).getFh().getSearchMonument(search.getText().toString());
//            }

        }

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
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        System.out.println("inGetConnection = " + cm.getActiveNetworkInfo());

        if (cm.getActiveNetworkInfo() == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Please connect with internet")
                    .setPositiveButton("CONNECT", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
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
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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
