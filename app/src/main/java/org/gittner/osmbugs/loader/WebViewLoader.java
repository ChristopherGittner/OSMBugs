package org.gittner.osmbugs.loader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.text.StringEscapeUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WebViewLoader
{
    private static final long LOAD_TIMEOUT = 20 * 1000;

    /**
     * Loads a URL by using a webview. This function is only a workaround for a SSL Handshake Problem on Android 7.0 Devices.
     * See: https://stackoverflow.com/questions/39133437/sslhandshakeexception-handshake-failed-on-android-n-7-0
     * Function is blocking, so it must be called through a task.
     * @param context The Context used by the WebView
     * @param url The Url to load
     * @return The loaded Data
     */
    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String loadUrl(Context context, String url) throws ExecutionException, InterruptedException
    {
        CompletableFuture<String> future = new CompletableFuture<>();

        new Handler(Looper.getMainLooper()).post(() -> {
            WebView webView = new WebView(context);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url)
                {
                    /* Grab the result data from the web page through javascript */
                    webView.evaluateJavascript("(function(){return document.getElementsByTagName('body')[0].innerHTML})();", s -> future.complete(StringEscapeUtils.unescapeJava(s)));
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
                {
                    /* Catch errors while loading */
                    future.complete(null);
                }
            });

            /* Timeout after which we return a null value */
            new Handler(Looper.getMainLooper()).postDelayed(() -> future.complete(null), LOAD_TIMEOUT);

            webView.loadUrl(url);
        });

        return future.get();
    }
}
