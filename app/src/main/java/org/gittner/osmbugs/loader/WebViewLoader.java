package org.gittner.osmbugs.loader;

import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.webkit.SslErrorHandler;
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
    /**
     * Loads a URL by using a webview. This function is only a workaround for a SSL Handshake Problem on Android 7.0 Devices.
     * See: https://stackoverflow.com/questions/39133437/sslhandshakeexception-handshake-failed-on-android-n-7-0
     * Function is blocking, so it must be called through a task.
     * @param context The Context used by the WebView
     * @param url The Url to load
     * @return The loaded Data
     * @throws ExecutionException
     * @throws InterruptedException
     */
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
                    webView.evaluateJavascript("(function(){return document.getElementsByTagName('body')[0].innerHTML})();", s -> future.complete(StringEscapeUtils.unescapeJava(s)));
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
                {
                    future.complete(null);
                }

                @Override
                public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse)
                {
                    future.complete(null);
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
                {
                    future.complete(null);
                }
            });
            webView.loadUrl(url);
        });

        return future.get();
    }
}
