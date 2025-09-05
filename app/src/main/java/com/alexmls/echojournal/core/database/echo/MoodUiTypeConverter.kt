package com.alexmls.echojournal.core.database.echo

import androidx.room.TypeConverter
import com.alexmls.echojournal.echo.presentation.models.MoodUi

class MoodUiTypeConverter {

    @TypeConverter
    fun fromMood(moodUi: MoodUi): String {
        return moodUi.name
    }

    @TypeConverter
    fun toMood(moodName: String): MoodUi {
        return MoodUi.valueOf(moodName)
    }
}