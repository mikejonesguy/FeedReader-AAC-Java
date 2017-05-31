package feedreader.aac.java;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import feedreader.aac.java.viewmodel.SharedViewModel;

public class ArticleDetailFragment extends LifecycleFragment {

    public static final String ARG_TITLE = "title";
    public static final String ARG_HTML = "html";

    private WebView webView;

    public ArticleDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        webView = (WebView) rootView.findViewById(R.id.webView);
        return rootView;
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // for mobile-friendly viewing and video support
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        setDetails(getArguments().getString(ARG_HTML));

        SharedViewModel sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelected().observe((LifecycleOwner) getActivity(),
                article -> setDetails(article == null ? null : article.getHtml()));
    }

    private void setDetails(String html) {
        // add a little CSS styling to make the content look nice (images and videos fit width)
        String formattedHtml = "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>" +
                "<style>body { margin: 0px; padding: 2%; } img { width:100%; } " +
                "img[width] { width:inherit; } div.video_iframe iframe { width:100%; height:56vw; margin: auto; }" +
                "</style></head><body>\n" + (TextUtils.isEmpty(html) ? "" : html) + "\n</body></html>";

        // load the html content
        webView.loadData(formattedHtml, "text/html; charset=UTF-8", "UTF-8");
    }
}
