package com.example.wordle

import android.app.Application
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }

    companion object {
        const val uiOptions =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

}