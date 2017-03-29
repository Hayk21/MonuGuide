package blue_team.com.monuguide.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.activities.MainActivity;
import blue_team.com.monuguide.activities.StartActivity;
import blue_team.com.monuguide.models.Monument;

public class DetailsFragment extends Fragment {

    private static final int MESSAGE_FOR_HANDLER = 18;
    public static final String SAVED_MONUMENT = "SavedMonument";
    private ImageView mHeaderImage;
    private ProgressBar mProgressBar;
    private Monument mMonument;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int ID,Monument monument);
    }

    View.OnClickListener onIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mListener.onFragmentInteraction(view.getId(),mMonument);
        }
    };

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == MESSAGE_FOR_HANDLER)
                mProgressBar.setVisibility(View.INVISIBLE);
            return false;
        }
    });


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
                mMonument = savedInstanceState.getParcelable(SAVED_MONUMENT);
        } else if (this.getArguments() != null) {
            if (this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT) != null) {
                mMonument = this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT);
            }
        }
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void startFragmentOperation(View view) {
        ImageView notes,favorites,wiki;
        TextView shortDesc;
        notes = (ImageView)view.findViewById(R.id.comment_img);
        favorites = (ImageView)view.findViewById(R.id.heart_img);
        wiki = (ImageView)view.findViewById(R.id.wiki_img);
        mHeaderImage = (ImageView) view.findViewById(R.id.monument_img);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        shortDesc = (TextView) view.findViewById(R.id.short_desc);
        notes.setOnClickListener(onIconClickListener);
        favorites.setOnClickListener(onIconClickListener);
        wiki.setOnClickListener(onIconClickListener);
        if (mMonument != null) {
            shortDesc.setText(((Monument) this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT)).getDesc());
            Picasso.with(getActivity()).load(((Monument) this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT)).getImage()).into(mHeaderImage);
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mHeaderImage.getDrawable() == null) {

                    }
                    handler.sendEmptyMessage(MESSAGE_FOR_HANDLER);
                }
            });
            thread.start();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.alpha_up,R.anim.alpha_down);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVED_MONUMENT, mMonument);
    }
}
