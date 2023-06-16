package com.alonalbert.pad.app.data.mapping

import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.UserShow
import com.alonalbert.pad.app.data.UserWithShows
import com.alonalbert.pad.app.data.local.database.LocalShow
import com.alonalbert.pad.app.data.local.database.LocalUser
import com.alonalbert.pad.app.data.local.database.LocalUserShow
import com.alonalbert.pad.app.data.local.database.LocalUserWithShows

@Suppress("unused")
internal object LocalToExternal {
    internal fun LocalUser.toExternal() = User(id = id, name = name, plexToken = plexToken, type = type.toExternal(), shows.toExternal())

    internal fun LocalShow.toExternal() = Show(id = id, name = name)

    internal fun LocalUserShow.toExternal() = UserShow(userId = userId, showId = showId)

    internal fun LocalUserWithShows.toExternal() = UserWithShows(user = user.toExternal(), shows = shows.toExternal())

    @JvmName("localToExternalUser")
    internal fun List<LocalUser>.toExternal() = map { it.toExternal() }

    @JvmName("localToExternalShow")
    internal fun List<LocalShow>.toExternal() = map { it.toExternal() }

    @JvmName("localToExternalUserShow")
    internal fun List<LocalUserShow>.toExternal() = map { it.toExternal() }

    internal fun LocalUser.UserType.toExternal() = when (this) {
        LocalUser.UserType.EXCLUDE -> User.UserType.EXCLUDE
        LocalUser.UserType.INCLUDE -> User.UserType.INCLUDE
    }
}