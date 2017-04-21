package blue_team.com.monuguide.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.activities.FacebookLoginActivity;
import blue_team.com.monuguide.activities.PagerActivity;
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
    private int mCountLike;
    private ImageView mLike,mDelete;
    private TextView mLikeCount;
    private Animation mOpen, mClose, mClose2;
    private AlertDialog mAlertDialog;
    private ArrayList<Note> mNotesList;
    private FireHelper mFireHelper = new FireHelper();
    private FireHelper.IOnFindUserLikeSuccessListener mOnFindUserLikeSuccessListener;
    private FireHelper.IOnGetLikeCountSuccessListener mOnGetLikeCountSuccessListener;
    private FireHelper.IOnFindNoteListener mOnFindNoteListener;
    private FireHelper.IOnDeleteNoteListener mOnDeleteNoteListener;
    private ProgressBar mProgressBar;
    static int number;
    FireHelper.IOnRemoveItemListener onRemoveItemListener = new FireHelper.IOnRemoveItemListener() {
        @Override
        public void onSuccess(String noteId) {

        }
    };


    View.OnClickListener OnLikeClickListener = new View.OnClickListener() {


        @Override
        public void onClick(View view) {
            mClose.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mLike.setImageDrawable(getResources().getDrawable(R.mipmap.like_icon));
                    mLike.startAnimation(mOpen);
                    mLike.setTag("default");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mClose2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mLike.setImageDrawable(getResources().getDrawable(R.mipmap.pressed_like_icon));
                    mLike.startAnimation(mOpen);
                    mLike.setTag("pressed");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            String user = mFireHelper.getCurrentUid();
            if (user != null) {
                if (mLike.getTag().toString().equals("default")) {
                    mLike.setTag("pressed");
                    mLike.startAnimation(mClose2);
                    mFireHelper.addLike(mNoteID, user, mMonumentID);
                } else {
                    mLike.setTag("default");
                    mLike.startAnimation(mClose);
                    mFireHelper.subLike(mNoteID, user, mMonumentID);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Attention").setMessage("If you want to like this note,log in with facebook.");
                builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mAlertDialog.cancel();
                    }
                });
                builder.setNegativeButton(R.string.login_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), FacebookLoginActivity.class);
                        startActivity(intent);
                    }
                });
                mAlertDialog = builder.create();
                mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                mAlertDialog.show();
            }
        }
    };

    public static PageFragment newInstance(int page, Note note, Monument monument) {
        PageFragment pageFragment = new PageFragment();
        Bundle args = new Bundle();
        number = page;
        args.putInt(PAGE_NUMBER, page);
        args.putString(PAGE_URL, note.getImage());
        args.putString(NOTE_ID, note.getId());
        args.putString(MONUMENT_ID, monument.getId());
        pageFragment.setArguments(args);
        return pageFragment;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnFindUserLikeSuccessListener = new FireHelper.IOnFindUserLikeSuccessListener() {
            @Override
            public void onSuccess(HashMap<String, String> mMap) {
                if (mMap != null) {
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

        mOnGetLikeCountSuccessListener = new FireHelper.IOnGetLikeCountSuccessListener() {
            @Override
            public void onSuccess(int likeCount) {
                mCountLike = likeCount;
                String lc = mCountLike + " ";
                mLikeCount.setText(lc);
            }
        };

        mOnFindNoteListener = new FireHelper.IOnFindNoteListener() {
            @Override
            public void onSuccess(HashMap<String, Note> mNote) {
                mNotesList.addAll(mNote.values());
                String userId = mFireHelper.getCurrentUid();
                if (mNotesList != null && userId != null) {
                    if (!mNotesList.isEmpty()) {
                        if(mNotesList.get(0).getUid().equals(userId))
                        mDelete.setVisibility(View.VISIBLE);
                    } else {
                        mDelete.setVisibility(View.INVISIBLE);
                    }
                }
                else
                {
                    mDelete.setVisibility(View.INVISIBLE);
                }
            }
        };

        mOnDeleteNoteListener = new FireHelper.IOnDeleteNoteListener() {
            @Override
            public void doingSomething(String noteId) {
                //mFireHelper.getOnRemoveListener().onSuccess(noteId);
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.push_effect);
        mClose = AnimationUtils.loadAnimation(getActivity(), R.anim.pull_effect);
        mClose2 = AnimationUtils.loadAnimation(getActivity(), R.anim.pull_effect);
        mURL = this.getArguments().getString(PAGE_URL);
        mNoteID = this.getArguments().getString(NOTE_ID);
        mMonumentID = this.getArguments().getString(MONUMENT_ID);
        mNotesList = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFireHelper.setOnGetLikeCountSuccessListener(mOnGetLikeCountSuccessListener);
        mFireHelper.getLikeCount(mMonumentID, mNoteID);
        if (mFireHelper.getCurrentUid() != null) {
            mFireHelper.setOnFindUserLikeSuccessListener(mOnFindUserLikeSuccessListener);
            mFireHelper.findLikeUser(mNoteID, mFireHelper.getCurrentUid(), mMonumentID);
        }
        mFireHelper.setOnFindNoteListener(mOnFindNoteListener);
        mFireHelper.findNoteById(mNoteID, mMonumentID);
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
        mDelete = (ImageView) view.findViewById(R.id.delete_note);
        mLike.setOnClickListener(OnLikeClickListener);

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFireHelper.setOnRemoveListener(onRemoveItemListener);
                mFireHelper.setOnDeleteNoteListener(mOnDeleteNoteListener);
                mFireHelper.deleteNote(mNoteID, mMonumentID);
//                mFireHelper.getOnRemoveListener().onSuccess(mNoteID);
            }
        });
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_page);
        Picasso.with(getActivity()).load(mURL).into(mCurrentImage, new Callback() {
            @Override
            public void onSuccess() {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });
    }


}
