package com.alonalbert.pad.server.controller

import com.alonalbert.pad.server.model.Show
import com.alonalbert.pad.server.repository.ShowRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.DriverManager

private val STATEMENT1 = """ 
  SELECT DISTINCT 
    grandparent_title as show
  FROM metadata_item_views 
  WHERE metadata_type = 4 
""".trimIndent()

private val STATEMENT2 = """
  SELECT
    D.path as show
  FROM library_sections AS S
  INNER JOIN directories AS d ON D.parent_directory_id = S.id
   WHERE S.section_type = 2 AND S.name in ('TV', '')
""".trimIndent()

private val STATEMENTS = listOf(STATEMENT1, STATEMENT2)

@RestController
@RequestMapping("/api")
class ShowController(
    private val showRepository: ShowRepository,
    private val plexDatabasePath: String,
) {
    private val logger = LoggerFactory.getLogger(ShowController::class.java)

    @GetMapping("/shows")
    fun getUsers(): List<Show> {
        updateShowsFromPlex()
        return showRepository.findAll()
    }

    private fun updateShowsFromPlex() {
        val plexShows = DriverManager.getConnection("jdbc:sqlite:${plexDatabasePath}").use { connection ->
            buildSet {
                DriverManager.getConnection("jdbc:sqlite:${plexDatabasePath}").use { connection ->
                    connection.createStatement().use { statement ->
                        STATEMENTS.forEach {
                            statement.executeQuery(it).use { result ->
                                while (result.next()) {
                                    val show = result.getString("show")
                                    if (show.isNotBlank()) {
                                        add(show)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        val shows = showRepository.findAll().associateBy { it.name }
        val newShows = plexShows - shows.keys

        if (newShows.isNotEmpty()) {
            logger.info("Adding new shows:\n  ${newShows.joinToString("\n  ") { it }}")
            showRepository.saveAll(newShows.map { Show(name = it) })
        }
    }
}
