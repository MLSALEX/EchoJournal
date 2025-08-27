package com.alexmls.echojournal.echo.presentation.echo.models

import com.alexmls.echojournal.core.presentation.util.UiText
import com.alexmls.echojournal.echo.presentation.models.EchoUi

data class DaySection(
    val dateHeader: UiText,
    val echos: List<EchoUi>
)
