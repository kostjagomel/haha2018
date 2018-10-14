package com.intervale.testbvg;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by vmoiseenko on 16.11.2015.
 */
public class WebViewFragment extends FragmentBase {

    private static String URL = "url";
    private static String TITLE = "title";
    private static String BACK_STACK_TAG = "back_stack_tag";
    private static String TDS = "tds";
    private String url;
    private String title;
    private boolean isTds;
    private String backStackTag;
    private View progressBar;

    public WebViewFragment() {
    }

    public static WebViewFragment newInstance(String url, String backStackTag, boolean isTds) {
        Bundle args = new Bundle();
        args.putString(URL, url);
        args.putString(BACK_STACK_TAG, backStackTag);
        args.putBoolean(TDS, isTds);

        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static WebViewFragment newInstance(String url, String title) {
        Bundle args = new Bundle();
        args.putString(URL, url);
        args.putString(TITLE, title);

        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(URL);
            backStackTag = getArguments().getString(BACK_STACK_TAG);
            title = getArguments().getString(TITLE);
            isTds = getArguments().getBoolean(TDS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web_view_fragment, container, false);
        if (savedInstanceState != null) {
            url = savedInstanceState.getString(URL);
            title = savedInstanceState.getString(TITLE);
            backStackTag = savedInstanceState.getString(BACK_STACK_TAG);
        }
//        progressBar = view.findViewById(R.id.progressBar);
        final WebView webView = (WebView) view.findViewById(R.id.webView);
        webView.setWebViewClient(new CustomWebViewClient(getMainActivity()));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setLongClickable(false);
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        });
        return view;
    }

    public class CustomWebViewClient extends WebViewClient {

        private boolean isFragmentClosed = false;
        private FragmentActivity activity;

        public CustomWebViewClient(FragmentActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.startsWith(getString(R.string.back_url))) {
                getMainActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        replaceFragmentWithoutStack(R.id.fragment, new DefaultFragment());
                    }
                });

//                closeFragment();
                return;
            }
            super.onPageStarted(view, url, favicon);
            Log.d("webview","webView page started " + url);
            if (!isFragmentClosed) {
//                showProgress(null, progressBar);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("webview","webView page finished " + url);
            if (!isFragmentClosed) {
//                hideProgress(null, progressBar);
            }
//            else {
//                if(activity != null){
//                    ((MainActivity) activity).hideLockScreenView();
//                }
//            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            Log.d("webview","webView page override url " + url);
            if (!isFragmentClosed) {
                Log.d("webview","url = " + url);
                if (url.startsWith(getString(R.string.back_url))) {
//                    hideProgress(null, progressBar);
                    getMainActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            replaceFragmentWithoutStack(R.id.fragment, new DefaultFragment());
                        }
                    });
//                    closeFragment();
//                    if (secureCallback != null) {
//                    }
//                    if (secureCallback != null) {
//                        secureCallback.onSuccess3DS();
//                    }
                } else {
                    webView.loadUrl(url);
                }
            }
            return true;
        }

        private void closeFragment() {
            isFragmentClosed = true;
            getFragmentManager().popBackStack(backStackTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d("webview","webView received error " + errorCode + " description=" + description + " failingUrl=" + failingUrl);
//            view.loadUrl("file:///android_asset/html/error.html");
//            Toast.makeText(getActivity(), "Oh no! error: "+errorCode + " " + description,
//                    Toast.LENGTH_SHORT).show();
        }

        public void onReceivedSslError(WebView view, @NonNull SslErrorHandler handler, SslError error) {
            Log.d("webview","webView onReceivedSslError " + error.toString());
            if (!isFragmentClosed) {
//                hideProgress(null, progressBar);
                showSSLErrorDialog(handler);
            }
        }

        public void showSSLErrorDialog(final SslErrorHandler handler) {
//            DialogWrapper dialogWrapper = new DialogWrapper(getActivity(),
//                    getString(R.string.sslErrorDialogTitle),
//                    getString(R.string.sslErrorDialogMessage),
//                    getString(R.string.sslErrorDialogOkButtonCaption),
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (handler != null) handler.proceed();
//                        }
//                    },
//                    getString(R.string.cancel),
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (handler != null) handler.cancel();
//                        }
//                    }
//            );
//            dialogWrapper.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(URL, url);
        outState.putBoolean(TDS, isTds);
        outState.putString(BACK_STACK_TAG, backStackTag);
    }

    public boolean isDrawerEnabled() {
        return false;
    }

    public String getTitle() {
        if(!TextUtils.isEmpty(title)){
            return title;
        } else{
            return getString(R.string.app_name);
        }
    }

}
