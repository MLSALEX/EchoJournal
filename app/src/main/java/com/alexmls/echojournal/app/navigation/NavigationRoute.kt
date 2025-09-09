package com.alexmls.echojournal.app.navigation

import kotlinx.serialization.Serializable

sealed interface NavigationRoute {
    @Serializable
    data class Home(
        val startRecording: Boolean
    ): NavigationRoute

    @Serializable
    data class CreateEcho(
        val recordingPath: String,
        val duration: Long,
        val amplitudes: String
    ): NavigationRoute

    @Serializable
    data object Settings
}