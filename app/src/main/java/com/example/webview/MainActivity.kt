package com.example.webview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.session.PlaybackState.CustomAction
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import com.example.webview.databinding.ActivityMainBinding
import com.example.webview.util.Constants

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var idText: String = ""
    private var uuidText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.wvMain.settings.javaScriptEnabled = true
        binding.wvMain.webViewClient = Loader()

        binding.pbStart.setOnClickListener {
            binding.pbStart.visibility = View.INVISIBLE
            binding.wvMain.loadUrl(Constants.URL)
        }
    }

    inner class Loader : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.pbStart.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.pbMain.visibility = View.INVISIBLE
            binding.pbStart.visibility = View.GONE
            binding.wvMain.visibility = View.VISIBLE
            binding.containerIds.visibility = View.VISIBLE
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            Toast.makeText(
                binding.root.context,
                "Page error: ${error?.description}",
                Toast.LENGTH_LONG
            ).show()
        }

        @SuppressLint
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val uri = Uri.parse(request?.url.toString())
            idText = uri.getQueryParameter("id") ?: "id = null"
            uuidText = uri.getQueryParameter("uuid") ?: "uuid = null"
            addTextView(idText, uuidText)
            savePref()
            return super.shouldOverrideUrlLoading(view, request)
        }

        fun addTextView(type: String, desc: String) {
            val textType = TextView(binding.root.context)
            textType.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textType.text = type

            val textDesc = TextView(binding.root.context)
            textDesc.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textDesc.text = desc

            binding.containerIds.addView(textType)
            binding.containerIds.addView(textDesc)
        }

        fun savePref() {
            val sharedPreferences = getSharedPreferences("URL", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("key_id", idText)
            editor.putString("key_uuid", uuidText)
        }
    }
}