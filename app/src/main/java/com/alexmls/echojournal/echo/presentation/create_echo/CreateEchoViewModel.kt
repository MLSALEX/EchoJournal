package com.alexmls.echojournal.echo.presentation.create_echo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alexmls.echojournal.app.navigation.NavigationRoute
import com.alexmls.echojournal.core.presentation.designsystem.dropdowns.Selectable.Companion.asUnselectedItems
import com.alexmls.echojournal.echo.data.recording.RecordingStorage
import com.alexmls.echojournal.echo.presentation.models.MoodUi
import com.alexmls.echojournal.echo.presentation.util.toRecordingDetails
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateEchoViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val recordingStorage: RecordingStorage
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val route = savedStateHandle.toRoute<NavigationRoute.CreateEcho>()
    private val recordingDetails = route.toRecordingDetails()

    private val eventChannel = Channel<CreateEchoEvent>()
    val events = eventChannel.receiveAsFlow()


    private val _state = MutableStateFlow(CreateEchoState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeAddTopicText()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CreateEchoState()
        )

    fun onAction(action: CreateEchoAction) {
        when (action) {
            is CreateEchoAction.OnAddTopicTextChange -> onAddTopicTextChange(action.text)
            CreateEchoAction.OnConfirmMood -> onConfirmMood()
            CreateEchoAction.OnDismissMoodSelector -> onDismissMoodSelector()
            CreateEchoAction.OnDismissTopicSuggestions ->  onDismissTopicSuggestions()
            is CreateEchoAction.OnMoodClick -> onMoodClick(action.moodUi)
            is CreateEchoAction.OnNoteTextChange -> {}
            CreateEchoAction.OnPauseAudioClick -> {}
            CreateEchoAction.OnPlayAudioClick -> {}
            is CreateEchoAction.OnRemoveTopicClick -> onRemoveTopicClick(action.topic)
            CreateEchoAction.OnSaveClick -> onSaveClick()
            CreateEchoAction.OnSelectMoodClick -> onSelectMoodClick()
            is CreateEchoAction.OnTitleTextChange -> onTitleTextChange(action.text)
            is CreateEchoAction.OnTopicClick -> onTopicClick(action.topic)
            is CreateEchoAction.OnTrackSizeAvailable -> {}
            CreateEchoAction.OnDismissConfirmLeaveDialog -> onDismissConfirmLeaveDialog()
            CreateEchoAction.OnCancelClick,
            CreateEchoAction.OnNavigateBackClick,
            CreateEchoAction.OnGoBack -> onShowConfirmLeaveDialog()
        }
    }

    private fun onTitleTextChange(text: String) {
        _state.update { it.copy(
            titleText = text
        ) }
    }

    private fun onSaveClick() {
        if(recordingDetails.filePath == null) {
            return
        }

        viewModelScope.launch {
            val savedFilePath = recordingStorage.savePersistently(
                tempFilePath = recordingDetails.filePath
            )
            if(savedFilePath == null) {
                eventChannel.send(CreateEchoEvent.FailedToSaveFile)
                return@launch
            }

            // TODO: Echo
        }
    }
    private fun onDismissConfirmLeaveDialog() {
        _state.update { it.copy(
            showConfirmLeaveDialog = false
        ) }
    }

    private fun onShowConfirmLeaveDialog() {
        _state.update { it.copy(
            showConfirmLeaveDialog = true
        ) }
    }

    private fun onConfirmMood(){
        _state.update { it.copy(
            mood = it.selectedMood,
            canSaveEcho = it.titleText.isNotBlank(),
            showMoodSelector = false
        ) }
    }

    private fun onDismissMoodSelector(){
        _state.update{ it.copy(
            showMoodSelector = false
        )}
    }

    private fun onSelectMoodClick(){
        _state.update { it.copy(
            showMoodSelector = true
        )}
    }

    private fun onMoodClick(mood: MoodUi){
        _state.update { it.copy(
            selectedMood = mood
        )}
    }
    @OptIn(FlowPreview::class)
    private fun observeAddTopicText() {
        state
            .map { it.addTopicText }
            .distinctUntilChanged()
            .debounce(300)
            .onEach { query ->
                _state.update { it.copy(
                    showTopicSuggestions = query.isNotBlank() && query.trim() !in it.topics,
                    searchResults = listOf(
                        "work",
                        "love",
                    ).asUnselectedItems()
                ) }
            }
            .launchIn(viewModelScope)
    }

    private fun onDismissTopicSuggestions() {
        _state.update { it.copy(
            showTopicSuggestions = false
        ) }
    }

    private fun onRemoveTopicClick(topic: String) {
        _state.update { it.copy(
            topics = it.topics - topic
        ) }
    }

    private fun onTopicClick(topic: String) {
        _state.update { it.copy(
            addTopicText = "",
            topics = (it.topics + topic).distinct()
        ) }
    }

    private fun onAddTopicTextChange(text: String) {
        _state.update { it.copy(
            addTopicText = text.filter {
                it.isLetterOrDigit()
            }
        ) }
    }

}