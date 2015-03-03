package ly.priv.mobile.gui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import ly.priv.mobile.R;
import ly.priv.mobile.utils.ConstantValues;
import ly.priv.mobile.utils.JsObject;
import ly.priv.mobile.utils.Utilities;

public class PrivlyApplicationFragment extends Fragment {

    private String LOGTAG = getClass().getSimpleName();

    public PrivlyApplicationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_privly_application, container, false);
        WebView webview = (WebView) rootView.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.addJavascriptInterface(new JsObject(getActivity()),
                ConstantValues.JAVASCRIPT_BRIDGE_NAME);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            webview.getSettings().setAllowUniversalAccessFromFileURLs(true);

        // Logs all Js Console messages on the logcat.
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d(LOGTAG,
                        cm.message() + " -- From line " + cm.lineNumber()
                                + " of " + cm.sourceId());
                return true;
            }
        });

        webview.loadUrl(Utilities.getFilePathURLFromAppName(getArguments().getString(ConstantValues.PRIVLY_APPLICATION_KEY)));
        webview.loadUrl("javascript: window.onload = function() {document.getElementsByClassName('navbar-toggle')[0].style.visibility = 'hidden';"
                + "document.getElementsByClassName('collapse navbar-collapse')[0].style.visibility = 'hidden';}");
        return rootView;
    }
}
