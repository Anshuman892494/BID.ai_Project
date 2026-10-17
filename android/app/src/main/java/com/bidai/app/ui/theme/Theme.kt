package com.bidai.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BidAiBlue,
    secondary = EcoGreen,
    tertiary = AmberWarning,
    background = PureWhite,
    surface = PureWhite,
    surfaceVariant = OffWhite,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onBackground = TextBlack,
    onSurface = TextBlack,
    outline = BorderColor,
    error = ErrorRed
)

@Composable
fun BidAITheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
