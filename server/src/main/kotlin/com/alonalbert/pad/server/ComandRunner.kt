package com.alonalbert.pad.server

import com.alonalbert.pad.model.AutoDeleteResult
import com.alonalbert.pad.server.importconfig.ImportConfig
import com.alonalbert.pad.server.plex.PlexAutoDeleter
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import kotlin.time.Duration.Companion.days

@SpringBootApplication
@EntityScan("com.alonalbert.pad.*")
@ComponentScan("com.alonalbert.pad.*")
class CommandRunner(
  private val plexAutoDeleterCommand: PlexAutoDeleter,
  private val importConfigCommand: ImportConfig,
) : CommandLineRunner {
  override fun run(vararg args: String) {
    val argsArray = args.drop(1).toList().toTypedArray()
    runBlocking {
      when (args.firstOrNull()) {
        "import-config" -> importConfigCommand.import()
        "auto-watch" -> plexAutoDeleterCommand.runAutoWatch()
        "auto-delete" -> runAutoDelete(argsArray)
        "get-unwatched" -> getUnwatched()
        else -> println("Please specify a commend to run")
      }
    }
  }

  private suspend fun runAutoDelete(args: Array<String>): AutoDeleteResult {
    val parser = ArgParser("auto-delete")
    val days by parser.option(ArgType.Int, shortName = "d", description = "Number of days").default(7)
    val testOnly by parser.option(ArgType.Boolean, shortName = "t", description = "Test ony").default(true)
    parser.parse(args)

    val result = plexAutoDeleterCommand.runAutoDelete(days.days, testOnly)
    println("============================================")
    println(result)
    return result
  }

  private suspend fun getUnwatched() {
    plexAutoDeleterCommand.getUnwatchedForUsers().forEach { (user, shows) ->
      println("$user:")
      shows.forEach { (show, count) ->
        println("    $show: $count")
      }
    }
  }
}

fun main(args: Array<String>) {
  val application = SpringApplication(CommandRunner::class.java)
  application.setDefaultProperties(
    mapOf(
      "spring.main.web-application-type" to "NONE",
      "logging.level.com.alonalbert.pad.server." to "DEBUG",
    )
  )
  application.run(*args).close()
}