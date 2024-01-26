package com.alonalbert.pad.app.data.mapping

import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.UserShow
import com.alonalbert.pad.app.data.local.database.LocalShow
import com.alonalbert.pad.app.data.local.database.LocalUser
import com.alonalbert.pad.app.data.local.database.LocalUserShow

@Suppress("unused")
internal object ExternalToLocal {
  internal fun User.toLocal() = LocalUser(id = id, name = name, plexToken = plexToken, type = type.toLocal(), shows.toLocal())

  internal fun Show.toLocal() = LocalShow(id = id, name = name)

  internal fun UserShow.toLocal() = LocalUserShow(userId = userId, showId = showId)

  @JvmName("externalToLocalUser")
  internal fun List<User>.toLocal() = map { it.toLocal() }

  @JvmName("externalToLocalShow")
  internal fun List<Show>.toLocal() = map { it.toLocal() }

  @JvmName("externalToLocalUserShow")
  internal fun List<UserShow>.toLocal() = map { it.toLocal() }

  internal fun User.UserType.toLocal() = when (this) {
    User.UserType.EXCLUDE -> LocalUser.UserType.EXCLUDE
    User.UserType.INCLUDE -> LocalUser.UserType.INCLUDE
  }
}