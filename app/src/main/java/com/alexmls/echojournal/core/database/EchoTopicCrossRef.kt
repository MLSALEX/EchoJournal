package com.alexmls.echojournal.core.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import com.alexmls.echojournal.core.database.echo.EchoEntity
import com.alexmls.echojournal.core.database.topic.TopicEntity

@Entity(
    primaryKeys = ["echoId", "topic"],
)
data class EchoTopicCrossRef(
    val echoId: Int,
    val topic: String
)

data class EchoWithTopics(
    @Embedded val echo: EchoEntity,
    @Relation(
        parentColumn = "echoId",
        entityColumn = "topic",
        associateBy = Junction(EchoTopicCrossRef::class)
    )
    val topics: List<TopicEntity>
)