package blue_team.com.monuguide.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import blue_team.com.monuguide.models.Monument;

public class DetailsFragment extends Fragment {

    LinearLayout mLinearHeart,mLinearComment;
    TextView mShortDesc,mNameMonument;
    ImageView mHeaderImage;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ProgressBar progressBar;


    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MainActivity.toggle.setDrawerIndicatorEnabled(false);
        MainActivity.toolbar.setNavigationIcon(R.drawable.ic_action_name);
        mListener.onFragmentInteraction(MainActivity.toolbar);
    }

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
       mLinearHeart = (LinearLayout)view.findViewById(R.id.linear_heart);
       mLinearComment = (LinearLayout)view.findViewById(R.id.linear_comment);
        mNameMonument = (TextView)view.findViewById(R.id.title_of_monument);
        mHeaderImage = (ImageView)view.findViewById(R.id.monument_img);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        mShortDesc = (TextView)view.findViewById(R.id.short_desc);
        if(this.getArguments() != null){
            if(this.getArguments().getParcelable(MainActivity.ARGUMENT_WITH_MONUMENT)!= null){
                mShortDesc.setText(((Monument) this.getArguments().getParcelable(MainActivity.ARGUMENT_WITH_MONUMENT)).getDesc());
                mNameMonument.setText(((Monument) this.getArguments().getParcelable(MainActivity.ARGUMENT_WITH_MONUMENT)).getName());
                Picasso.with(getActivity()).load(((Monument) this.getArguments().getParcelable(MainActivity.ARGUMENT_WITH_MONUMENT)).getImage()).into(mHeaderImage);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Toolbar toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.equals(MainActivity.toolbar.getNavigationIcon())){
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragmentManager.findFragmentByTag("DetailsFragment"));
            MainActivity.toggle.setDrawerIndicatorEnabled(true);
            fragmentTransaction.commit();
        }
        return true;
    }
}
