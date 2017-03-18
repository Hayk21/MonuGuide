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
import blue_team.com.monuguide.fragments.NotesFragment;
import blue_team.com.monuguide.fragments.WebFragment;
import blue_team.com.monuguide.models.Monument;

public class StartActivity extends AppCompatActivity implements DetailsFragment.OnFragmentInteractionListener {

    public static final String ARGUMENT_WITH_MONUMENT = "CurrentMonument";
    public static final String HEADER_BACKSTACK = "HeaderBackStack";
    Fragment mDetailsFragment,mNotesFragment,mWebFragment;
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
        monument = mActivityIntent.getParcelableExtra(LocationService.SHOWING_MONUMENT);
        fragmentManager = getFragmentManager();
        args = new Bundle();

        fragmentTransaction = fragmentManager.beginTransaction();
        mDetailsFragment = new DetailsFragment();
        if (mActivityIntent.getExtras() != null) {
            if (mActivityIntent.getParcelableExtra(LocationService.SHOWING_MONUMENT) != null) {
                monument = mActivityIntent.getExtras().getParcelable(LocationService.SHOWING_MONUMENT);
                args.putParcelable(ARGUMENT_WITH_MONUMENT,monument);
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
        if(fragmentManager.getBackStackEntryCount() == 1){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);}
        else {
            fragmentManager.popBackStack();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onFragmentInteraction(int ID) {
        switch (ID){
            case R.id.linear_heart:
                break;
            case R.id.linear_comment:
                mNotesFragment = new NotesFragment();
                mNotesFragment.setArguments(args);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.start_activity_container,mNotesFragment,"NotesFragment");
                fragmentTransaction.addToBackStack(HEADER_BACKSTACK);
                fragmentTransaction.commit();
                break;
            case R.id.linear_wiki:
                mWebFragment = new WebFragment();
                args.putParcelable(ARGUMENT_WITH_MONUMENT,monument);
                mWebFragment.setArguments(args);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.start_activity_container,mWebFragment,"WebFragment");
                fragmentTransaction.addToBackStack(HEADER_BACKSTACK);
                fragmentTransaction.commit();
        }
    }
}
