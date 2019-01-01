package com.jesta

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class JestaApplication : Application() {
    companion object {
        lateinit var instance: JestaApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}