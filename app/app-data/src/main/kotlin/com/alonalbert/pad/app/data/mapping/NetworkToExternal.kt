package com.alonalbert.pad.app.data.mapping

import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.UserShow
import com.alonalbert.pad.model.Show as NetworkShow
import com.alonalbert.pad.model.User as NetworkUser
import com.alonalbert.pad.model.UserShow as NetworkUserShow

@Suppress("unused")
internal object NetworkToExternal {
    internal fun NetworkUser.toExternal() = User(id = id, name = name, plexToken = plexToken, type = type.toExternal(), shows.toExternal())

    internal fun NetworkShow.toExternal() = Show(id = id, name = name)

    internal fun NetworkUserShow.toExternal() = UserShow(userId = userId, showId = showId)

    @JvmName("localToExternalUser")
    internal fun List<NetworkUser>.toExternal() = map { it.toExternal() }

    @JvmName("localToExternalShow")
    internal fun List<NetworkShow>.toExternal() = map { it.toExternal() }

    @JvmName("localToExternalUserShow")
    internal fun List<NetworkUserShow>.toExternal() = map { it.toExternal() }

    internal fun NetworkUser.UserType.toExternal() = when (this) {
        NetworkUser.UserType.EXCLUDE -> User.UserType.EXCLUDE
        NetworkUser.UserType.INCLUDE -> User.UserType.INCLUDE
    }
}