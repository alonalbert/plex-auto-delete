package com.alonalbert.pad.server.repository

import com.alonalbert.pad.server.model.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : JpaRepository<Post, Long>
