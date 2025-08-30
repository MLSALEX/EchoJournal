package com.alexmls.echojournal.echo.presentation.echo

import com.alexmls.echojournal.echo.domain.recording.RecordingDetails

interface EchoEvent {
    data object RequestAudioPermission: EchoEvent
    data object RecordingTooShort: EchoEvent
    data class OnDoneRecording(val details: RecordingDetails): EchoEvent
}