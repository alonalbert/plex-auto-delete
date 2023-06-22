package com.alonalbert.pad.model

import com.alonalbert.pad.model.User.UserType.INCLUDE
import kotlinx.serialization.Serializable
import javax.persistence.CascadeType.ALL
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.AUTO
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany

@Serializable
@Entity
data class User(
    @Id @GeneratedValue(strategy = AUTO)
    val id: Long = 0,
    val name: String = "",
    val plexToken: String = "",
    val type: UserType = INCLUDE,
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
