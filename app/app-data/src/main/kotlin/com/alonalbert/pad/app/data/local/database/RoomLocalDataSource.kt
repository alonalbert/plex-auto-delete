package com.alonalbert.pad.app.data.local.database

import androidx.room.withTransaction
import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.UserShow
import com.alonalbert.pad.app.data.UserWithShows
import com.alonalbert.pad.app.data.mapping.ExternalToLocal.toLocal
import com.alonalbert.pad.app.data.mapping.LocalToExternal.toExternal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class RoomLocalDataSource @Inject constructor(
    private val database: AppDatabase,
) : LocalDataSource {
    private val userDao = database.userDao()
    private val showDao = database.showDao()
    private val useShowDao = database.userShowDao()

    override fun getUsersFlow(): Flow<List<User>> = userDao.observeAll().map { it.toExternal() }

    override fun getUserFlow(id: Long): Flow<UserWithShows> = userDao.observe(id).map { it.toExternal() }

    override suspend fun updateUser(user: User) = userDao.update(user.toLocal())

    override suspend fun refreshUsers(users: List<User>) {
        database.withTransaction {
            userDao.upsertAll(users.toLocal())
            val userShows = users.flatMap { user -> user.shows.map { show -> LocalUserShow(user.id, show.id) } }
            useShowDao.update(userShows)
        }
    }

    override suspend fun refreshShows(shows: List<Show>) = showDao.refreshAll(shows.toLocal())

    override fun getShowsFlow(): Flow<List<Show>> = showDao.observeAll().map { it.toExternal() }

    override suspend fun updateUserShows(userShows: List<UserShow>) = useShowDao.update(userShows.toLocal())
}