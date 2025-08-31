package com.alexmls.echojournal.echo.di

import com.alexmls.echojournal.echo.data.recording.VoiceRecorderImpl
import com.alexmls.echojournal.echo.domain.recording.VoiceRecorder
import org.koin.android.ext.koin.androidApplication
import com.alexmls.echojournal.echo.presentation.echo.EchoViewModel
import com.alexmls.echojournal.echo.presentation.create_echo.CreateEchoViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val echoModule = module {
    single {
       VoiceRecorderImpl(
            context = androidApplication(),
            applicationScope = get()
        )
    } bind VoiceRecorder::class

    viewModelOf(::EchoViewModel)
    viewModelOf(::CreateEchoViewModel)
}