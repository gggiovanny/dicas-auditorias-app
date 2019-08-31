package com.dicas.auditorias

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    companion object {
        lateinit var sApplication: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        sApplication = this
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_YES
        )
    }
}