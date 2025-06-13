package com.vihaanshika.mykidsvan.android

import android.app.Application
import com.vihaanshika.mykidsvan.android.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            modules(appModule)
        }
    }
}
