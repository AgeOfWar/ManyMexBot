package io.github.ageofwar.manymexbot.regex

import java.io.Reader
import java.util.regex.Matcher

interface GroupMap {
    companion object {
        fun empty() = object : GroupMap {
            override fun get(index: Int): Nothing? = null
            override fun get(name: String): Nothing? = null
        }
    }
    operator fun get(index: Int): String?
    operator fun get(name: String): String?
}

fun combine(groupMap1: GroupMap, groupMap2: GroupMap) = object : GroupMap {
    override fun get(index: Int) = groupMap1[index] ?: groupMap2[index]
    override fun get(name: String) = groupMap1[name] ?: groupMap2[name]
}

fun format(format: String, groupMap: GroupMap) = buildString {
    fun Reader.readUntil(predicate: (Char) -> Boolean) = buildString {
        while(true) {
            val char = read()
            if (char == -1) break
            val delimiterFound = predicate(char.toChar())
            if (delimiterFound) {
                break
            } else {
                append(char.toChar())
            }
        }
    }

    fun Reader.readUntil(delimiter: Char, message: () -> String) = buildString {
        while(true) {
            val char = read()
            if (char == -1) throw IllegalArgumentException(message())
            if (char.toChar() == delimiter) {
                break
            } else {
                append(char.toChar())
            }
        }
    }

    fun findGroup(name: String) = groupMap[name]
            ?: throw throw IllegalArgumentException("No group with name {$name}")
    fun findGroup(index: Int) = groupMap[index]
            ?: throw throw IllegalArgumentException("No group with index {$index}")

    val reader = format.reader()
    var c = reader.read()
    while (c > 0) {
        when (c.toChar()) {
            '$' -> {
                c = reader.read()
                if (c == -1) {
                    throw IllegalArgumentException("named capturing group has 0 length name")
                }
                when (val char = c.toChar()) {
                    in '0'..'9' -> {
                        val group = (char + reader.readUntil { it !in '0'..'9' }).toInt()
                        append(findGroup(group))
                    }
                    in 'A'..'Z', in 'a'..'z' -> {
                        val group = char + reader.readUntil { it !in 'A'..'Z' && it !in 'a'..'z' }
                        append(findGroup(group))
                    }
                    '{' -> {
                        val group = reader.readUntil('}') { "named capturing group is missing trailing '}'" }
                        if (group[0] in '0'..'9') {
                            try {
                                append(findGroup(group.toInt()))
                            } catch (e: NumberFormatException) {
                                throw IllegalArgumentException("capturing group name {$group} starts with digit character")
                            }
                        } else {
                            append(findGroup(group))
                        }
                    }
                    else -> append("$$char")
                }
            }
            '\\' -> {
                c = reader.read()
                when (c) {
                    '$'.toInt() -> append(c.toChar())
                    else -> throw IllegalArgumentException("Invalid escape character")
                }
            }
            else -> append(c.toChar())
        }
        c = reader.read()
    }
}

fun groupMap(groups: List<String> = emptyList(),
             namedGroups: Map<String, String> = emptyMap()) = object : GroupMap {
    override fun get(index: Int) = groups.getOrNull(index)
    override fun get(name: String) = namedGroups[name]
}

fun Matcher.toGroupMap() = object : GroupMap {
    override fun get(index: Int) = try {
        group(index)
    } catch (e: IllegalStateException) {
        null
    } catch (e: IndexOutOfBoundsException) {
        null
    }

    override fun get(name: String) = try {
        group(name)
    } catch (e: IllegalArgumentException) {
        null
    }
}
