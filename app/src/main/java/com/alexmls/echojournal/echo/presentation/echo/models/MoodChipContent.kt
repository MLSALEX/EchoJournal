package com.alexmls.echojournal.echo.presentation.echo.models

import com.alexmls.echojournal.R
import com.alexmls.echojournal.core.presentation.util.UiText

data class MoodChipContent(
    val iconsRes: List<Int> = emptyList(),
    val title: UiText = UiText.StringResource(R.string.all_moods)
)
