package com.alonalbert.rest.server.spring.controller

import com.alonalbert.rest.server.spring.model.Customer
import com.alonalbert.rest.server.spring.repository.CustomerRepository
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Optional

@RestController
@RequestMapping("/api")
class CustomerController(val repository: CustomerRepository) {

    @GetMapping("/customers")
    fun findAll(): Iterable<Customer> = repository.findAll()

    @PostMapping("/customers")
    fun addCustomer(@Valid @RequestBody customer: Customer): Customer = repository.save(customer)

    @PutMapping("/customers/{id}")
    fun updateCustomer(@Valid @PathVariable id: Long, @RequestBody customer: Customer) {
        assert(customer.id == id)
        repository.save(customer)
    }

    @DeleteMapping("/customers/{id}")
    fun removeCustomer(@PathVariable id: Long) = repository.deleteById(id)

    @GetMapping("/customers/{id}")
    fun getById(@PathVariable id: Long): Optional<Customer> = repository.findById(id)
}
