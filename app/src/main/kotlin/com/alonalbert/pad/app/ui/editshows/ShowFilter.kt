package com.alonalbert.pad.app.ui.editshows

import android.os.Parcelable
import com.alonalbert.pad.app.data.Show
import kotlinx.parcelize.Parcelize

private val WHITESPACE = "\\s".toRegex()

@Parcelize
data class ShowFilter(val query: String, val selectedOnly: Boolean) : Parcelable {

  fun filter(shows: List<Show>, selectedShowIds: Set<Long>): List<Show> {
    fun List<Show>.filterByState() = filter { !selectedOnly || it.id in selectedShowIds }

    return shows.filterByState().filterByWords()
  }

  private fun List<Show>.filterByWords(): List<Show> {
    val predicates: List<(Show) -> Boolean> = query
      .split(WHITESPACE)
      .map { term -> { show -> show.name.contains(term, ignoreCase = true) } }

    return filter { show -> predicates.all { predicate -> predicate(show) } }
  }
}
