package com.alonalbert.pad.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication
@EntityScan("com.alonalbert.pad")
class Server

fun main(args: Array<String>) {
    runApplication<Server>(*args)
}
