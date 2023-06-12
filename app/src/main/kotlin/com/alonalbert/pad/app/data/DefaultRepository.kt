/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alonalbert.pad.app.data

import com.alonalbert.pad.app.data.source.local.database.AppDatabase
import com.alonalbert.pad.app.data.source.network.NetworkDataSource
import com.alonalbert.pad.app.di.ApplicationScope
import com.alonalbert.pad.app.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of [Repository]. Single entry point for managing users' data.
 *
 * @param networkDataSource - The network data source
 * @param localDataSource - The local data source
 * @param dispatcher - The dispatcher to be used for long running or complex operations, such as ID
 * generation or mapping many models.
 * @param scope - The coroutine scope used for deferred jobs where the result isn't important, such
 * as sending data to the network.
 */
@Singleton
class DefaultRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: AppDatabase,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : Repository {

    private val userDao = localDataSource.userDao()
    private val showDao = localDataSource.showDao()

    override fun getUsersFlow(): Flow<List<User>> {
        return userDao.observeAll().map { users ->
            users.toExternal()
        }
    }

    override fun getUserFlow(id: Long): Flow<User> {
        return userDao.observe(id).map {
            it.toExternal()
        }
    }

    override suspend fun updateUser(user: User) {
        withContext(dispatcher) {
            val updated = networkDataSource.updateUser(user.toNetwork())
            userDao.update(updated.toLocal())
        }
    }

    override suspend fun refreshUsers(): List<User> {
        return withContext(dispatcher) {
            val users = networkDataSource.loadUsers()
            userDao.upsertAll(users.toLocal())
            users.toExternal()
        }
    }

    override suspend fun refreshShows(): List<Show> {
        return withContext(dispatcher) {
            val shows = networkDataSource.loadShows()
            showDao.upsertAll(shows.toLocal())
            shows.toExternal()
        }
    }
}
