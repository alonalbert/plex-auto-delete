package com.alonalbert.pad.server.repository

import com.alonalbert.pad.server.model.Customer
import org.springframework.data.repository.CrudRepository

interface CustomerRepository : CrudRepository<Customer, Long>
