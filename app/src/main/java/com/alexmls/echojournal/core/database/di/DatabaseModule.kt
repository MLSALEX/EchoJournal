package com.alexmls.echojournal.core.database.di

import androidx.room.Room
import com.alexmls.echojournal.core.database.Database
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single<Database> {
        Room.databaseBuilder(
            androidApplication(),
            Database::class.java,
            "echos.db",
        ).build()
    }
    single {
        get<Database>().dao
    }
}