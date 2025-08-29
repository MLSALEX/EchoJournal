package com.alexmls.echojournal.echo.presentation.echo

interface EchoEvent {
    data object RequestAudioPermission: EchoEvent
    data object RecordingTooShort: EchoEvent
    data object OnDoneRecording: EchoEvent
}