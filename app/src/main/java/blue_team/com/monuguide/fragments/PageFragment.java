package blue_team.com.monuguide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.models.Note;

import static android.R.attr.animation;


public class PageFragment extends Fragment {

    public static final String PAGE_NUMBER = "PageNumber";
    public static final String PAGE_URL = "PageURL";
    int mPageNumber;
    String URL;
    ImageView mCurrentImage,mLike;
    Animation open,close,close2;


    View.OnClickListener OnLikeClickListener = new View.OnClickListener() {


        @Override
        public void onClick(View view) {
            close.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mLike.setImageDrawable(getResources().getDrawable(R.mipmap.like_icon));
                    mLike.startAnimation(open);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            close2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mLike.setImageDrawable(getResources().getDrawable(R.mipmap.pressed_like_icon));
                    mLike.startAnimation(open);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if(mLike.getTag().toString().equals("default")){
                mLike.setTag("pressed");
                mLike.startAnimation(close2);
            }else {
                mLike.setTag("default");
                mLike.startAnimation(close);
            }
        }
    };

    public static PageFragment newInstance(int page,Note note){
        PageFragment pageFragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_NUMBER,page);
        args.putString(PAGE_URL,note.getImage());
        pageFragment.setArguments(args);
        return pageFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        open = AnimationUtils.loadAnimation(getActivity(), R.anim.push_effect);
        close = AnimationUtils.loadAnimation(getActivity(), R.anim.pull_effect);
        close2 = AnimationUtils.loadAnimation(getActivity(), R.anim.pull_effect);
        mPageNumber = this.getArguments().getInt(PAGE_NUMBER);
        URL = this.getArguments().getString(PAGE_URL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mCurrentImage = (ImageView)view.findViewById(R.id.image_page);
        mLike = (ImageView)view.findViewById(R.id.like_img);
        mLike.setOnClickListener(OnLikeClickListener);
        Picasso.with(getActivity()).load(URL).into(mCurrentImage);
    }
}
