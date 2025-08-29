package com.alexmls.echojournal.echo.presentation.echo

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexmls.echojournal.R
import com.alexmls.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.alexmls.echojournal.core.presentation.designsystem.theme.bgGradient
import com.alexmls.echojournal.core.presentation.util.ObserveAsEvents
import com.alexmls.echojournal.core.presentation.util.isAppInForeground
import com.alexmls.echojournal.echo.presentation.echo.components.EchoEmptyBackground
import com.alexmls.echojournal.echo.presentation.echo.components.EchoFilterRow
import com.alexmls.echojournal.echo.presentation.echo.components.EchoList
import com.alexmls.echojournal.echo.presentation.echo.components.EchoRecordFloatingActionButton
import com.alexmls.echojournal.echo.presentation.echo.components.EchoTopBar
import com.alexmls.echojournal.echo.presentation.echo.components.RecordingSheet
import com.alexmls.echojournal.echo.presentation.echo.models.AudioCaptureMethod
import com.alexmls.echojournal.echo.presentation.echo.models.RecordingState
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun EchoRoot(
    viewModel: EchoViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if(isGranted && state.currentCaptureMethod == AudioCaptureMethod.STANDARD) {
            viewModel.onAction(EchoAction.OnAudioPermissionGranted)
        }
    }
    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is EchoEvent.RequestAudioPermission -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            is EchoEvent.RecordingTooShort -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.audio_recording_was_too_short),
                    Toast.LENGTH_LONG
                ).show()
            }
            is EchoEvent.OnDoneRecording -> {
                Timber.d("Recording successful!")
            }
        }
    }

    val isAppInForeground by isAppInForeground()
    LaunchedEffect(isAppInForeground, state.recordingState) {
        if(state.recordingState == RecordingState.NORMAL_CAPTURE && !isAppInForeground) {
            viewModel.onAction(EchoAction.OnPauseRecordingClick)
        }
    }
    EchoScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun EchoScreen(
    state: EchoState,
    onAction: (EchoAction) -> Unit,
) {
    Scaffold (
        floatingActionButton = {
            EchoRecordFloatingActionButton(
                onClick = {
                    onAction(EchoAction.OnFabClick)
                }
            )
        },
        topBar = {
            EchoTopBar(
                onSettingsClick = {
                    onAction(EchoAction.OnSettingsClick)
                }
            )
        }
    ){ innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = MaterialTheme.colorScheme.bgGradient
                )
                .padding(innerPadding)
        ){
            EchoFilterRow(
                moodChipContent = state.moodChipContent,
                hasActiveMoodFilters = state.hasActiveMoodFilters,
                selectedEchoFilterChip = state.selectedEchoFilterChip,
                moods = state.moods,
                topicChipTitle = state.topicChipTitle,
                hasActiveTopicFilters = state.hasActiveTopicFilters,
                topics = state.topics,
                onAction = onAction,
                modifier = Modifier
                    .fillMaxWidth()
            )

            when{
                state.isLoadingData -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .wrapContentSize(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                !state.hasEchosRecorded -> {
                    EchoEmptyBackground(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
                else -> {
                    EchoList(
                        sections = state.echoDaySections,
                        onPlayClick = {
                            onAction(EchoAction.OnPlayEchoClick(it))
                        },
                        onPauseClick = {
                            onAction(EchoAction.OnPauseRecordingClick)
                        },
                        onTrackSizeAvailable = { trackSize ->
                            onAction(EchoAction.OnTrackSizeAvailable(trackSize))
                        }
                    )
                }
            }
        }
        if(state.recordingState in listOf(RecordingState.NORMAL_CAPTURE, RecordingState.PAUSED)) {
            RecordingSheet(
                formattedRecordDuration = state.formattedRecordDuration,
                isRecording = state.recordingState == RecordingState.NORMAL_CAPTURE,
                onDismiss = { onAction(EchoAction.OnCancelRecording) },
                onPauseClick = { onAction(EchoAction.OnPauseRecordingClick) },
                onResumeClick = { onAction(EchoAction.OnResumeRecordingClick) },
                onCompleteRecording = { onAction(EchoAction.OnCompleteRecording) },
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    EchoJournalTheme {
        EchoScreen(
            state = EchoState(
                isLoadingData = false,
                hasEchosRecorded = false
            ),
            onAction = {}
        )
    }
}