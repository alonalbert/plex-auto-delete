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
  private val commands = mapOf<String, suspend (Array<String>) -> Unit>(
    "import-config" to { importConfigCommand.import() },
    "auto-watch" to { plexAutoDeleterCommand.runAutoWatch() },
    "auto-delete" to { runAutoDelete(it) },
    "get-unwatched" to { getUnwatched() },
    "get-all-shows" to { getAllShows() },
    "get-unwatched-by" to { getUnwatchedBy() },
  )

  override fun run(vararg args: String) {
    val command = commands[args.firstOrNull() ?: "absent"]
    if (command == null) {
      println("Please specify a commend to run (${commands.keys.sorted().joinToString { it }})")
      return
    }
    val commandArgs = args.drop(1).toList().toTypedArray()
    runBlocking {
      command(commandArgs)
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

  private suspend fun getAllShows() {
    println("Shows:")
    plexAutoDeleterCommand.getAllShows().forEach { (name, count) ->
      println("%3d: %s".format(count, name))
    }
  }

  private suspend fun getUnwatchedBy() {
    println("Shows:")
    plexAutoDeleterCommand.getUnwatchedBy().forEach { (name, users) ->
      println("$name: $users")
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