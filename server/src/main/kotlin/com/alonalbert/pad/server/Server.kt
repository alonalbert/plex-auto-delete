package com.alonalbert.pad.server

import com.alonalbert.pad.server.plex.PlexAutoDeleter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.TimeUnit

@SpringBootApplication
@PropertySource("classpath:local.properties")
@EntityScan("com.alonalbert.pad.*")
@EnableScheduling
class Server(private val plexAutoDeleter: PlexAutoDeleter) {
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 10)
    fun autoWatch() {
        runBlocking(Dispatchers.Default) {
            plexAutoDeleter.runAutoWatch()
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun autoDelete() {
        runBlocking(Dispatchers.Default) {
            plexAutoDeleter.runAutoDelete()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Server>(*args)
}
