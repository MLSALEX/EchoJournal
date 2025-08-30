package com.alexmls.echojournal.echo.presentation.create_echo

import com.alexmls.echojournal.echo.presentation.echo.models.PlaybackState
import com.alexmls.echojournal.echo.presentation.models.MoodUi
import kotlin.time.Duration

data class CreateEchoState(
    val titleText: String = "",
    val addTopicText: String = "",
    val noteText: String = "",
    val showMoodSelector: Boolean = true,
    val selectedMood: MoodUi = MoodUi.NEUTRAL,
    val showTopicSuggestions: Boolean = false,
    val mood: MoodUi? = null,
    val searchResult: List<String> = emptyList(),
    val showCreateTopicOption: Boolean = false,
    val canSaveEcho: Boolean = false,
    val playbackAmplitudes: List<Float> = List(32) {0.3f},
    val playbackTotalDuration: Duration = Duration.ZERO,
    val playbackState: PlaybackState = PlaybackState.STOPPED,
    val durationPlayed: Duration = Duration.ZERO,
 ){
    val durationPlayedRatio = (durationPlayed / playbackTotalDuration).toFloat()
}