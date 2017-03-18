package blue_team.com.monuguide.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.activities.MainActivity;
import blue_team.com.monuguide.activities.StartActivity;
import blue_team.com.monuguide.models.Monument;

public class DetailsFragment extends Fragment {

    private static final int MESSAGE_FOR_HANDLER = 18;
    LinearLayout mLinearHeart, mLinearComment;
    TextView mShortDesc, mNameMonument;
    ImageView mHeaderImage;
    FragmentManager fragmentManager;
    ProgressBar progressBar;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Toolbar toolbar);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if(message.what == MESSAGE_FOR_HANDLER)
                progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
    });


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fragmentManager = getActivity().getFragmentManager();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mLinearHeart = (LinearLayout) view.findViewById(R.id.linear_heart);
        mLinearComment = (LinearLayout) view.findViewById(R.id.linear_comment);
        mNameMonument = (TextView) view.findViewById(R.id.title_of_monument);
        mHeaderImage = (ImageView) view.findViewById(R.id.monument_img);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mShortDesc = (TextView) view.findViewById(R.id.short_desc);
        if (this.getArguments() != null) {
            if (this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT) != null) {
                mShortDesc.setText(((Monument) this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT)).getDesc());
                mNameMonument.setText(((Monument) this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT)).getName());
                Picasso.with(getActivity()).load(((Monument) this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT)).getImage()).into(mHeaderImage);
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mHeaderImage.getDrawable() == null){

                        }
                        handler.sendEmptyMessage(MESSAGE_FOR_HANDLER);
                    }
                });
                thread.start();
            }
        }
    }

    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}