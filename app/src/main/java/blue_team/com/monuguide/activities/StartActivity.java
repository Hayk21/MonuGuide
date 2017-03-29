package blue_team.com.monuguide.activities;

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
    private Monument mMonument;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setupActionBar();
        if (getIntent() != null) {
            if (getIntent().getParcelableExtra(LocationService.SHOWING_MONUMENT) != null) {
                mMonument = getIntent().getParcelableExtra(LocationService.SHOWING_MONUMENT);
            }
        }
        mFragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        args = new Bundle();
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
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
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
    public void onFragmentInteraction(int ID, Monument monument) {
        switch (ID) {
            case R.id.heart_img:
                break;
            case R.id.comment_img:
                Intent intent = new Intent(StartActivity.this, PagerActivity.class);
                intent.putExtra(ARGUMENT_WITH_MONUMENT, monument);
                startActivity(intent);
                overridePendingTransition(R.anim.alpha_up, R.anim.alpha_down);
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


}
