package com.alonalbert.pad.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource

@SpringBootApplication
@PropertySource("classpath:local.properties")
@EntityScan("com.alonalbert.pad.*")
class Server

fun main(args: Array<String>) {
    runApplication<Server>(*args)
}
