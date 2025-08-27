package com.alexmls.echojournal.echo.presentation.echo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.alexmls.echojournal.core.presentation.designsystem.chips.HashtagChip
import com.alexmls.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.alexmls.echojournal.core.presentation.util.defaultShadow
import com.alexmls.echojournal.echo.presentation.components.MoodPlayer
import com.alexmls.echojournal.echo.presentation.echo.models.TrackSizeInfo
import com.alexmls.echojournal.echo.presentation.models.EchoUi
import com.alexmls.echojournal.echo.presentation.models.MoodUi
import com.alexmls.echojournal.echo.presentation.preview.PreviewModels

@Composable
fun EchoCard(
    echoUi: EchoUi,
    onTrackSizeAvailable: (TrackSizeInfo) -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .defaultShadow(shape = RoundedCornerShape(8.dp))
    ){
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = echoUi.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = echoUi.formattedRecordedAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            MoodPlayer(
                moodUi = echoUi.mood,
                playbackState = echoUi.playbackState,
                playerProgress = { echoUi.playbackRatio },
                durationPlayed = echoUi.playbackCurrentDuration,
                totalPlaybackDuration = echoUi.playbackTotalDuration,
                powerRatios = echoUi.amplitudes,
                onPlayClick = onPlayClick,
                onPauseClick = onPauseClick,
                onTrackSizeAvailable = onTrackSizeAvailable
            )

            if(!echoUi.note.isNullOrBlank()){
                ExpandableText(echoUi.note)
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ){
                echoUi.topics.forEach { topic ->
                    HashtagChip(text = topic)
                }
            }
        }
    }
}

@Preview
@Composable
private fun EchoCardPr() {
    EchoJournalTheme {
        EchoCard(
            echoUi = PreviewModels.echoUi.copy(
                mood = MoodUi.EXCITED
            ),
            onTrackSizeAvailable = {},
            onPlayClick = {},
            onPauseClick = {}
        )
    }
}