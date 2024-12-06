package com.khan.fftracker.mvvmUtils.custom_binding_adaptor

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.BindingAdapter
import com.khan.fftracker.logCalls.LogCalls_Debug

@BindingAdapter(value = ["app:webViewUrl","app:setWebViewClient"], requireAll = true)
fun loadUrl(webView: WebView, strUrl:String, webViewClient: WebViewClient )
{
    LogCalls_Debug.d(LogCalls_Debug.TAG, "$strUrl loadUrl")
    LogCalls_Debug.d(LogCalls_Debug.TAG, "webViewClient $webViewClient")
    val webSettings: WebSettings = webView.settings
    webSettings.javaScriptEnabled = true

    webSettings.useWideViewPort = true
    webSettings.loadWithOverviewMode = true
    ///Zoom enable in webview
    webSettings.builtInZoomControls = true
    //hide zoom button in webview
    webSettings.displayZoomControls = false
    //save no cache
    webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
    webSettings.domStorageEnabled = true

    webView.webViewClient= webViewClient

    webView.loadUrl(strUrl)
}