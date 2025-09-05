package com.alexmls.echojournal.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alexmls.echojournal.core.database.echo.Dao
import com.alexmls.echojournal.core.database.echo.EchoEntity
import com.alexmls.echojournal.core.database.echo.FloatListTypeConverter
import com.alexmls.echojournal.core.database.echo.MoodUiTypeConverter
import com.alexmls.echojournal.core.database.topic.TopicEntity


@Database(
    entities = [EchoEntity::class, TopicEntity::class, EchoTopicCrossRef::class],
    version = 1,
)
@TypeConverters(
    MoodUiTypeConverter::class,
    FloatListTypeConverter::class
)
abstract class Database: RoomDatabase() {
    abstract val dao: Dao
}