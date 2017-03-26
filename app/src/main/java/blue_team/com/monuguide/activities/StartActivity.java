package blue_team.com.monuguide.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
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
import blue_team.com.monuguide.fragments.WebFragment;
import blue_team.com.monuguide.models.Monument;

public class StartActivity extends AppCompatActivity implements DetailsFragment.OnFragmentInteractionListener {

    public static final String ARGUMENT_WITH_MONUMENT = "CurrentMonument";
    public static final String HEADER_BACKSTACK = "HeaderBackStack";
    public static final String WEB_FRAGMENT = "WebFragment";
    public static final String DETAILS_FRAGMENT = "DeatilsFragment";
    Fragment mDetailsFragment, mWebFragment;
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
        if (mActivityIntent != null) {
            if(mActivityIntent.getParcelableExtra(LocationService.SHOWING_MONUMENT)!=null) {
                monument = mActivityIntent.getParcelableExtra(LocationService.SHOWING_MONUMENT);
            }
        }
        fragmentManager = getFragmentManager();
        args = new Bundle();

        fragmentTransaction = fragmentManager.beginTransaction();
        mDetailsFragment = new DetailsFragment();
        if (monument != null) {
            monument = mActivityIntent.getExtras().getParcelable(LocationService.SHOWING_MONUMENT);
            args.putParcelable(ARGUMENT_WITH_MONUMENT, monument);
            mDetailsFragment.setArguments(args);
            fragmentTransaction.add(R.id.start_activity_container, mDetailsFragment, DETAILS_FRAGMENT);
            fragmentTransaction.addToBackStack(HEADER_BACKSTACK);
            getSupportActionBar().setTitle(monument.getName());

            this.getIntent().removeExtra(LocationService.SHOWING_MONUMENT);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha_up,R.anim.alpha_down);
        } else {
            if (fragmentManager.findFragmentByTag(WEB_FRAGMENT) != null) {
                if (((WebFragment) fragmentManager.findFragmentByTag(WEB_FRAGMENT)).getWebView().canGoBack()) {
                    ((WebFragment) fragmentManager.findFragmentByTag(WEB_FRAGMENT)).getWebView().goBack();
                } else fragmentManager.popBackStack();
            } else
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
    public void onFragmentInteraction(int ID,Monument monument) {
        switch (ID) {
            case R.id.heart_img:
                break;
            case R.id.comment_img:
                Intent intent = new Intent(StartActivity.this,PagerActivity.class);
                intent.putExtra(ARGUMENT_WITH_MONUMENT,monument);
                startActivity(intent);
                overridePendingTransition(R.anim.alpha_up,R.anim.alpha_down);
                break;
            case R.id.wiki_img:
                mWebFragment = new WebFragment();
                args.putParcelable(ARGUMENT_WITH_MONUMENT, monument);
                mWebFragment.setArguments(args);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.replace(R.id.start_activity_container, mWebFragment, WEB_FRAGMENT);
                fragmentTransaction.addToBackStack(HEADER_BACKSTACK);
                fragmentTransaction.commit();
        }
    }


}
