package blue_team.com.monuguide.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.fragments.PageFragment;
import blue_team.com.monuguide.models.Monument;
import blue_team.com.monuguide.models.Note;

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
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        mFireHelper.setOnNoteSuccessListener(iOnNoteSuccessListener);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mNothingText = (TextView) findViewById(R.id.nothing_id);
        TextView name = (TextView) findViewById(R.id.name_of_monument);
        ImageView back = (ImageView) findViewById(R.id.home_img);
        ImageView draw = (ImageView) findViewById(R.id.draw_img);
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
                PagerActivity.this.finish();
                overridePendingTransition(R.anim.alpha_up, R.anim.alpha_down);
            }
        });

        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PagerActivity.this, DrawingActivity.class);
                intent.putExtra(EXTRA_WITH_MONUMENT, mMonument);
                intent.putExtra(EXTRA_WITH_SIZE, mSize);
                startActivity(intent);
                overridePendingTransition(R.anim.draw_open_anim, R.anim.draw_alpha_down);
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
    protected void onResume() {
        super.onResume();
        if (mFirstCommit && mViewPager.getVisibility() == View.GONE) {
            mNothingText.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position, mListOfNote.get(position));
        }

        @Override
        public int getCount() {
            return mListOfNote.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Image " + position;
        }
    }

    public static void setFirstCommit(boolean firstCommit) {
        mFirstCommit = firstCommit;
    }

}
