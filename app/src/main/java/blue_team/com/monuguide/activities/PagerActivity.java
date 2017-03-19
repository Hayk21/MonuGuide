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
import java.util.List;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.fragments.PageFragment;
import blue_team.com.monuguide.models.Monument;
import blue_team.com.monuguide.models.Note;

public class PagerActivity extends FragmentActivity {

    public static final String EXTRA_WITH_MONUMENT_ID = "ExtraMonumentID";
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    Monument monument;
    TextView mName;
    ImageView mBack,mDraw;
    List<Note> mNotesList;
    List<String> list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        mName = (TextView)findViewById(R.id.name_of_monument);
        mBack = (ImageView)findViewById(R.id.home_img);
        mDraw = (ImageView)findViewById(R.id.draw_img);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        if(this.getIntent() != null){
            if(this.getIntent().getParcelableExtra(StartActivity.ARGUMENT_WITH_MONUMENT) != null)
                monument = this.getIntent().getParcelableExtra(StartActivity.ARGUMENT_WITH_MONUMENT);
            mName.setText(monument.getName());
//                mNotesList = new ArrayList<>();
//                if(monument.getNotes() != null) {
//                    mNotesList.addAll(monument.getNotes().values());
                    list = new ArrayList<>();
                    list.add("https://www.iposters.co.uk/media/catalog/product/cache/1/small_image/300x400/9df78eab33525d08d6e5fb8d27136e95/0/3/0359CH_3.jpg");
                    list.add("https://s-media-cache-ak0.pinimg.com/originals/c4/74/bd/c474bd3b777ac3b7bf59fedac4d7d8d7.jpg");
                    list.add("https://www.iposters.co.uk/media/catalog/product/cache/1/small_image/300x400/9df78eab33525d08d6e5fb8d27136e95/0/5/0583CH_3.jpg");
//                }
//            }
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
                intent.putExtra(EXTRA_WITH_MONUMENT_ID,monument.getId());
                startActivity(intent);
            }
        });


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position,list.get(position));
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Image " + position;
        }
    }

}
