package com.alonalbert.pad.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class User(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val name: String = "",
    val plexToken: String? = null,
    val type: UserType = UserType.INCLUDE,
) {
    enum class UserType {
        EXCLUDE,
        INCLUDE,
    }
}
