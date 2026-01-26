package com.verifythecomboboxproject

import android.app.Application
import androidx.multidex.MultiDex


class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this);
    }

}