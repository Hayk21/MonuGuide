package blue_team.com.monuguide.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.Services.LocationService;
import blue_team.com.monuguide.fragments.DetailsFragment;
import blue_team.com.monuguide.fragments.MapStatueFragment;
import blue_team.com.monuguide.models.Monument;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DetailsFragment.OnFragmentInteractionListener {

    public static final String NAME_OF_PREFERENCE = "Service_runing";
    public static final String ARGUMENT_WITH_MONUMENT = "CurrentMonument";
    public static final String HEADER_BACKSTACK = "HeaderBackStack";
    Fragment mDetailsFragment, mMapStatueFragment;
    Monument monument;
    Intent mActivityIntent;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Bundle args;
    public static ActionBarDrawerToggle toggle;
    public static DrawerLayout drawer;
    public static Toolbar toolbar;

    public void setToll(Toolbar toolbar){
        this.setSupportActionBar(toolbar);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivityIntent = getIntent();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        mMapStatueFragment = new MapStatueFragment();
        mDetailsFragment = new DetailsFragment();
        if (mActivityIntent.getExtras() != null) {
            if (mActivityIntent.getParcelableExtra(LocationService.SHOWING_MONUMENT) != null) {
                monument = mActivityIntent.getExtras().getParcelable(LocationService.SHOWING_MONUMENT);
                args = new Bundle();
                args.putParcelable(ARGUMENT_WITH_MONUMENT, monument);
                mDetailsFragment.setArguments(args);
                fragmentTransaction.add(R.id.container, mDetailsFragment,"DetailsFragment");

                this.getIntent().removeExtra(LocationService.SHOWING_MONUMENT);
            } else {
                fragmentTransaction.add(R.id.container, mMapStatueFragment, "MapFragment");
                fragmentTransaction.addToBackStack(HEADER_BACKSTACK);
            }
        } else {
            fragmentTransaction.add(R.id.container, mMapStatueFragment, "MapFragment");
            fragmentTransaction.addToBackStack(HEADER_BACKSTACK);
        }
        fragmentTransaction.commit();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DrawingActivity.class);
                startActivity(intent);
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
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
        if(item.getItemId() == android.R.id.home){
            Log.d("Log_Tag","ayoooooooooooooooooooooo");
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(final Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragmentManager.findFragmentByTag("DetailsFragment"));
                fragmentTransaction.add(R.id.container,mMapStatueFragment,"MapFragment");
                toolbar.getNavigationIcon().setVisible(false,true);
                toggle.setDrawerIndicatorEnabled(true);
                toggle.syncState();
                fragmentTransaction.commit();
            }
        });
    }
}
