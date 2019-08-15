package com.dicas.auditorias

import android.app.Application
import android.content.Context

class App : Application() {
    companion object {
        lateinit var sApplication: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        sApplication = this
    }
}