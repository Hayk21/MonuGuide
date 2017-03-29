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


public class WebFragment extends Fragment {

    private WebView mWebView;
    private String mURL;
    private MyWebClient mMyWebClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mMyWebClient = new MyWebClient();
        if (getArguments() != null) {
            if (this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT) != null) {
                mURL = ((Monument) this.getArguments().getParcelable(StartActivity.ARGUMENT_WITH_MONUMENT)).getUrlMon();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mWebView = (WebView) view.findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(mMyWebClient);
        mWebView.loadUrl(mURL);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mWebView.canGoBack())
                mWebView.goBack();
            else
                getActivity().getFragmentManager().popBackStack();
        }
        return true;
    }

    public WebView getWebView() {
        return mWebView;
    }

    public class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }
}
