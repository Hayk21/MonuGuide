package blue_team.com.monuguide.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.activities.MainActivity;
import blue_team.com.monuguide.activities.StartActivity;
import blue_team.com.monuguide.models.Monument;

/**
 * Created by Hayk on 18.03.2017.
 */

public class WebFragment extends Fragment {

    WebView webView;
    String URL;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getArguments() != null) {
            if (this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT) != null) {
                URL = ((Monument) this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT)).getUrlMon();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        webView = (WebView)view.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(URL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            getActivity().getFragmentManager().popBackStack();
        }
        return true;
    }
}
