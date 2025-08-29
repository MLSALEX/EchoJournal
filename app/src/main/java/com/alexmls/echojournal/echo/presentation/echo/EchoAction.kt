package com.alexmls.echojournal.echo.presentation.echo

import com.alexmls.echojournal.echo.presentation.echo.models.EchoFilterChip
import com.alexmls.echojournal.echo.presentation.echo.models.TrackSizeInfo
import com.alexmls.echojournal.echo.presentation.models.MoodUi

sealed interface EchoAction {
    data object OnMoodChipClick: EchoAction
    data object OnDismissMoodDropDown: EchoAction
    data object OnDismissTopicDropDown: EchoAction
    data class OnFilterByMoodClick(val moodUi: MoodUi): EchoAction
    data object OnTopicChipClick: EchoAction
    data class OnFilterByTopicClick(val topic: String): EchoAction
    data object OnSettingsClick: EchoAction
    data object OnFabClick: EchoAction
    data object OnFabLongClick: EchoAction
    data class OnRemoveFilters(val filterType: EchoFilterChip): EchoAction
    data class OnTrackSizeAvailable(val trackSizeInfo: TrackSizeInfo): EchoAction
    data class OnPlayEchoClick(val echoId: Int): EchoAction
    data object OnPauseRecordingClick : EchoAction
    data object OnResumeRecordingClick : EchoAction
    data object OnCompleteRecording : EchoAction
    data object OnPauseAudioClick : EchoAction
    data object OnAudioPermissionGranted: EchoAction
    data object OnCancelRecording: EchoAction
}