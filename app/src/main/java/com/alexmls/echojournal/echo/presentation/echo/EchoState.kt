package com.alexmls.echojournal.echo.presentation.echo

import com.alexmls.echojournal.R
import com.alexmls.echojournal.core.presentation.designsystem.dropdowns.Selectable
import com.alexmls.echojournal.core.presentation.designsystem.dropdowns.Selectable.Companion.asUnselectedItems
import com.alexmls.echojournal.core.presentation.util.UiText
import com.alexmls.echojournal.echo.presentation.echo.models.EchoFilterChip
import com.alexmls.echojournal.echo.presentation.echo.models.MoodChipContent
import com.alexmls.echojournal.echo.presentation.models.MoodUi

data class EchoState(
    val hasEchosRecorded: Boolean = false,
    val hasActiveTopicFilters: Boolean = false,
    val hasActiveMoodFilters: Boolean = false,
    val isLoadingData: Boolean = false,
    val moods: List<Selectable<MoodUi>> = emptyList(),
    val topics: List<Selectable<String>> = listOf("Love", "Happy", "Work").asUnselectedItems(),
    val moodChipContent: MoodChipContent = MoodChipContent(),
    val selectedEchoFilterChip: EchoFilterChip? = null,
    val topicChipTitle: UiText = UiText.StringResource(R.string.all_topics)
)
