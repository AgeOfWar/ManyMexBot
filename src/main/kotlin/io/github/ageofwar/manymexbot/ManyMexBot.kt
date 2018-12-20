package io.github.ageofwar.manymexbot

import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot
import io.github.ageofwar.telejam.callbacks.CallbackDataHandler
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.inline.InlineQuery
import io.github.ageofwar.telejam.inline.InlineQueryHandler
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.messages.TextMessageHandler

class ManyMexBot(
        bot: Bot,
        private val config: Config
) : LongPollingBot(bot) {
    init {
        events.apply {
            config.messages?.forEach {
                registerTextMessageHandler(MessageHandler(bot, it))
            }

            config.callbacks?.forEach {
                registerCallbackDataHandler(CallbackHandler(bot, it))
            }

            config.inlineQueries?.forEach {
                registerInlineQueryHandler(InlineHandler(bot, it))
            }
        }
    }
}

class MessageHandler(private val bot: Bot, private val config: Config.OnMessage) : TextMessageHandler {
    override fun onTextMessage(message: TextMessage) {
        if (config.regex.find(message.text) != null) {
            if (config.whitelist == null || message.sender.id in config.whitelist) {
                if (config.blacklist == null || message.sender.id !in config.blacklist) {
                    bot.sendMessage(message, config.message)
                }
            }
        }
    }
}


class CallbackHandler(private val bot: Bot, private val config: Config.OnCallback) : CallbackDataHandler {
    override fun onCallbackData(callbackQuery: CallbackQuery, name: String, args: String) {
        val data = callbackQuery.data.get()
        if (data == config.callback) {
            val message = callbackQuery.message.get()
            if (config.whitelist == null || callbackQuery.sender.id in config.whitelist) {
                if (config.blacklist == null || callbackQuery.sender.id !in config.blacklist) {
                    config.message?.let { bot.sendMessage(message, it) }
                    config.answer?.let { bot.answerCallbackQuery(callbackQuery, it) }
                            ?: bot.answerCallbackQuery(callbackQuery)
                }
            }
        }
    }
}

class InlineHandler(private val bot: Bot, private val config: Config.OnInlineQuery) : InlineQueryHandler {
    override fun onInlineQuery(inlineQuery: InlineQuery) {
        if (config.whitelist == null || inlineQuery.sender.id in config.whitelist) {
            if (config.blacklist == null || inlineQuery.sender.id !in config.blacklist) {
                if (config.regex.find(inlineQuery.query) != null) {
                    bot.answerInlineQuery(inlineQuery, config)
                }
            }
        }
    }
}
