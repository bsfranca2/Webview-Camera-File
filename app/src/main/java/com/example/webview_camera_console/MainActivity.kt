package com.example.webview_camera_console

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2
    private var mUploadMessageForAndroid5 : ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.loadUrl("https://qasenff.klink.ai/")
        myWebView.settings.javaScriptEnabled = true

        myWebView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d("Webview", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId())
                return super.onConsoleMessage(consoleMessage)
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }

            override fun onShowFileChooser(
                    webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
                    fileChoserParams: FileChooserParams): Boolean {
                mUploadMessageForAndroid5 = filePathCallback
                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "*/*"
                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "File Chooser")
                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5)
                return true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5) return
            val result = if (intent == null || resultCode != Activity.RESULT_OK) {
                null
            } else {
                intent.data
            }
            if (result != null) {
                mUploadMessageForAndroid5?.onReceiveValue(arrayOf(result))
            } else {
                mUploadMessageForAndroid5?.onReceiveValue(arrayOf())
            }
            mUploadMessageForAndroid5 = null
        }
    }
}