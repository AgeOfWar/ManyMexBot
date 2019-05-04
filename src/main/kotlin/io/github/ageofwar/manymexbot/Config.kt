package io.github.ageofwar.manymexbot

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.google.gson.stream.MalformedJsonException
import io.github.ageofwar.telejam.replymarkups.ReplyMarkup
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files.*
import java.nio.file.Path
import java.util.*


data class Config(
        @SerializedName("on_message") val messages: List<OnMessage>? = null,
        @SerializedName("welcome_message") val welcomeMessages: List<String>? = null,
        @SerializedName("on_callback") val callbacks: List<OnCallback>? = null
) {
    data class OnMessage(
            @SerializedName("trigger") val regex: Regex,
            @SerializedName("whitelist") val whitelist: List<Long>? = null,
            @SerializedName("blacklist") val blacklist: List<Long>? = null,
            @SerializedName("message") val message: Message
    )
    data class Message(
            @SerializedName("text") val text: List<String> = listOf(),
            @SerializedName("files", alternate = ["file"]) val files: List<String> = listOf(),
            @SerializedName("send_as_reply") val sendAsReply: Boolean = true,
            @SerializedName("reply_markup") val replyMarkup: ReplyMarkup? = null
    )
    data class OnCallback(
            @SerializedName("callback") val callback: String,
            @SerializedName("whitelist") val whitelist: List<Long>? = null,
            @SerializedName("blacklist") val blacklist: List<Long>? = null,
            @SerializedName("message") val message: Message? = null,
            @SerializedName("answer") val answer: CallbackAnswer? = null
    )
    data class CallbackAnswer(
            @SerializedName("text") val text: List<String> = listOf(),
            @SerializedName("show_alert") val showAlert: Boolean = false
    )
}

fun loadConfig(path: Path): Config {
    return gson.fromJson(newBufferedReader(path), Config::class.java)
}

fun Config.save(path: Path) {
    createDirectories(path.parent)
    newBufferedWriter(path, UTF_8).use {
        gson.toJson(this, it)
    }
}

private val gson = GsonBuilder()
        .registerTypeAdapter(Regex::class.java, RegexTypeAdapter())
        .registerTypeAdapterFactory(AlwaysListTypeAdapterFactory())
        .setPrettyPrinting()
        .create()

private class RegexTypeAdapter : TypeAdapter<Regex>() {
    override fun write(out: JsonWriter, regex: Regex?) {
        out.value(regex.toString())
    }

    override fun read(`in`: JsonReader): Regex? {
        return Regex(`in`.nextString())
    }
}

private class AlwaysListTypeAdapterFactory : TypeAdapterFactory {

    @Suppress("UNCHECKED_CAST")
    override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
        if (!List::class.java.isAssignableFrom(typeToken.rawType)) {
            return null
        }
        val elementType = resolveTypeArgument(typeToken.type)
        val elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType)) as TypeAdapter<T>
        return AlwaysListTypeAdapter(elementTypeAdapter).nullSafe() as TypeAdapter<T>
    }

    private fun resolveTypeArgument(type: Type): Type {
        if (type !is ParameterizedType) {
            return Any::class.java
        }
        return type.actualTypeArguments[0]
    }

    private class AlwaysListTypeAdapter<E>(private val elementTypeAdapter: TypeAdapter<E>) : TypeAdapter<List<E>>() {

        override fun write(out: JsonWriter, list: List<E>) {
            throw UnsupportedOperationException()
        }

        @Throws(IOException::class)
        override fun read(`in`: JsonReader): List<E> {
            val list = ArrayList<E>()
            when (val token = `in`.peek()) {
                JsonToken.BEGIN_ARRAY -> {
                    `in`.beginArray()
                    while (`in`.hasNext()) {
                        list.add(elementTypeAdapter.read(`in`))
                    }
                    `in`.endArray()
                }
                JsonToken.BEGIN_OBJECT, JsonToken.STRING, JsonToken.NUMBER, JsonToken.BOOLEAN -> list.add(elementTypeAdapter.read(`in`))
                JsonToken.NULL -> throw AssertionError("Must never happen: check if the type adapter configured with .nullSafe()")
                JsonToken.NAME, JsonToken.END_ARRAY, JsonToken.END_OBJECT, JsonToken.END_DOCUMENT -> throw MalformedJsonException("Unexpected token: $token")
                else -> throw AssertionError("Must never happen: $token")
            }
            return list
        }

    }

}
