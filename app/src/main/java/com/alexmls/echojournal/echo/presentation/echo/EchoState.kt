package com.alexmls.echojournal.echo.presentation.echo

import com.alexmls.echojournal.R
import com.alexmls.echojournal.core.presentation.designsystem.dropdowns.Selectable
import com.alexmls.echojournal.core.presentation.designsystem.dropdowns.Selectable.Companion.asUnselectedItems
import com.alexmls.echojournal.core.presentation.util.UiText
import com.alexmls.echojournal.echo.presentation.echo.models.AudioCaptureMethod
import com.alexmls.echojournal.echo.presentation.echo.models.EchoFilterChip
import com.alexmls.echojournal.echo.presentation.echo.models.MoodChipContent
import com.alexmls.echojournal.echo.presentation.models.EchoUi
import com.alexmls.echojournal.echo.presentation.models.MoodUi
import com.alexmls.echojournal.echo.presentation.echo.models.DaySection
import com.alexmls.echojournal.echo.presentation.echo.models.RecordingState
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Duration

data class EchoState(
    val echos: Map<UiText, List<EchoUi>> = emptyMap(),
    val currentCaptureMethod: AudioCaptureMethod? = null,
    val recordingElapsedDuration: Duration = Duration.ZERO,
    val hasEchosRecorded: Boolean = false,
    val hasActiveTopicFilters: Boolean = false,
    val hasActiveMoodFilters: Boolean = false,
    val isLoadingData: Boolean = false,
    val recordingState: RecordingState = RecordingState.NOT_RECORDING,
    val moods: List<Selectable<MoodUi>> = emptyList(),
    val topics: List<Selectable<String>> = listOf("Love", "Happy", "Work").asUnselectedItems(),
    val moodChipContent: MoodChipContent = MoodChipContent(),
    val selectedEchoFilterChip: EchoFilterChip? = null,
    val topicChipTitle: UiText = UiText.StringResource(R.string.all_topics)
){
    val echoDaySections = echos
        .toList()
        .map { (dateHeader, echos) ->
            DaySection(dateHeader, echos)
        }

    val formattedRecordDuration: String
        get() {
            val minutes = (recordingElapsedDuration.inWholeMinutes % 60).toInt()
            val seconds = (recordingElapsedDuration.inWholeSeconds % 60).toInt()
            val centiseconds = ((recordingElapsedDuration.inWholeMilliseconds % 1000) / 10.0).roundToInt()

            return String.format(
                locale = Locale.US,
                format = "%02d:%02d:%02d",
                minutes, seconds, centiseconds
            )
        }
}
