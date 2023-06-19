package com.alonalbert.pad.util

fun <T> Collection<Set<T>>.intersect(): Set<T> {
    return when (size) {
        0 -> emptySet<T>()
        1 -> first()
        else -> {
            val sets = sortedBy { it.size }
            HashSet(sets.first()).also { result -> drop(1).forEach { result.retainAll(it) } }
        }
    }
}
