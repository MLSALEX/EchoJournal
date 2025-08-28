package com.alexmls.echojournal.echo.presentation.echo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexmls.echojournal.R
import com.alexmls.echojournal.core.presentation.designsystem.dropdowns.Selectable
import com.alexmls.echojournal.core.presentation.util.UiText
import com.alexmls.echojournal.echo.domain.recording.VoiceRecorder
import com.alexmls.echojournal.echo.presentation.echo.models.AudioCaptureMethod
import com.alexmls.echojournal.echo.presentation.echo.models.EchoFilterChip
import com.alexmls.echojournal.echo.presentation.echo.models.MoodChipContent
import com.alexmls.echojournal.echo.presentation.models.MoodUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class EchoViewModel(
    private val voiceRecorder: VoiceRecorder
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())
    private val selectedTopicFilters = MutableStateFlow<List<String>>(emptyList())

    private val eventChannel = Channel<EchoEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(EchoState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeFilters()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EchoState()
        )

    fun onAction(action: EchoAction) {
        when (action) {
            EchoAction.OnFabClick -> {
                requestAudioPermission()
                _state.update { it.copy(
                    currentCaptureMethod = AudioCaptureMethod.STANDARD
                ) }
            }
            EchoAction.OnFabLongClick -> {
                requestAudioPermission()
                _state.update { it.copy(
                    currentCaptureMethod = AudioCaptureMethod.QUICK
                ) }
            }

            is EchoAction.OnRemoveFilters -> {
                when(action.filterType){
                    EchoFilterChip.MOODS -> selectedMoodFilters.update { emptyList()}
                    EchoFilterChip.TOPICS -> selectedTopicFilters.update { emptyList() }
                }
            }
            EchoAction.OnTopicChipClick -> {
                _state.update { it.copy(
                    selectedEchoFilterChip = EchoFilterChip.TOPICS
                ) }
            }
            EchoAction.OnMoodChipClick -> {
                _state.update { it.copy(
                    selectedEchoFilterChip = EchoFilterChip.MOODS
                ) }
            }
            EchoAction.OnSettingsClick -> {

            }

            EchoAction.OnDismissMoodDropDown -> {
                _state.update {
                    it.copy(
                        selectedEchoFilterChip = null
                    )
                }
            }
            EchoAction.OnDismissTopicDropDown -> {}
            is EchoAction.OnFilterByMoodClick -> {
                toggleIn(selectedMoodFilters, action.moodUi)
            }
            is EchoAction.OnFilterByTopicClick -> {
                toggleIn(selectedTopicFilters, action.topic)
            }

            EchoAction.OnPauseClick -> {}
            is EchoAction.OnPlayEchoClick -> {}
            is EchoAction.OnTrackSizeAvailable -> {}
            EchoAction.OnAudioPermissionGranted -> {
                Timber.d("Recording started...")
            }
        }
    }

    private fun requestAudioPermission() = viewModelScope.launch {
        eventChannel.send(EchoEvent.RequestAudioPermission)
    }

    private fun <T> toggleIn(flow: MutableStateFlow<List<T>>, item: T) {
        flow.update { current ->
            if (item in current) current - item else current + item
        }
    }

    private fun observeFilters() {
        combine(
            selectedTopicFilters,
            selectedMoodFilters
        ) { selectedTopics, selectedMoods ->
            _state.update {
                it.copy(
                    topics = it.topics.map { selectableTopic ->
                        Selectable(
                            item = selectableTopic.item,
                            selected = selectedTopics.contains(selectableTopic.item)
                        )
                    },
                    moods = MoodUi.entries.map {
                        Selectable(
                            item = it,
                            selected = selectedMoods.contains(it)
                        )
                    },
                    hasActiveMoodFilters = selectedMoods.isNotEmpty(),
                    hasActiveTopicFilters = selectedTopics.isNotEmpty(),
                    topicChipTitle = selectedTopics.deriveTopicsToText(),
                    moodChipContent = selectedMoods.asMoodChipContent()
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun List<String>.deriveTopicsToText(): UiText {
        return when (size) {
            0 -> UiText.StringResource(R.string.all_topics)
            1 -> UiText.Dynamic(this.first())
            2 -> UiText.Dynamic("${this.first()}, ${this.last()}")
            else -> {
                val extraElementCount = size - 2
                UiText.Dynamic("${this.first()}, ${this[1]} +$extraElementCount")
            }
        }
    }

    private fun List<MoodUi>.asMoodChipContent(): MoodChipContent {
        if (this.isEmpty()) {
            return MoodChipContent()
        }

        val icons = this.map { it.iconSet.fill }
        val moodNames = this.map { it.title }

        return when (size) {
            1 -> MoodChipContent(
                iconsRes = icons,
                title = moodNames.first()
            )

            2 -> MoodChipContent(
                iconsRes = icons,
                title = UiText.Combined(
                    format = "%s, %s",
                    uiTexts = moodNames.toTypedArray()
                )
            )

            else -> {
                val extraElementCount = size - 2
                MoodChipContent(
                    iconsRes = icons,
                    title = UiText.Combined(
                        format = "%s, %s +$extraElementCount",
                        uiTexts = moodNames.take(2).toTypedArray()
                    )
                )
            }
        }
    }
}