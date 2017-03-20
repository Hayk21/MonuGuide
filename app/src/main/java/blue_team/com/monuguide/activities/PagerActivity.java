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
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    Monument monument;
    TextView mName,mNothing;
    ImageView mBack,mDraw;
    FireHelper fireHelper = new FireHelper();

    List<Note> mListOfNote;
    private FireHelper.IOnNoteSuccessListener iOnNoteSuccessListener = new FireHelper.IOnNoteSuccessListener() {
        @Override
        public void onSuccess(HashMap<String, Note> mMap) {
            mListOfNote.clear();
            mListOfNote.addAll(mMap.values());
            if(!mListOfNote.isEmpty()){
                viewPager.setAdapter(pagerAdapter);
                mSize = mListOfNote.size();
            }
            else {
                viewPager.setVisibility(View.GONE);
                mNothing.setVisibility(View.VISIBLE);
            }
            //anel gorcoxutyunner@
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        fireHelper.setOnNoteSuccessListener(iOnNoteSuccessListener);

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        mName = (TextView)findViewById(R.id.name_of_monument);
        mNothing = (TextView)findViewById(R.id.nothing_id);
        mBack = (ImageView)findViewById(R.id.home_img);
        mDraw = (ImageView)findViewById(R.id.draw_img);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        if(this.getIntent() != null){
            if(this.getIntent().getParcelableExtra(StartActivity.ARGUMENT_WITH_MONUMENT) != null) {
                monument = this.getIntent().getParcelableExtra(StartActivity.ARGUMENT_WITH_MONUMENT);
                mName.setText(monument.getName());
                mListOfNote = new ArrayList<>();
                fireHelper.getNotesList(monument.getId());
//                    list.add("https://www.iposters.co.uk/media/catalog/product/cache/1/small_image/300x400/9df78eab33525d08d6e5fb8d27136e95/0/3/0359CH_3.jpg");
//                    list.add("https://s-media-cache-ak0.pinimg.com/originals/c4/74/bd/c474bd3b777ac3b7bf59fedac4d7d8d7.jpg");
//                    list.add("https://www.iposters.co.uk/media/catalog/product/cache/1/small_image/300x400/9df78eab33525d08d6e5fb8d27136e95/0/5/0583CH_3.jpg");


            }
        }

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PagerActivity.this.finish();
            }
        });

        mDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PagerActivity.this,DrawingActivity.class);
                intent.putExtra(EXTRA_WITH_MONUMENT,monument);
                intent.putExtra(EXTRA_WITH_SIZE,mSize);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFirstCommit  && viewPager.getVisibility() == View.GONE) {
            mNothing.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            pagerAdapter.notifyDataSetChanged();
        }
    }

    public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position,mListOfNote.get(position));
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
