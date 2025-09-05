package com.alexmls.echojournal.app

import android.app.Application
import com.alexmls.echojournal.BuildConfig
import com.alexmls.echojournal.app.di.appModule
import com.alexmls.echojournal.echo.di.echoModule
import com.alexmls.echojournal.core.database.di.databaseModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class EchoJournalApp: Application() {

    val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate(){
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@EchoJournalApp)
            modules(
                appModule,
                echoModule,
                databaseModule
            )
        }
    }
}