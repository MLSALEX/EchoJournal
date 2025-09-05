package com.alexmls.echojournal.echo.data.echo

import com.alexmls.echojournal.core.database.echo.Dao
import com.alexmls.echojournal.echo.domain.echo.Echo
import com.alexmls.echojournal.echo.domain.echo.EchoDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomEchoDataSource(
    private val dao: Dao
): EchoDataSource {

    override fun observeEchos(): Flow<List<Echo>> {
        return dao
            .observeEchos()
            .map { echoWithTopics ->
                echoWithTopics.map { echoWithTopic ->
                    echoWithTopic.toEcho()
                }
            }
    }

    override fun observeTopics(): Flow<List<String>> {
        return dao
            .observeTopics()
            .map { topicEntities ->
                topicEntities.map { it.topic }
            }
    }

    override fun searchTopics(query: String): Flow<List<String>> {
        return dao
            .searchTopics(query)
            .map { topicEntities ->
                topicEntities.map { it.topic }
            }
    }

    override suspend fun insertEcho(echo: Echo) {
        dao.insertEchoWithTopics(echo.toEchoWithTopics())
    }
}