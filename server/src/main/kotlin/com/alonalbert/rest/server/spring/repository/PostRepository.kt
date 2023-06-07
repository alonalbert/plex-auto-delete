package com.alonalbert.rest.server.spring.repository

import com.alonalbert.rest.server.spring.model.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : JpaRepository<Post, Long>
