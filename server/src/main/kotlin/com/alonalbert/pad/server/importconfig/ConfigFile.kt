package com.alonalbert.pad.server.importconfig


data class ConfigFile(
  val plexUrl: String = "",
  val days: Long = 0,
  val tvSections: Set<String> = emptySet(),
  val users: List<User> = emptyList(),
  val pushoverConfig: PushoverConfig = PushoverConfig(),
)

data class User(val name: String = "", val plexToken: String = "", val shows: Shows = Shows())

data class Shows(val titles: Set<String> = emptySet(), val type: Type = Type.INCLUDE) {
  enum class Type { INCLUDE, EXCLUDE }
}

data class PushoverConfig(val appToken: String = "", val userToken: String = "")
