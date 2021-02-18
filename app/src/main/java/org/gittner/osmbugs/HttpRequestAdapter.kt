package org.gittner.osmbugs

import com.github.kittinunf.fuel.core.Request
import oauth.signpost.http.HttpRequest
import java.io.InputStream
import java.net.URL

/**
 * Adapter to enable OAuth request signing for Fuel
 */
class HttpRequestAdapter(request: Request) : HttpRequest {
    private val mRequest = request

    override fun setHeader(name: String, value: String) {
        mRequest[name] = value
    }

    override fun getMessagePayload(): InputStream {
        return mRequest.body.toStream()
    }

    override fun setRequestUrl(url: String) {
        mRequest.url = URL(url)
    }

    override fun getMethod(): String {
        return mRequest.method.value
    }

    override fun getRequestUrl(): String {
        return mRequest.url.toString()
    }

    override fun getHeader(name: String): String {
        return mRequest.headers[name].toString()
    }

    override fun getAllHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()

        mRequest.headers.transformIterate(headers::set)

        return headers
    }

    override fun getContentType(): String {
        return mRequest["ContentType"].toString()
    }

    override fun unwrap(): Any {
        throw RuntimeException("Not implemented")
    }

}