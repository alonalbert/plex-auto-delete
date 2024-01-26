package com.alonalbert.pad.app.data.local.database

import androidx.room.Room
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class RoomLocalDataSourceTest {
  private val db = Room.inMemoryDatabaseBuilder(RuntimeEnvironment.getApplication(), AppDatabase::class.java).build()

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun name(): Unit = runBlocking {
    db.userDao().upsertAll(listOf(LocalUser(name = "Alon")))
    db.showDao().upsertAll(listOf(LocalShow(name = "Show")))
    db.userShowDao().insertAll(listOf(LocalUserShow(userId = 0, showId = 0)))

    val users = db.userDao().observeAll().take(1).toList().flatten()

    assertThat(users).containsExactly(
      LocalUser(id = 0, name = "Alon", plexToken = null, type = LocalUser.UserType.INCLUDE, shows = listOf(LocalShow(id = 0, name = "Show"))),
    )
  }
}