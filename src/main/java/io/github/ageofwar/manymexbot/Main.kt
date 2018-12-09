package io.github.ageofwar.manymexbot

import io.github.ageofwar.telejam.Bot
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.Paths

val BOT_PROPERTIES_PATH = Paths.get(".", "bot.properties")!!

fun main() = try {
    val botProperties = loadBotProperties(BOT_PROPERTIES_PATH)
    val (token, configPath) = botProperties
    val config = tryLoadConfig(configPath)
    val bot = Bot.fromToken(token)
    ManyMexBot(bot, config).run()
} catch (e: NoSuchFileException) {
    BotProperties(
            token = "123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11",
            configPath = Paths.get(".", "config.json")
    ).save(BOT_PROPERTIES_PATH)
}

fun tryLoadConfig(path: Path) = try {
    loadConfig(path)
} catch (e: NoSuchFileException) {
    Config(
    ).also {
        it.save(path)
    }
}
