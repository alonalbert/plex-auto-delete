package com.alonalbert.pad.server.repository

import com.alonalbert.pad.server.model.Show
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ShowRepository : JpaRepository<Show, Long>
