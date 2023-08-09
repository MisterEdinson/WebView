package com.example.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import com.example.webview.databinding.ActivityMainBinding
import com.example.webview.ui.home.SavedIds
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

        keyId = pref.read("id")
        keyUuid = pref.read("uuid")

        if(keyId != null){
            binding.tvID.visibility = View.VISIBLE
            binding.tvID.text = "id = $keyId"
            binding.tvUuid.visibility = View.VISIBLE
            binding.tvUuid.text = "uuid = $keyUuid"
        }else{
            binding.tvID.visibility = View.GONE
            binding.tvUuid.visibility = View.GONE
        }

        binding.btnStart.setOnClickListener {
            binding.btnStart.visibility = View.INVISIBLE
            binding.wvMain.loadUrl(Constants.URL)
        }
    }

    private val client = object : WebViewClient(){
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.progressLoad.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.progressLoad.visibility = View.INVISIBLE
            binding.btnStart.visibility = View.GONE
            binding.wvMain.visibility = View.VISIBLE

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

            keyId = uri.getQueryParameter("id")
            keyUuid = uri.getQueryParameter("uuid")
            pref.saved("id",keyId)
            pref.saved("uuid",keyUuid)

            if(keyId != null || keyUuid != null){
                binding.tvID.text = "id = $keyId"
                binding.tvUuid.text = "uuid = $keyUuid"
                binding.tvID.visibility = View.VISIBLE
                binding.tvUuid.visibility = View.VISIBLE
            }else{
                binding.tvID.visibility = View.GONE
                binding.tvUuid.visibility = View.GONE
            }

            return super.shouldOverrideUrlLoading(view, request)
        }
    }
}