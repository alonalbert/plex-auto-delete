package com.alonalbert.pad.app.data.mapping

import com.alonalbert.pad.app.data.AutoDeleteResult
import com.alonalbert.pad.app.data.AutoWatchResult
import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.UserShow
import com.alonalbert.pad.model.AutoDeleteResult as NetworkAutoDeleteResult
import com.alonalbert.pad.model.AutoWatchResult as NetworkAutoWatchResult
import com.alonalbert.pad.model.Show as NetworkShow
import com.alonalbert.pad.model.User as NetworkUser
import com.alonalbert.pad.model.UserShow as NetworkUserShow

@Suppress("unused")
internal object NetworkToExternal {
    fun NetworkUser.toExternal() = User(id, name, plexToken, type.toExternal(), shows.toExternal())

    fun NetworkShow.toExternal() = Show(id, name)

    fun NetworkUserShow.toExternal() = UserShow(userId, showId)

    @JvmName("localToExternalUser")
    fun List<NetworkUser>.toExternal() = map { it.toExternal() }

    @JvmName("localToExternalShow")
    fun List<NetworkShow>.toExternal() = map { it.toExternal() }

    @JvmName("localToExternalUserShow")
    fun List<NetworkUserShow>.toExternal() = map { it.toExternal() }

    fun NetworkUser.UserType.toExternal() = when (this) {
        NetworkUser.UserType.EXCLUDE -> User.UserType.EXCLUDE
        NetworkUser.UserType.INCLUDE -> User.UserType.INCLUDE
    }

    fun NetworkAutoWatchResult.toExternal() = AutoWatchResult(users.toExternal())

    fun NetworkAutoDeleteResult.toExternal() = AutoDeleteResult(numFiles, numBytes, shows)
}