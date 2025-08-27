package com.alexmls.echojournal.echo.presentation.echo.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.alexmls.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.alexmls.echojournal.echo.presentation.echo.models.DaySection
import com.alexmls.echojournal.echo.presentation.echo.models.RelativePosition
import com.alexmls.echojournal.echo.presentation.echo.models.TrackSizeInfo
import com.alexmls.echojournal.echo.presentation.preview.PreviewModels
import java.time.Instant
import java.time.ZonedDateTime
import com.alexmls.echojournal.core.presentation.util.UiText

@Composable
fun EchoList(
    sections: List<DaySection>,
    onPlayClick: (echoId: Int) -> Unit,
    onPauseClick: () -> Unit,
    onTrackSizeAvailable: (TrackSizeInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ){
        sections.forEachIndexed { sectionIndex, (dateHeader, echos) ->
            stickyHeader {
                if(sectionIndex > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text(
                    text = dateHeader.asString().uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            itemsIndexed(
                items = echos,
                key = { _, echo -> echo.id}
            ){ index, echo ->
                TimelineItem(
                    echoUi = echo,
                    relativePosition = when {
                        index == 0 && echos.size == 1 -> RelativePosition.SINGLE_ENTRY
                        index == 0 -> RelativePosition.FIRST
                        echos.lastIndex == index -> RelativePosition.LAST
                        else -> RelativePosition.IN_BETWEEN
                    },
                    onPlayClick = { onPlayClick(echo.id)},
                    onPauseClick = onPauseClick,
                    onTrackSizeAvailable = onTrackSizeAvailable
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun EchoListPreview() {
    EchoJournalTheme {
        val todaysEchos = remember {
            (1..3).map {
                PreviewModels.echoUi.copy(
                    id = it,
                    recordedAt = Instant.now()
                )
            }
        }
        val yesterdaysEchos = remember {
            (4..6).map {
                PreviewModels.echoUi.copy(
                    id = it,
                    recordedAt = ZonedDateTime.now().minusDays(1).toInstant()
                )
            }
        }
        val echosFrom2DaysAgo = remember {
            (7..9).map {
                PreviewModels.echoUi.copy(
                    id = it,
                    recordedAt = ZonedDateTime.now().minusDays(2).toInstant()
                )
            }
        }
        val sections = remember {
            listOf(
                DaySection(
                    dateHeader = UiText.Dynamic("Today"),
                    echos = todaysEchos
                ),
                DaySection(
                    dateHeader = UiText.Dynamic("Yesterday"),
                    echos = yesterdaysEchos
                ),
                DaySection(
                    dateHeader = UiText.Dynamic("2025/04/25"),
                    echos = echosFrom2DaysAgo
                ),
            )
        }

        EchoList(
            sections = sections,
            onPauseClick = {},
            onPlayClick = {},
            onTrackSizeAvailable = {}
        )
    }
}
