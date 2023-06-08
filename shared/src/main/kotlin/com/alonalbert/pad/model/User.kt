package com.alonalbert.pad.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class User(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,
    var name: String = "",
    var plexToken: String? = null,
    var type: UserType = UserType.INCLUDE
) {
    enum class UserType {
         EXCLUDE,
        INCLUDE,
    }
}
