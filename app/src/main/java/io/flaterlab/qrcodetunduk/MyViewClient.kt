package io.flaterlab.qrcodetunduk

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

class MyViewClient: WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?):
            Boolean {
        val u = request?.url
        view?.loadUrl(u.toString())
        return true
    }
}