package com.alonalbert.pad.server.controller

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
@RequestMapping("/plex")
class PlexController(private val plexDatabasePath: String) {
    @GetMapping("/shows")
    fun getShowsNames(): List<String> {
        return buildSet {
            DriverManager.getConnection("jdbc:sqlite:${plexDatabasePath}").use { connection ->
                connection.createStatement().use { statement ->
                    STATEMENTS.forEach {
                        statement.executeQuery(it).use { result ->
                            while (result.next()) {
                                add(result.getString("show"))
                            }
                        }
                    }
                }
            }
        }.sorted()
    }
}
