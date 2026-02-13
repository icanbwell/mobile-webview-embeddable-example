package com.example.bwell_embeddable_android

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private val VALID_ENVIRONMENTS = setOf("dev", "staging", "client-sandbox", "prod")
    }

    private lateinit var webView: WebView
    private lateinit var errorContainer: ScrollView
    private lateinit var errorText: TextView
    private var hasInjectedToken = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        errorContainer = findViewById(R.id.errorContainer)
        errorText = findViewById(R.id.errorText)

        val validationErrors = validateConfig()
        if (validationErrors.isNotEmpty()) {
            showError(validationErrors.joinToString("\n"))
            return
        }

        setupWebView()
    }

    private fun validateConfig(): List<String> {
        val errors = mutableListOf<String>()

        if (BuildConfig.BWELL_ENVIRONMENT.isBlank()) {
            errors.add("BWELL_ENVIRONMENT is required")
        } else if (BuildConfig.BWELL_ENVIRONMENT !in VALID_ENVIRONMENTS) {
            errors.add("BWELL_ENVIRONMENT must be one of: ${VALID_ENVIRONMENTS.joinToString(", ")}")
        }

        if (BuildConfig.BWELL_CLIENT_ID.isBlank()) {
            errors.add("BWELL_CLIENT_ID is required")
        }

        if (BuildConfig.CLIENT_USER_TOKEN.isBlank()) {
            errors.add("CLIENT_USER_TOKEN is required")
        }

        // INITIAL_PATH is optional, defaults to empty string

        return errors
    }

    private fun showError(message: String) {
        Log.e(TAG, "Configuration error:\n$message")
        webView.visibility = View.GONE
        errorContainer.visibility = View.VISIBLE
        errorText.text = "Configuration Error:\n\n$message"
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.visibility = View.VISIBLE
        errorContainer.visibility = View.GONE

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        val userToken = BuildConfig.CLIENT_USER_TOKEN
        val tokenSetterJs = """
            (function() {
                async function setUserToken() {
                    try {
                        await bwell.setUserToken('$userToken');
                        console.log('User token set successfully');
                    } catch (e) {
                        console.error('Failed to set user token:', e);
                        var errorMsg = e.message || String(e);
                        if (errorMsg.length > 500) {
                            errorMsg = errorMsg.substring(0, 500) + '...';
                        }
                        document.body.innerHTML = '<div style="font-family: monospace; padding: 24px; background: #fff3f3; color: #d32f2f;">' +
                            '<h3 style="margin-top: 0;">Error Setting User Token</h3>' +
                            '<pre style="white-space: pre-wrap; word-break: break-word; background: #ffebee; padding: 12px; border-radius: 4px;">' +
                            errorMsg +
                            '</pre></div>';
                    }
                }

                if (typeof bwell !== 'undefined' && bwell.isInitialized) {
                    setUserToken();
                } else if (typeof bwell !== 'undefined') {
                    bwell.once('initialized', setUserToken);
                } else {
                    console.error('bwell object not found on window');
                }
            })();
        """.trimIndent()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "Page finished loading: $url")
                if (!hasInjectedToken) {
                    webView.evaluateJavascript(tokenSetterJs) { result ->
                        Log.d(TAG, "JS injection result: $result")
                    }
                    hasInjectedToken = true
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    val errorMsg = "Error loading page:\n\nURL: ${request.url}\nCode: ${error?.errorCode}\nDescription: ${error?.description}"
                    Log.e(TAG, errorMsg)
                    showError(errorMsg)
                }
            }
        }

        val url = buildUrl()
        Log.d(TAG, "Loading URL: $url")
        webView.loadUrl(url)
    }

    private fun buildUrl(): String {
        val environment = BuildConfig.BWELL_ENVIRONMENT
        val clientId = BuildConfig.BWELL_CLIENT_ID
        val initialPath = BuildConfig.INITIAL_PATH

        return "https://app.$environment.icanbwell.com/$clientId/#$initialPath"
    }
}
