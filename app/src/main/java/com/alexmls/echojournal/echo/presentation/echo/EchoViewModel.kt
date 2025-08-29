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
import com.alexmls.echojournal.echo.presentation.echo.models.RecordingState
import com.alexmls.echojournal.echo.presentation.models.MoodUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

class EchoViewModel(
    private val voiceRecorder: VoiceRecorder
) : ViewModel() {

    companion object {
        private val MIN_RECORD_DURATION = 1.5.seconds
    }

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
            EchoAction.OnRecordFabClick -> {
                requestAudioPermission()
                _state.update {
                    it.copy(
                        currentCaptureMethod = AudioCaptureMethod.STANDARD
                    )
                }
            }
            EchoAction.OnRequestPermissionQuickRecording -> {
                requestAudioPermission()
                _state.update {
                    it.copy(
                        currentCaptureMethod = AudioCaptureMethod.QUICK
                    )
                }
            }
            EchoAction.OnRecordButtonLongClick -> {
                startRecording(captureMethod = AudioCaptureMethod.QUICK)
            }
            is EchoAction.OnRemoveFilters -> {
                when(action.filterType){
                    EchoFilterChip.MOODS -> selectedMoodFilters.update { emptyList()}
                    EchoFilterChip.TOPICS -> selectedTopicFilters.update { emptyList() }
                }
            }
            EchoAction.OnTopicChipClick -> {
                _state.update {
                    it.copy(
                        selectedEchoFilterChip = EchoFilterChip.TOPICS
                    )
                }
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

            EchoAction.OnPauseAudioClick -> {}

            is EchoAction.OnPlayEchoClick -> {}
            is EchoAction.OnTrackSizeAvailable -> {}
            EchoAction.OnAudioPermissionGranted -> {
                Timber.d("Recording started...")
                startRecording(captureMethod = AudioCaptureMethod.STANDARD)
            }

            EchoAction.OnCancelRecording -> cancelRecording()
            EchoAction.OnCompleteRecording -> stopRecording()
            EchoAction.OnPauseRecordingClick -> pausedRecording()
            EchoAction.OnResumeRecordingClick -> resumeRecording()
        }
    }

    private fun pausedRecording() {
        voiceRecorder.pause()
        _state.update {
            it.copy(
                recordingState = RecordingState.PAUSED
            )
        }
    }

    private fun resumeRecording() {
        voiceRecorder.resume()
        _state.update {
            it.copy(
                recordingState = RecordingState.NORMAL_CAPTURE
            )
        }
    }

    private fun cancelRecording() {
        _state.update {
            it.copy(
                recordingState = RecordingState.NOT_RECORDING,
                currentCaptureMethod = null
            )
        }
        voiceRecorder.cancel()
    }

    private fun stopRecording() {
        voiceRecorder.stop()
        _state.update {
            it.copy(
                recordingState = RecordingState.NOT_RECORDING
            )
        }
        val recordingDetails = voiceRecorder.recordingDetails.value

        viewModelScope.launch{
            if(recordingDetails.duration < MIN_RECORD_DURATION) {
                eventChannel.send(EchoEvent.RecordingTooShort)
            } else {
                eventChannel.send(EchoEvent.OnDoneRecording)
            }
        }
    }

    private fun startRecording(captureMethod: AudioCaptureMethod) {
        _state.update {
            it.copy(
                recordingState =  when(captureMethod){
                    AudioCaptureMethod.STANDARD -> RecordingState.NORMAL_CAPTURE
                    AudioCaptureMethod.QUICK -> RecordingState.QUICK_CAPTURE
                }
            )
        }
        voiceRecorder.start()

        if (captureMethod == AudioCaptureMethod.STANDARD){
            voiceRecorder
                .recordingDetails
                .distinctUntilChangedBy { it.duration }
                .map { it.duration }
                .onEach { duration ->
                    _state.update {
                        it.copy(
                            recordingElapsedDuration = duration
                        )
                    }
                }
                .launchIn(viewModelScope)
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