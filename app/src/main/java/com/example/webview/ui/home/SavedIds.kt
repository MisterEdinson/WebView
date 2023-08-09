package com.example.webview.ui.home

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SavedIds @Inject constructor(private val context: Context){

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("SavedUrl", Context.MODE_PRIVATE)
    }
    fun saved(key: String?, value: String?){
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun read(key: String? = null, desc: String? = null): String?{
        return sharedPreferences.getString(key, desc)
    }
}