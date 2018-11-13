package com.jesta.application

import android.app.Application
import android.content.Context
import android.support.v7.app.AppCompatDelegate

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