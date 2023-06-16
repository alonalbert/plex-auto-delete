package com.alonalbert.pad.app.data.local.database

import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.UserWithShows
import com.alonalbert.pad.app.data.toExternal
import com.alonalbert.pad.app.data.toLocal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class RoomLocalDataSource @Inject constructor(
    database: AppDatabase,
) : LocalDataSource {
    private val userDao = database.userDao()
    private val showDao = database.showDao()

    override fun getUsersFlow(): Flow<List<User>> = userDao.observeAll().map { it.toExternal() }

    override fun getUserFlow(id: Long): Flow<UserWithShows> = userDao.observe(id).map { it.toExternal() }

    override suspend fun updateUser(user: User) = userDao.update(user.toLocal())

    override suspend fun refreshUsers(users: List<User>) = userDao.refreshAll(users.toLocal())

    override suspend fun refreshShows(shows: List<Show>) = showDao.refreshAll(shows.toLocal())

    override fun getShowsFlow(): Flow<List<Show>> = showDao.observeAll().map { it.toExternal() }
}