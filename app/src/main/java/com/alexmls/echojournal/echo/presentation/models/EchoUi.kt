package com.alexmls.echojournal.echo.presentation.models
import com.alexmls.echojournal.echo.presentation.echo.models.PlaybackState
import com.alexmls.echojournal.echo.presentation.util.toReadableTime
import kotlin.time.Duration
import java.time.Instant as JavaInstant

data class EchoUi(
    val id: Int,
    val title: String,
    val mood: MoodUi,
    val recordedAt: JavaInstant,
    val note: String?,
    val topics: List<String>,
    val amplitudes: List<Float>,
    val playbackTotalDuration: Duration,
    val audioFilePath: String,
    val playbackCurrentDuration: Duration = Duration.ZERO,
    val playbackState: PlaybackState = PlaybackState.STOPPED
) {
    val formattedRecordedAt = recordedAt.toReadableTime()
    val playbackRatio = (playbackCurrentDuration / playbackTotalDuration).toFloat()
}
