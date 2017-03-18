package blue_team.com.monuguide.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.Services.LocationService;
import blue_team.com.monuguide.fragments.DetailsFragment;
import blue_team.com.monuguide.models.Monument;

public class StartActivity extends AppCompatActivity {

    public static final String ARGUMENT_WITH_MONUMENT = "CurrentMonument";
    public static final String HEADER_BACKSTACK = "HeaderBackStack";
    Fragment mDetailsFragment;
    Monument monument;
    Intent mActivityIntent;
    Bundle args;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setupActionBar();
        mActivityIntent = getIntent();
        fragmentManager = getFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();
        mDetailsFragment = new DetailsFragment();
        if (mActivityIntent.getExtras() != null) {
            if (mActivityIntent.getParcelableExtra(LocationService.SHOWING_MONUMENT) != null) {
                monument = mActivityIntent.getExtras().getParcelable(LocationService.SHOWING_MONUMENT);
                args = new Bundle();
                args.putParcelable(ARGUMENT_WITH_MONUMENT, monument);
                mDetailsFragment.setArguments(args);
                fragmentTransaction.add(R.id.start_activity_container, mDetailsFragment, "DetailsFragment");
                fragmentTransaction.addToBackStack(HEADER_BACKSTACK);

                this.getIntent().removeExtra(LocationService.SHOWING_MONUMENT);
            }
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
