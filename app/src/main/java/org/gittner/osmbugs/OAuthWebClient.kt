package org.gittner.osmbugs

import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Web client that calls a callback for all calls to a given URL
 * the "oauth_verifier" query parameter will be parsed as argument to the Callback
 */
class OAuthWebClient(verifierScheme: String, authDoneCb: AuthDone) : WebViewClient() {
    private val mCbUrl = verifierScheme
    private val mAuthDoneCb = authDoneCb

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url != null) {
            if (url.startsWith(mCbUrl)) {
                mAuthDoneCb.authDone(Uri.parse(url).getQueryParameter("oauth_verifier"))

                return true
            }
        }

        return super.shouldOverrideUrlLoading(view, url)
    }

    interface AuthDone {
        fun authDone(token: String?)
    }
}