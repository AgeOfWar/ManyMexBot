package io.github.ageofwar.manymexbot

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.github.ageofwar.telejam.json.Json.fromJson
import io.github.ageofwar.telejam.json.Json.toPrettyJson
import io.github.ageofwar.telejam.replymarkups.ReplyMarkup
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files.*
import java.nio.file.Path

data class Config(
        @SerializedName("on_message") val messages: List<OnMessage> = listOf(),
        @SerializedName("on_callback") val callbacks: List<OnCallback> = listOf()
) {
    data class OnMessage(
            @SerializedName("trigger") @JsonAdapter(RegexTypeAdapter::class) val regex: Regex,
            @SerializedName("user_id") val userId: Long? = null,
            @SerializedName("message") val message: Message
    )
    data class Message(
            @SerializedName("replies", alternate = ["reply"]) val replies: List<String> = listOf(),
            @SerializedName("files", alternate = ["file"]) val files: List<String> = listOf(),
            @SerializedName("send_as_reply") val sendAsReply: Boolean = true,
            @SerializedName("reply_markup") val replyMarkup: ReplyMarkup? = null
    )
    data class OnCallback(
            @SerializedName("callback") val callback: String,
            @SerializedName("message") val message: Message? = null,
            @SerializedName("answer") val answer: CallbackAnswer? = null
    )
    data class CallbackAnswer(
            @SerializedName("text") val text: List<String> = listOf(),
            @SerializedName("show_alert") val showAlert: Boolean = false
    )
}

fun loadConfig(path: Path): Config {
    return fromJson(newBufferedReader(path), Config::class.java)
}

fun Config.save(path: Path) {
    createDirectories(path.parent)
    newBufferedWriter(path, UTF_8).use {
        toPrettyJson(this, it)
    }
}

private class RegexTypeAdapter : TypeAdapter<Regex>() {
    override fun write(out: JsonWriter, regex: Regex?) {
        out.value(regex.toString())
    }

    override fun read(`in`: JsonReader): Regex? {
        return Regex(`in`.nextString())
    }
}
