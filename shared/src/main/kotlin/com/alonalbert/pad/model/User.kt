package com.alonalbert.pad.model

import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.EAGER
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class User(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val name: String = "",
    val plexToken: String = "",
    val type: UserType = UserType.INCLUDE,
    @ManyToMany(cascade = [ALL], fetch = EAGER)
    @JoinTable(
        name = "user_show",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "show_id", referencedColumnName = "id")]
    )
    val shows: List<Show> = emptyList(),

    ) {
    enum class UserType {
        EXCLUDE,
        INCLUDE,
    }
}
