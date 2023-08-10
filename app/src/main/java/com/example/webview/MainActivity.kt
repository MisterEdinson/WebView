package com.example.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.webview.databinding.ActivityMainBinding
import com.example.webview.ui.home.SavedIds
import com.example.webview.ui.home.invisible
import com.example.webview.ui.home.visible
import com.example.webview.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var keyId: String? = null
    private var keyUuid: String? = null
    lateinit var pref: SavedIds
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        pref = SavedIds(this)
        setContentView(binding.root)

        binding.wvMain.settings.javaScriptEnabled = true
        binding.wvMain.webViewClient = client

        readKey()

        binding.apply {
            btnStart.setOnClickListener {
                btnStart.invisible()
                wvMain.loadUrl(Constants.URL)
            }
        }
    }

    private fun readKey() {
        keyId = pref.read(Constants.ID)
        keyUuid = pref.read(Constants.UUID)

        binding.tvID.text = getString(R.string.str_id_show, keyId)
        binding.tvUuid.text = getString(R.string.str_uuid_show, keyUuid)
    }

    private val client = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.progressLoad.visible()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.apply {
                progressLoad.invisible()
                btnStart.invisible()
                wvMain.visible()
            }
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            Toast.makeText(
                binding.root.context,
                getString(R.string.str_error_show, error?.description),
                Toast.LENGTH_LONG
            ).show()
        }

        @SuppressLint
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val uri = Uri.parse(request?.url.toString())

            keyId = uri.getQueryParameter(Constants.ID)
            keyUuid = uri.getQueryParameter(Constants.UUID)

            if (keyId != null || keyUuid != null) {
                pref.saved(Constants.ID, keyId)
                pref.saved(Constants.UUID, keyUuid)
            }

            binding.apply {
                tvID.text = getString(R.string.str_id_show, pref.read(Constants.ID))
                tvUuid.text = getString(R.string.str_uuid_show, pref.read(Constants.UUID))
            }

            return super.shouldOverrideUrlLoading(view, request)
        }
    }
}