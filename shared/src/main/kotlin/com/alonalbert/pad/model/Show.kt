package com.alonalbert.pad.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Show(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val name: String = "",
)