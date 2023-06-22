package com.alonalbert.pad.app.data.local.database

import androidx.room.withTransaction
import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.UserShow
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
    private val userShowDao = database.userShowDao()
    private val settingsDao = database.settingsDao()

    override fun getUsersFlow(): Flow<List<User>> = userDao.observeAll().map { it.toExternal() }

    override fun getUserFlow(id: Long): Flow<User> = userDao.observe(id).map { it.toExternal() }

    override suspend fun updateUser(user: User) {
        database.withTransaction {
            // todo: Check is cascade works
            userDao.update(user.toLocal())
            val userShows = user.shows.map { show -> LocalUserShow(user.id, show.id) }
            userShowDao.update(userShows)
        }
    }

    override suspend fun refreshUsers(users: List<User>) {
        database.withTransaction {
            userDao.deleteExcept(users.map { it.id })
            userDao.upsertAll(users.toLocal())
            val userShows = users.flatMap { user -> user.shows.map { show -> LocalUserShow(user.id, show.id) } }
            userShowDao.update(userShows)
        }
    }

    override suspend fun refreshShows(shows: List<Show>) = showDao.refreshAll(shows.toLocal())

    override fun getShowsFlow(): Flow<List<Show>> = showDao.observeAll().map { it.toExternal() }

    override suspend fun updateUserShows(userShows: List<UserShow>) = userShowDao.update(userShows.toLocal())

    override fun getSettingIntFlow(name: String): Flow<Int> = settingsDao.observeInt(name)

    override fun getSettingStringFlow(name: String): Flow<String> = settingsDao.observeString(name)
}