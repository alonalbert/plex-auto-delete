package com.alonalbert.rest.server.spring.repository

import com.alonalbert.rest.server.model.Customer
import org.springframework.data.repository.CrudRepository

interface CustomerRepository : CrudRepository<Customer, Long>
