package io.github.ageofwar.manymexbot

import java.nio.file.Files.*
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

data class BotProperties(
        val token: String,
        val configPath: Path
)

fun loadBotProperties(path: Path): BotProperties {
    val botProperties = Properties().apply {
        load(newInputStream(path))
    }
    val token = botProperties.getNotNullProperty("token")
    val configPath = Paths.get(botProperties.getNotNullProperty("config-path"))
    return BotProperties(token, configPath)
}

private fun Properties.getNotNullProperty(property: String) = getProperty(property)
        ?: throw MissingPropertyException(property)

fun BotProperties.save(path: Path) {
    createDirectories(path.parent)
    val botProperties = Properties().apply {
        setProperty("token", token)
        setProperty("config-path", configPath.toString())
    }
    botProperties.store(newOutputStream(path), null)
}

class MissingPropertyException(property: String) : Exception("cannot find property '$property'")

