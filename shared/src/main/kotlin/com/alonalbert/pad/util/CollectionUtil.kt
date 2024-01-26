package com.alonalbert.pad.util

fun <T> Collection<Set<T>>.intersect(): Set<T> {
  return when (size) {
    0 -> emptySet<T>()
    1 -> first()
    else -> {
      return map { it.toMutableSet() }.reduce { acc, it ->
        acc.apply {
          retainAll(it)
        }
      }
    }
  }
}

private val data = listOf(
  setOf(
    "Better Call Saul",
    "Big Brother (US)",
    "Black Bird",
    "Boardwalk Empire",
    "Breaking Bad",
    "Candy",
    "Dancing with the Stars",
    "Echo",
    "FBoy Island",
    "Fargo",
    "Gen V",
    "Hard Knocks (2001)",
    "Heartstopper",
    "Lessons in Chemistry",
    "Living with Yourself",
    "Lucky Hank",
    "Nancy Drew (2019)",
    "Percy Jackson and the Olympians",
    "Special Forces: World's Toughest Test",
    "Super Pumped",
    "Tehran",
    "The Boys",
    "The First Lady (2022)",
    "The Morning Show",
    "The Ultimate Fighter",
    "Under Pressure: The U.S. Women's World Cup Team",
    "Warrior",
    "What If…?",
    "Yellowjackets",
    "Your Honor (US)",
  ),
  setOf(
    "Better Call Saul",
    "Breaking Bad",
    "Candy",
    "Chicago Fire",
    "Chicago P.D.",
    "Dancing with the Stars",
    "FBoy Island",
    "Fargo",
    "Hard Knocks (2001)",
    "Heartstopper",
    "Lessons in Chemistry",
    "Living with Yourself",
    "Lucky Hank",
    "Star Wars: The Clone Wars",
    "The Bachelor",
    "The First Lady (2022)",
    "The Morning Show",
    "The Ultimate Fighter",
    "Under Pressure: The U.S. Women's World Cup Team",
    "What If…?",
    "Your Honor (US)",
  ),
  setOf(
    "Better Call Saul",
    "Big Brother (US)",
    "Black Bird",
    "Boardwalk Empire",
    "Breaking Bad",
    "Candy",
    "Chicago Fire",
    "Chicago P.D.",
    "Dancing with the Stars",
    "Echo",
    "FBoy Island",
    "Fargo",
    "Gen V",
    "Hard Knocks (2001)",
    "Heartstopper",
    "Lessons in Chemistry",
    "Living with Yourself",
    "Lucky Hank",
    "Nancy Drew (2019)",
    "Percy Jackson and the Olympians",
    "Reacher",
    "Special Forces: World's Toughest Test",
    "Super Pumped",
    "Tehran",
    "The Bachelor",
    "The Boys",
    "The First Lady (2022)",
    "The Morning Show",
    "The Ultimate Fighter",
    "Under Pressure: The U.S. Women's World Cup Team",
    "Warrior",
    "Yellowjackets",
    "Your Honor (US)",
  ),
  setOf(
    "Big Brother (US)",
    "Black Bird",
    "Boardwalk Empire",
    "Chicago Fire",
    "Chicago P.D.",
    "Echo",
    "Gen V",
    "Hard Knocks (2001)",
    "Nancy Drew (2019)",
    "Percy Jackson and the Olympians",
    "Reacher",
    "Special Forces: World's Toughest Test",
    "Star Wars: The Clone Wars",
    "Tehran",
    "The Bachelor",
    "The Boys",
    "Warrior",
    "What If…?",
    "Yellowjackets",
  ),
)

fun main() {
  println(data.intersect())
}
