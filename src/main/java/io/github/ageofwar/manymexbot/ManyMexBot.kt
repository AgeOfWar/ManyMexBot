package io.github.ageofwar.manymexbot

import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot
import io.github.ageofwar.telejam.callbacks.CallbackDataHandler
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.messages.TextMessageHandler

class ManyMexBot(
        bot: Bot,
        private val config: Config
) : LongPollingBot(bot) {
    init {
        events.apply {
            config.messages.forEach {
                registerTextMessageHandler(MessageHandler(bot, it))
            }

            config.callbacks.forEach {
                registerCallbackDataHandler(CallbackHandler(bot, it))
            }
        }
    }
}

class MessageHandler(private val bot: Bot, private val config: Config.OnMessage) : TextMessageHandler {
    override fun onTextMessage(message: TextMessage) {
        if (config.regex.find(message.text) != null) {
            if (config.userId == null || config.userId == message.sender.id) {
                bot.sendMessage(message, config.message)
            }
        }
    }
}


class CallbackHandler(private val bot: Bot, private val config: Config.OnCallback) : CallbackDataHandler {
    override fun onCallbackData(callbackQuery: CallbackQuery, name: String, args: String) {
        val data = callbackQuery.data.get()
        if (data == config.callback) {
            val message = callbackQuery.message.get()
            config.message?.let { bot.sendMessage(message, it) }
            config.answer?.let { bot.answerCallbackQuery(callbackQuery, it) }
        }
    }
}
