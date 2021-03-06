package io.github.ageofwar.manymexbot

import io.github.ageofwar.manymexbot.regex.GroupMap
import io.github.ageofwar.manymexbot.regex.format
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.connection.UploadFile
import io.github.ageofwar.telejam.messages.DocumentMessage
import io.github.ageofwar.telejam.messages.Message
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.methods.AnswerCallbackQuery
import io.github.ageofwar.telejam.methods.SendDocument
import io.github.ageofwar.telejam.methods.SendMessage
import io.github.ageofwar.telejam.replymarkups.ReplyMarkup
import io.github.ageofwar.telejam.text.Text

fun Bot.sendMessage(replyToMessage: Message,
                    message: Config.Message,
                    groupMap: GroupMap = GroupMap.empty()): Message? {
    return if (message.text.isNotEmpty() || message.files.isNotEmpty()) {
        val reply = Text.parseHtml(if (message.text.isNotEmpty()) format(message.text.random(), groupMap) else null)
        val file = if (message.files.isNotEmpty()) message.files.random() else null
        val replyMarkup = message.replyMarkup
        val sendAsReply = message.sendAsReply
        if (message.files.isEmpty()) {
            sendMessage(replyToMessage, reply, replyMarkup, sendAsReply)
        } else {
            UploadFile.fromFile(file).use {
                sendDocument(replyToMessage, it, reply, replyMarkup, sendAsReply)
            }
        }
    } else null
}

fun Bot.sendMessage(replyToMessage: Message,
                    text: Text,
                    replyMarkup: ReplyMarkup? = null,
                    sendAsReply: Boolean = true): TextMessage {
    val sendMessage = SendMessage().apply {
        if (sendAsReply) {
            replyToMessage(replyToMessage)
        } else {
            chat(replyToMessage.chat)
        }
        text(text)
        replyMarkup(replyMarkup)
    }
    return execute(sendMessage)
}

fun Bot.sendDocument(replyToMessage: Message,
                     file: UploadFile,
                     caption: Text? = null,
                     replyMarkup: ReplyMarkup? = null,
                     sendAsReply: Boolean = true): DocumentMessage {
    val sendDocument = SendDocument().apply {
        if (sendAsReply) {
            replyToMessage(replyToMessage)
        } else {
            chat(replyToMessage.chat)
        }
        document(file)
        caption(caption)
        replyMarkup(replyMarkup)
    }
    return execute(sendDocument)
}

fun Bot.answerCallbackQuery(callbackQuery: CallbackQuery,
                            answer: Config.CallbackAnswer,
                            groupMap: GroupMap = GroupMap.empty()) {
    val text = format(answer.text.random(), groupMap)
    answerCallbackQuery(callbackQuery, text, answer.showAlert)
}

fun Bot.answerCallbackQuery(
        callbackQuery: CallbackQuery,
        text: String? = null,
        showAlert: Boolean = false,
        cacheTime: Int? = null) {
    val answerCallbackQuery = AnswerCallbackQuery()
            .callbackQuery(callbackQuery)
            .text(text)
            .cacheTime(cacheTime)
            .showAlert(showAlert)
    execute(answerCallbackQuery)
}
