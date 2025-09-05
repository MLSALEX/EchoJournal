package com.alexmls.echojournal.core.presentation.util

import com.alexmls.echojournal.echo.domain.echo.Echo
import com.alexmls.echojournal.echo.presentation.echo.models.PlaybackState
import com.alexmls.echojournal.echo.presentation.models.EchoUi
import com.alexmls.echojournal.echo.presentation.models.MoodUi
import kotlin.time.Duration

fun Echo.toEchoUi(
    currentPlaybackDuration: Duration = Duration.ZERO,
    playbackState: PlaybackState = PlaybackState.STOPPED
): EchoUi {
    return EchoUi(
        id = id!!,
        title = title,
        mood = MoodUi.valueOf(mood.name),
        recordedAt = recordedAt,
        note = note,
        topics = topics,
        amplitudes = audioAmplitudes,
        playbackTotalDuration = audioPlaybackLength,
        audioFilePath = audioFilePath,
        playbackCurrentDuration = currentPlaybackDuration,
        playbackState = playbackState
    )
}