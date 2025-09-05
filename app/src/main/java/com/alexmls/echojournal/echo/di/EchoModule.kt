package com.alexmls.echojournal.echo.di

import com.alexmls.echojournal.echo.data.audio.AudioPlayerImpl
import com.alexmls.echojournal.echo.data.echo.RoomEchoDataSource
import com.alexmls.echojournal.echo.data.recording.InternalRecordingStorage
import com.alexmls.echojournal.echo.data.recording.RecordingStorage
import com.alexmls.echojournal.echo.data.recording.VoiceRecorderImpl
import com.alexmls.echojournal.echo.domain.audio.AudioPlayer
import com.alexmls.echojournal.echo.domain.recording.VoiceRecorder
import com.alexmls.echojournal.echo.presentation.create_echo.CreateEchoViewModel
import com.alexmls.echojournal.echo.presentation.echo.EchoViewModel
import com.alexmls.echojournal.echo.domain.echo.EchoDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val echoModule = module {
    singleOf(::VoiceRecorderImpl) bind VoiceRecorder::class
    singleOf(::InternalRecordingStorage) bind RecordingStorage::class
    singleOf(::AudioPlayerImpl) bind AudioPlayer::class
    singleOf(::RoomEchoDataSource) bind EchoDataSource::class

    viewModelOf(::EchoViewModel)
    viewModelOf(::CreateEchoViewModel)
}