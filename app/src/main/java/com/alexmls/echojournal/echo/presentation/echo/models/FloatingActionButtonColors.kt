package com.alexmls.echojournal.echo.presentation.echo.models

    import androidx.compose.material3.MaterialTheme
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.remember
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.SolidColor
    import com.alexmls.echojournal.core.presentation.designsystem.theme.buttonGradient
    import com.alexmls.echojournal.core.presentation.designsystem.theme.buttonGradientPressed
    import com.alexmls.echojournal.core.presentation.designsystem.theme.primary90
    import com.alexmls.echojournal.core.presentation.designsystem.theme.primary95


data class FloatingActionButtonColors(
    val primary: Brush,
    val primaryPressed: Brush,
    val outerCircle: Brush,
    val innerCircle: Brush
)

@Composable
fun rememberFloatingActionButtonColors(
    primary: Brush = MaterialTheme.colorScheme.buttonGradient,
    primaryPressed: Brush = MaterialTheme.colorScheme.buttonGradientPressed,
    outerCircle: Brush = SolidColor(MaterialTheme.colorScheme.primary95),
    innerCircle: Brush = SolidColor(MaterialTheme.colorScheme.primary90),
): FloatingActionButtonColors {
    return remember(primary, primaryPressed, outerCircle, innerCircle) {
        FloatingActionButtonColors(
            primary = primary,
            primaryPressed = primaryPressed,
            outerCircle = outerCircle,
            innerCircle = innerCircle
        )
    }
}
