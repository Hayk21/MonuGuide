package blue_team.com.monuguide.fragments;

import android.app.Activity;
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

import java.util.HashMap;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;
import blue_team.com.monuguide.models.Note;


public class PageFragment extends Fragment {

    public static final String PAGE_NUMBER = "PageNumber";
    public static final String PAGE_URL = "PageURL";
    public static final String NOTE_ID = "NoteID";
    public static final String MONUMENT_ID = "MonumentID";
    private String mNoteID;
    private String mMonumentID;
    private String mURL;
    private ImageView mLike;
    private TextView mLikeCount;
    private Animation open, close, close2;
    private FireHelper mFireHelper = new FireHelper();
    private FireHelper.IOnFindUserLikeSuccessListener mOnFindUserLikeSuccessListener;


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
                    mLike.setTag("default");
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
                    mLike.setTag("pressed");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            String user = mFireHelper.getCurrentUid();
            if(user != null) {
                if (mLike.getTag().toString().equals("default")) {
                    mLike.setTag("pressed");
                    mLike.startAnimation(close2);
                    mFireHelper.addLike(mNoteID,user,mMonumentID);
                } else {
                    mLike.setTag("default");
                    mLike.startAnimation(close);
                    mFireHelper.subLike(mNoteID,user,mMonumentID);
                }
            }
        }
    };

    public static PageFragment newInstance(int page, Note note, Monument monument) {
        PageFragment pageFragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_NUMBER, page);
        args.putString(PAGE_URL, note.getImage());
        args.putString(NOTE_ID, note.getId());
        args.putString(MONUMENT_ID,monument.getId());
        pageFragment.setArguments(args);
        return pageFragment;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnFindUserLikeSuccessListener = new FireHelper.IOnFindUserLikeSuccessListener() {
            @Override
            public void onSuccess(HashMap<String, String> mMap) {
                if(mMap != null) {
                    if (!mMap.isEmpty()) {
                        mLike.setImageDrawable(getResources().getDrawable(R.mipmap.pressed_like_icon));
                        mLike.setTag("pressed");
                    } else {
                        mLike.setImageDrawable(getResources().getDrawable(R.mipmap.like_icon));
                        mLike.setTag("default");
                    }
                }
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        open = AnimationUtils.loadAnimation(getActivity(), R.anim.push_effect);
        close = AnimationUtils.loadAnimation(getActivity(), R.anim.pull_effect);
        close2 = AnimationUtils.loadAnimation(getActivity(), R.anim.pull_effect);
        mURL = this.getArguments().getString(PAGE_URL);
        mNoteID = this.getArguments().getString(NOTE_ID);
        mMonumentID = this.getArguments().getString(MONUMENT_ID);

        if(mFireHelper.getCurrentUid() != null)
        {
            mFireHelper.setOnFindUserLikeSuccessListener(mOnFindUserLikeSuccessListener);
            mFireHelper.findLikeUser(mNoteID,mFireHelper.getCurrentUid(),mMonumentID);
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ImageView mCurrentImage = (ImageView) view.findViewById(R.id.image_page);
        mLikeCount = (TextView) view.findViewById(R.id.like_count);
        mLike = (ImageView) view.findViewById(R.id.like_img);
        mLike.setOnClickListener(OnLikeClickListener);
        Picasso.with(getActivity()).load(mURL).into(mCurrentImage);

    }
}
