package com.alonalbert.pad.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree


@HiltAndroidApp
class PadApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(object : DebugTree() {

            override fun createStackElementTag(element: StackTraceElement) = "PAD (${element.fileName}:${element.lineNumber})"
        })
    }
}