package com.example.data.api

import com.example.data.model.DiscordGuild
import com.example.data.model.DiscordUser
import com.example.data.model.WebhookEmbedPayload
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class DiscordApiClient {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun validateBotToken(rawToken: String): Result<DiscordUser> = withContext(Dispatchers.IO) {
        try {
            val token = cleanToken(rawToken)
            val request = Request.Builder()
                .url("https://discord.com/api/v10/users/@me")
                .header("Authorization", "Bot $token")
                .header("User-Agent", "DiscordArchitect/1.0")
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    JSONObject(body).optString("message", "Error HTTP ${response.code}")
                } catch (e: Exception) {
                    "Error HTTP ${response.code}: $body"
                }
                return@withContext Result.failure(Exception("Gagal verifikasi Bot: $errorMsg (Periksa kembali token bot Anda)"))
            }

            val userAdapter = moshi.adapter(DiscordUser::class.java)
            val user = userAdapter.fromJson(body)
                ?: return@withContext Result.failure(Exception("Format user tidak valid"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchBotGuilds(rawToken: String): Result<List<DiscordGuild>> = withContext(Dispatchers.IO) {
        try {
            val token = cleanToken(rawToken)
            val request = Request.Builder()
                .url("https://discord.com/api/v10/users/@me/guilds")
                .header("Authorization", "Bot $token")
                .header("User-Agent", "DiscordArchitect/1.0")
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    JSONObject(body).optString("message", "Status ${response.code}")
                } catch (e: Exception) {
                    body
                }
                return@withContext Result.failure(Exception("Gagal mengambil server: $errorMsg"))
            }

            val type = Types.newParameterizedType(List::class.java, DiscordGuild::class.java)
            val adapter = moshi.adapter<List<DiscordGuild>>(type)
            val guilds = adapter.fromJson(body) ?: emptyList()

            Result.success(guilds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createChannel(
        rawToken: String,
        guildId: String,
        name: String,
        type: Int,
        parentId: String? = null,
        topic: String? = null,
        userLimit: Int? = null,
        bitrate: Int? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val token = cleanToken(rawToken)
            val json = JSONObject().apply {
                put("name", name)
                put("type", type)
                if (!parentId.isNullOrEmpty()) {
                    put("parent_id", parentId)
                }
                if (!topic.isNullOrEmpty() && type == 0) {
                    put("topic", topic)
                }
                if (type == 2) {
                    if (userLimit != null && userLimit > 0) {
                        put("user_limit", userLimit)
                    }
                    if (bitrate != null && bitrate > 0) {
                        put("bitrate", bitrate)
                    }
                }
            }

            val request = Request.Builder()
                .url("https://discord.com/api/v10/guilds/$guildId/channels")
                .header("Authorization", "Bot $token")
                .header("Content-Type", "application/json")
                .header("User-Agent", "DiscordArchitect/1.0")
                .post(json.toString().toRequestBody(jsonMediaType))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val obj = JSONObject(body)
                    obj.optString("message", "HTTP ${response.code}")
                } catch (e: Exception) {
                    body
                }
                return@withContext Result.failure(Exception("Gagal membuat channel '$name': $errorMsg (Cek permission 'Manage Channels' bot di server)"))
            }

            val channelId = JSONObject(body).optString("id", "")
            Result.success(channelId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendWebhookMessage(
        webhookUrl: String,
        payloadJson: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (webhookUrl.isBlank() || !webhookUrl.startsWith("https://discord.com/api/webhooks/")) {
                return@withContext Result.failure(Exception("URL Webhook tidak valid. Harus diawali dengan https://discord.com/api/webhooks/..."))
            }

            val request = Request.Builder()
                .url(webhookUrl.trim())
                .header("Content-Type", "application/json")
                .header("User-Agent", "DiscordArchitect/1.0")
                .post(payloadJson.toRequestBody(jsonMediaType))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    JSONObject(body).optString("message", "Status ${response.code}")
                } catch (e: Exception) {
                    body
                }
                return@withContext Result.failure(Exception("Gagal mengirim webhook: $errorMsg"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun cleanToken(token: String): String {
        var t = token.trim()
        if (t.startsWith("Bot ", ignoreCase = true)) {
            t = t.substring(4).trim()
        }
        return t
    }
}
