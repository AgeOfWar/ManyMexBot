package io.github.ageofwar.manymexbot

import io.github.ageofwar.manymexbot.regex.combine
import io.github.ageofwar.manymexbot.regex.format
import io.github.ageofwar.manymexbot.regex.groupMap
import io.github.ageofwar.manymexbot.regex.toGroupMap
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot
import io.github.ageofwar.telejam.callbacks.CallbackDataHandler
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.chats.Chat
import io.github.ageofwar.telejam.messages.NewChatMemberHandler
import io.github.ageofwar.telejam.messages.NewChatMembersMessage
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.messages.TextMessageHandler
import io.github.ageofwar.telejam.text.Text
import io.github.ageofwar.telejam.users.User

class ManyMexBot(
        bot: Bot,
        private val config: Config
) : LongPollingBot(bot) {
    init {
        events.apply {
            config.messages?.forEach {
                registerTextMessageHandler(MessageHandler(bot, it))
            }

            config.welcomeMessages?.let {
                registerNewChatMemberHandler(WelcomeHandler(bot, it))
            }

            config.callbacks?.forEach {
                registerCallbackDataHandler(CallbackHandler(bot, it))
            }
        }
    }

    override fun onError(t: Throwable) = t.printStackTrace()
}

class MessageHandler(private val bot: Bot, private val config: Config.OnMessage) : TextMessageHandler {
    override fun onTextMessage(message: TextMessage) {
        val matcher = config.regex.toPattern().matcher(message.text)
        if (matcher.find()) {
            if (config.whitelist == null || message.sender.id in config.whitelist) {
                if (config.blacklist == null || message.sender.id !in config.blacklist) {
                    val mention = Text.textMention(message.sender).toHtmlString()
                    val groupMap = combine(
                            matcher.toGroupMap(),
                            groupMap(namedGroups = mapOf("mention" to mention))
                    )
                    bot.sendMessage(message, config.message, groupMap)
                }
            }
        }
    }
}

class WelcomeHandler(private val bot: Bot, private val messages: List<String>) : NewChatMemberHandler {
    override fun onNewChatMember(chat: Chat, user: User, message: NewChatMembersMessage) {
        val mention = Text.textMention(user).toHtmlString()
        val groupMap = groupMap(namedGroups = mapOf("mention" to mention))
        bot.sendMessage(message, Text.parseHtml(format(messages.random(), groupMap)))
    }
}


class CallbackHandler(private val bot: Bot, private val config: Config.OnCallback) : CallbackDataHandler {
    override fun onCallbackData(callbackQuery: CallbackQuery, name: String, args: String) {
        val data = callbackQuery.data.get()
        if (data == config.callback) {
            val message = callbackQuery.message.get()
            if (config.whitelist == null || callbackQuery.sender.id in config.whitelist) {
                if (config.blacklist == null || callbackQuery.sender.id !in config.blacklist) {
                    val mention = Text.textMention(callbackQuery.sender).toHtmlString()
                    val groupMap = groupMap(namedGroups = mapOf("mention" to mention))
                    config.message?.let { bot.sendMessage(message, it, groupMap) }
                    config.answer?.let { bot.answerCallbackQuery(callbackQuery, it, groupMap) }
                            ?: bot.answerCallbackQuery(callbackQuery)
                }
            }
        }
    }
}
