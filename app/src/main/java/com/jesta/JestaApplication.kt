package com.jesta

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class JestaApplication : Application() {
    companion object {
        lateinit var instance: JestaApplication
            private set
        lateinit var defaultUEH: Thread.UncaughtExceptionHandler
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // Setup handler for uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler { thread, e ->
            val _errorsLogDB = FirebaseDatabase.getInstance().getReference("errors")
            val errorId = UUID.randomUUID().toString()

            _errorsLogDB.child(errorId).child("thread").setValue(thread.name)
            _errorsLogDB.child(errorId).child("date").setValue(Date().toString())
            if (e.cause !== null) {
                _errorsLogDB.child(errorId).child("errorMessage").setValue(e.cause.toString())
                _errorsLogDB.child(errorId).child("stackTrace").setValue(e.cause!!.stackTrace.toList())
            }
            else {
                _errorsLogDB.child(errorId).child("errorMessage").setValue(e.message)
                _errorsLogDB.child(errorId).child("stackTrace").setValue(e.stackTrace.toList())
            }

            defaultUEH.uncaughtException(thread, e)
        }
    }


}