package blue_team.com.monuguide.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.activities.StartActivity;
import blue_team.com.monuguide.models.Monument;

/**
 * Created by Hayk on 18.03.2017.
 */

public class WebFragment extends Fragment {

    public WebView webView;
    String URL;
    MyWebClient myWebClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        myWebClient = new MyWebClient();
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
        webView.setWebViewClient(myWebClient);
        webView.loadUrl(URL);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(webView.canGoBack())
                webView.goBack();
            else
            getActivity().getFragmentManager().popBackStack();
        }
        return true;
    }

    public WebView getWebView() {
        return webView;
    }

    public class MyWebClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }
}
