package blue_team.com.monuguide.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.activities.MainActivity;
import blue_team.com.monuguide.activities.StartActivity;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

public class DetailsFragment extends Fragment {

    private ProgressBar mProgressBar;
    private Monument mMonument;
    private ImageView mFavorites;
    private FireHelper mFireHelper = new FireHelper();
    private FireHelper.IOnFindFavMonSuccessListener mFindFavMonSuccessListener;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int ID, Monument monument, ImageView view);
    }

    View.OnClickListener onIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mListener.onFragmentInteraction(view.getId(), mMonument, ((ImageView) view));
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (this.getArguments() != null) {
            if (this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT) != null) {
                mMonument = this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT);
            }
        }
        mFindFavMonSuccessListener = new FireHelper.IOnFindFavMonSuccessListener() {
            @Override
            public void onSuccess(HashMap<String, Monument> mMap) {
                if (!mMap.isEmpty()) {
                    mFavorites.setTag("pressed");
                    mFavorites.setImageDrawable(getResources().getDrawable(R.mipmap.pressed_star_icon));
                } else {
                    mFavorites.setTag("default");
                    mFavorites.setImageDrawable(getResources().getDrawable(R.mipmap.star_icon7));
                }
            }
        };
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        startFragmentOperation(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mFireHelper.getCurrentUid() != null) {
            mFireHelper.setOnFindFavMonSuccessListener(mFindFavMonSuccessListener);
            mFireHelper.findFavMon(mFireHelper.getCurrentUid(), mMonument);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.alpha_up, R.anim.alpha_down);
        }
        return true;
    }

    public void startFragmentOperation(View view) {
        ImageView location, notes, wiki, headerImage;
        TextView shortDesc;
        location = (ImageView) view.findViewById(R.id.location_img);
        notes = (ImageView) view.findViewById(R.id.comment_img);
        mFavorites = (ImageView) view.findViewById(R.id.heart_img);
        wiki = (ImageView) view.findViewById(R.id.wiki_img);
        headerImage = (ImageView) view.findViewById(R.id.monument_img);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        shortDesc = (TextView) view.findViewById(R.id.short_desc);
        location.setOnClickListener(onIconClickListener);
        notes.setOnClickListener(onIconClickListener);
        mFavorites.setOnClickListener(onIconClickListener);
        wiki.setOnClickListener(onIconClickListener);
        if (mMonument != null) {
            shortDesc.setText(((Monument) this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT)).getDesc());
            Picasso.with(getActivity()).load(((Monument) this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT)).getImage()).into(headerImage, new Callback() {
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
}
