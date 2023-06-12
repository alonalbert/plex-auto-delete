/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("unused")

package com.alonalbert.pad.app.data

import com.alonalbert.pad.app.data.source.local.database.LocalShow
import com.alonalbert.pad.app.data.source.local.database.LocalUser
import com.alonalbert.pad.app.data.source.local.database.LocalUserWithShows
import com.alonalbert.pad.app.data.source.network.NetworkShow
import com.alonalbert.pad.app.data.source.network.NetworkUser

/**
 * Data model mapping extension functions. There are three model types:
 *
 * - External: Model exposed to other layers in the architecture.
 * Obtained using `toExternal`.
 *
 * - Network: Internal model used to represent a user from the network. Obtained using
 * `toNetwork`.
 *
 * - Local: Internal model used to represent a user stored locally in a database. Obtained
 * using `toLocal`.
 *
 */

/**
 * External to local
 */

fun User.toLocal() = LocalUser(id = id, name = name, plexToken = plexToken, type = type.toLocal())
fun User.UserType.toLocal() = when (this) {
    User.UserType.EXCLUDE -> LocalUser.UserType.EXCLUDE
    User.UserType.INCLUDE -> LocalUser.UserType.INCLUDE
}

@JvmName("externalToLocalUser")
fun List<User>.toLocal() = map(User::toLocal)

fun Show.toLocal() = LocalShow(id = id, name = name)

@JvmName("externalToLocalShow")
fun List<Show>.toLocal() = map(Show::toLocal)


// Local to External
fun LocalUser.toExternal() = User(id = id, name = name, plexToken = plexToken, type = type.toExternal())
fun LocalUser.UserType.toExternal() = when (this) {
    LocalUser.UserType.EXCLUDE -> User.UserType.EXCLUDE
    LocalUser.UserType.INCLUDE -> User.UserType.INCLUDE
}

fun LocalShow.toExternal() = Show(id = id, name = name)
fun LocalUserWithShows.toExternal() = UserWithShows(user = user.toExternal(), shows = shows.toExternal())

@JvmName("localToExternalUser")
fun List<LocalUser>.toExternal() = map(LocalUser::toExternal)

@JvmName("localToExternalShow")
fun List<LocalShow>.toExternal() = map(LocalShow::toExternal)

@JvmName("localToExternalUserWithShows")
fun List<LocalUserWithShows>.toExternal() = map(LocalUserWithShows::toExternal)

// Network to Local
fun NetworkUser.toLocal() = LocalUser(id = id, name = name, plexToken = plexToken, type = type.toLocal())
fun NetworkUser.UserType.toLocal() = when (this) {
    NetworkUser.UserType.EXCLUDE -> LocalUser.UserType.EXCLUDE
    NetworkUser.UserType.INCLUDE -> LocalUser.UserType.INCLUDE
}

@JvmName("networkToLocalUser")
fun List<NetworkUser>.toLocal() = map(NetworkUser::toLocal)
fun NetworkShow.toLocal() = LocalShow(id = id, name = name)

@JvmName("networkToLocalShow")
fun List<NetworkShow>.toLocal() = map(NetworkShow::toLocal)

// Local to Network
fun LocalUser.toNetwork() = NetworkUser(id = id, name = name, plexToken = plexToken, type = type.toNetwork())
fun LocalUser.UserType.toNetwork() = when (this) {
    LocalUser.UserType.EXCLUDE -> NetworkUser.UserType.EXCLUDE
    LocalUser.UserType.INCLUDE -> NetworkUser.UserType.INCLUDE
}

@JvmName("localToNetworkUser")
fun List<LocalUser>.toNetwork() = map(LocalUser::toNetwork)
fun LocalShow.toNetwork() = NetworkShow(id = id, name = name)

@JvmName("localToNetworkShow")
fun List<LocalShow>.toNetwork() = map(LocalShow::toNetwork)


// External to Network
fun User.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetworkUser")
fun List<User>.toNetwork() = map(User::toNetwork)
fun Show.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetworkShow")
fun List<Show>.toNetwork() = map(Show::toNetwork)

// Network to External
fun NetworkUser.toExternal() = toLocal().toExternal()

@JvmName("networkToExternalUser")
fun List<NetworkUser>.toExternal() = map(NetworkUser::toExternal)
fun NetworkShow.toExternal() = toLocal().toExternal()

@JvmName("networkToExternalShow")
fun List<NetworkShow>.toExternal() = map(NetworkShow::toExternal)
