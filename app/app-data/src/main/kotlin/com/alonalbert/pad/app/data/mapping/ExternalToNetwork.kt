package com.alonalbert.pad.app.data.mapping

import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.UserShow
import com.alonalbert.pad.model.Show as NetworkShow
import com.alonalbert.pad.model.User as NetworkUser
import com.alonalbert.pad.model.UserShow as NetworkUserShow

@Suppress("unused")
internal object ExternalToNetwork {
    internal fun User.toNetwork() = NetworkUser(id = id, name = name, plexToken = plexToken, type = type.toNetwork(), shows.toNetwork())

    internal fun Show.toNetwork() = NetworkShow(id = id, name = name)

    internal fun UserShow.toNetwork() = NetworkUserShow(userId = userId, showId = showId)

    @JvmName("externalToNetworkUser")
    internal fun List<User>.toNetwork() = map { it.toNetwork() }

    @JvmName("externalToNetworkShow")
    internal fun List<Show>.toNetwork() = map { it.toNetwork() }

    @JvmName("externalToNetworkUserShow")
    internal fun List<UserShow>.toNetwork() = map { it.toNetwork() }

    internal fun User.UserType.toNetwork() = when (this) {
        User.UserType.EXCLUDE -> NetworkUser.UserType.EXCLUDE
        User.UserType.INCLUDE -> NetworkUser.UserType.INCLUDE
    }
}