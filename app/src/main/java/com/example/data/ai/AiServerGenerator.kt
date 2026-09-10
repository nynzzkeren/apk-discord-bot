package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.DiscordChannelType
import com.example.data.model.ServerCategoryDraft
import com.example.data.model.ServerChannelDraft
import com.example.data.model.ServerTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiServerGenerator {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val presetTemplates: List<ServerTemplate> = listOf(
        ServerTemplate(
            id = "gaming",
            title = "Gaming & Esports Lounge",
            description = "Lengkap dengan ruang Squad Duo/Trio/Full Team, klip gameplay, dan trigger Auto-Voice.",
            tag = "Gaming",
            icon = "🎮",
            categories = listOf(
                ServerCategoryDraft(
                    name = "📌 INFORMATION",
                    channels = listOf(
                        ServerChannelDraft(name = "📢・announcements", topic = "Pengumuman dan event gaming server"),
                        ServerChannelDraft(name = "📜・rules", topic = "Peraturan komunitas server"),
                        ServerChannelDraft(name = "🎁・giveaways", topic = "Info giveaway game & skin")
                    )
                ),
                ServerCategoryDraft(
                    name = "💬 TEXT CHANNELS",
                    channels = listOf(
                        ServerChannelDraft(name = "💬・general-chat", topic = "Obrolan santai all gamers"),
                        ServerChannelDraft(name = "🎮・game-clips", topic = "Bagi klip epic & highlight"),
                        ServerChannelDraft(name = "🤖・bot-commands", topic = "Ketik perintah bot musik dan game di sini")
                    )
                ),
                ServerCategoryDraft(
                    name = "🔊 SQUAD VOICE ROOMS",
                    channels = listOf(
                        ServerChannelDraft(name = "➕ Join to Create", type = DiscordChannelType.GUILD_VOICE, userLimit = 0),
                        ServerChannelDraft(name = "🔊 Duo Queue [2]", type = DiscordChannelType.GUILD_VOICE, userLimit = 2, bitrate = 96000),
                        ServerChannelDraft(name = "🔊 Trio Squad [3]", type = DiscordChannelType.GUILD_VOICE, userLimit = 3, bitrate = 96000),
                        ServerChannelDraft(name = "🏆 Ranked Team [5]", type = DiscordChannelType.GUILD_VOICE, userLimit = 5, bitrate = 128000),
                        ServerChannelDraft(name = "🔴 Live Streaming", type = DiscordChannelType.GUILD_VOICE, userLimit = 0, bitrate = 128000)
                    )
                )
            )
        ),
        ServerTemplate(
            id = "anime",
            title = "Anime & Manga Cafe",
            description = "Nuansa estetik Jepang dengan channel seasonal anime, fanart, dan voice lofi santai.",
            tag = "Anime",
            icon = "🌸",
            categories = listOf(
                ServerCategoryDraft(
                    name = "🌸 WELCOME CAFE",
                    channels = listOf(
                        ServerChannelDraft(name = "🍙・welcome", topic = "Selamat datang di Anime Sanctuary!"),
                        ServerChannelDraft(name = "📜・server-rules", topic = "Aturan obrolan & spoiler policy"),
                        ServerChannelDraft(name = "🎭・self-roles", topic = "Ambil role anime favoritmu")
                    )
                ),
                ServerCategoryDraft(
                    name = "🍵 ANIME & MANGA",
                    channels = listOf(
                        ServerChannelDraft(name = "📺・seasonal-anime", topic = "Diskusi anime musim ini"),
                        ServerChannelDraft(name = "📖・manga-spoilers", topic = "Diskusi manga & light novel"),
                        ServerChannelDraft(name = "🎨・fanart-cosplay", topic = "Pamer fanart dan cosplay kalian")
                    )
                ),
                ServerCategoryDraft(
                    name = "🌸 LOFI VOICE CHANNELS",
                    channels = listOf(
                        ServerChannelDraft(name = "➕ Auto Voice Room", type = DiscordChannelType.GUILD_VOICE, userLimit = 0),
                        ServerChannelDraft(name = "「 🌸・sakura-lounge 」", type = DiscordChannelType.GUILD_VOICE, userLimit = 10),
                        ServerChannelDraft(name = "「 🍵・tea-time-chill 」", type = DiscordChannelType.GUILD_VOICE, userLimit = 5),
                        ServerChannelDraft(name = "「 🎧・lofi-listening 」", type = DiscordChannelType.GUILD_VOICE, userLimit = 0, bitrate = 128000),
                        ServerChannelDraft(name = "「 🎬・watch-party 」", type = DiscordChannelType.GUILD_VOICE, userLimit = 15, bitrate = 128000)
                    )
                )
            )
        ),
        ServerTemplate(
            id = "community",
            title = "Community & Hangout Hub",
            description = "Desain rapi untuk komunitas publik, obrolan santai, event malam mingguan, dan stage.",
            tag = "Community",
            icon = "🏛️",
            categories = listOf(
                ServerCategoryDraft(
                    name = "🏛️ COMMUNITY GATE",
                    channels = listOf(
                        ServerChannelDraft(name = "👋・welcome-gate", topic = "Halo member baru!"),
                        ServerChannelDraft(name = "📢・announcements", topic = "Kabar terbaru komunitas"),
                        ServerChannelDraft(name = "💡・suggestions", topic = "Kritik & saran server")
                    )
                ),
                ServerCategoryDraft(
                    name = "💬 SOCIAL LOUNGE",
                    channels = listOf(
                        ServerChannelDraft(name = "☕・main-chat", topic = "Nongkrong santai bebas topik"),
                        ServerChannelDraft(name = "📸・media-share", topic = "Foto makanan, foto liburan, foto hewan"),
                        ServerChannelDraft(name = "😂・memes", topic = "Dilarang baper, khusus meme lucu")
                    )
                ),
                ServerCategoryDraft(
                    name = "🎙️ VOICE & STAGE",
                    channels = listOf(
                        ServerChannelDraft(name = "➕ Click to Create VC", type = DiscordChannelType.GUILD_VOICE, userLimit = 0),
                        ServerChannelDraft(name = "☕ Central Lounge", type = DiscordChannelType.GUILD_VOICE, userLimit = 0),
                        ServerChannelDraft(name = "🎲 Board Games Night", type = DiscordChannelType.GUILD_VOICE, userLimit = 8),
                        ServerChannelDraft(name = "🎙️ Community Podcast", type = DiscordChannelType.GUILD_STAGE_VOICE, userLimit = 0)
                    )
                )
            )
        ),
        ServerTemplate(
            id = "developer",
            title = "Tech & Developer Community",
            description = "Dikhususkan untuk programmer, code review, showcase proyek, dan voice pair programming.",
            tag = "Tech",
            icon = "💻",
            categories = listOf(
                ServerCategoryDraft(
                    name = "⚡ DEV PORTAL",
                    channels = listOf(
                        ServerChannelDraft(name = "📢・releases", topic = "Changelog & tool updates"),
                        ServerChannelDraft(name = "📚・resources", topic = "Buku, kursus, & cheat-sheet")
                    )
                ),
                ServerCategoryDraft(
                    name = "💻 CODE & ARCHITECTURE",
                    channels = listOf(
                        ServerChannelDraft(name = "💻・general-dev", topic = "Diskusi algoritma & coding umum"),
                        ServerChannelDraft(name = "🤖・ai-ml-prompts", topic = "Model AI, LLM, dan API integrations"),
                        ServerChannelDraft(name = "🚀・project-showcase", topic = "Pamerkan hasil karya dan app kamu!"),
                        ServerChannelDraft(name = "🐛・debug-help", topic = "Saling bantu error & bug")
                    )
                ),
                ServerCategoryDraft(
                    name = "🎧 DEV AUDIO & PAIRING",
                    channels = listOf(
                        ServerChannelDraft(name = "➕ Auto Pair Voice", type = DiscordChannelType.GUILD_VOICE, userLimit = 0),
                        ServerChannelDraft(name = "🎧 Focus & Code (Muted)", type = DiscordChannelType.GUILD_VOICE, userLimit = 0),
                        ServerChannelDraft(name = "💻 Pair Programming [2]", type = DiscordChannelType.GUILD_VOICE, userLimit = 2, bitrate = 96000),
                        ServerChannelDraft(name = "👥 Team Standup [10]", type = DiscordChannelType.GUILD_VOICE, userLimit = 10, bitrate = 128000)
                    )
                )
            )
        ),
        ServerTemplate(
            id = "study",
            title = "Study & Pomodoro Space",
            description = "Fokus belajar bersama, ruang Pomodoro 25 menit, sharing materi, dan silent voice.",
            tag = "Study",
            icon = "📚",
            categories = listOf(
                ServerCategoryDraft(
                    name = "📖 STUDY HALL",
                    channels = listOf(
                        ServerChannelDraft(name = "🎯・daily-goals", topic = "Tulis target belajarmu hari ini"),
                        ServerChannelDraft(name = "📚・study-materials", topic = "Bagi catatan dan rangkuman"),
                        ServerChannelDraft(name = "💡・study-tips", topic = "Metode belajar efektif & motivasi")
                    )
                ),
                ServerCategoryDraft(
                    name = "🔇 FOCUS VOICE ROOMS",
                    channels = listOf(
                        ServerChannelDraft(name = "➕ Create Study Room", type = DiscordChannelType.GUILD_VOICE, userLimit = 0),
                        ServerChannelDraft(name = "⏳ Pomodoro 25/5", type = DiscordChannelType.GUILD_VOICE, userLimit = 10),
                        ServerChannelDraft(name = "🔇 Silent Study (Cam On)", type = DiscordChannelType.GUILD_VOICE, userLimit = 20),
                        ServerChannelDraft(name = "🔇 Silent Study (No Cam)", type = DiscordChannelType.GUILD_VOICE, userLimit = 20),
                        ServerChannelDraft(name = "👥 Study Group Discussion", type = DiscordChannelType.GUILD_VOICE, userLimit = 6)
                    )
                )
            )
        )
    )

    suspend fun generateServerDesign(prompt: String): ServerTemplate = withContext(Dispatchers.IO) {
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isBlank()) {
            return@withContext presetTemplates.first()
        }

        // Try Gemini API if key is available
        val geminiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (!geminiKey.isNullOrBlank() && geminiKey != "MY_GEMINI_API_KEY") {
            try {
                val aiTemplate = callGeminiForServerDesign(cleanPrompt, geminiKey)
                if (aiTemplate != null) return@withContext aiTemplate
            } catch (_: Exception) {}
        }

        // Fallback to intelligent dynamic semantic generator
        generateDynamicThemedTemplate(cleanPrompt)
    }

    private suspend fun callGeminiForServerDesign(prompt: String, apiKey: String): ServerTemplate? {
        val systemInstruction = """
            You are an expert Discord Server Architect and Discord Bot Designer.
            The user wants a Discord server structure based on their prompt: "$prompt".
            Generate a JSON with:
            {
              "title": "Server Title",
              "description": "Short description",
              "icon": "an emoji",
              "tag": "Theme Tag",
              "categories": [
                {
                  "name": "CATEGORY NAME IN CAPS WITH EMOJI",
                  "channels": [
                    {
                      "name": "channel-name",
                      "type": 0 for text or 2 for voice,
                      "topic": "topic for text channel",
                      "userLimit": 0 or 2 or 5 for voice channel,
                      "bitrate": 64000 or 96000 or 128000
                    }
                  ]
                }
              ]
            }
            Return ONLY valid JSON.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7)
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
            .header("Content-Type", "application/json")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        val resBody = response.body?.string() ?: return null
        val resJson = JSONObject(resBody)
        val text = resJson.getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")

        val parsed = JSONObject(text)
        val title = parsed.optString("title", "AI Discord Server")
        val desc = parsed.optString("description", "Dibuat oleh AI Server Architect")
        val icon = parsed.optString("icon", "✨")
        val tag = parsed.optString("tag", "Custom AI")

        val catArray = parsed.getJSONArray("categories")
        val categories = mutableListOf<ServerCategoryDraft>()

        for (i in 0 until catArray.length()) {
            val cObj = catArray.getJSONObject(i)
            val cName = cObj.getString("name")
            val chArray = cObj.getJSONArray("channels")
            val channels = mutableListOf<ServerChannelDraft>()

            for (j in 0 until chArray.length()) {
                val chObj = chArray.getJSONObject(j)
                val chName = chObj.getString("name")
                val typeRaw = chObj.optInt("type", 0)
                val type = if (typeRaw == 2) DiscordChannelType.GUILD_VOICE else DiscordChannelType.GUILD_TEXT
                val topic = chObj.optString("topic", "")
                val userLimit = chObj.optInt("userLimit", 0)
                val bitrate = chObj.optInt("bitrate", 64000)

                channels.add(
                    ServerChannelDraft(
                        name = chName,
                        type = type,
                        topic = topic,
                        userLimit = userLimit,
                        bitrate = bitrate,
                        categoryName = cName
                    )
                )
            }
            categories.add(ServerCategoryDraft(name = cName, channels = channels))
        }

        return ServerTemplate(
            id = "ai_" + System.currentTimeMillis(),
            title = title,
            description = desc,
            tag = tag,
            icon = icon,
            categories = categories
        )
    }

    private fun generateDynamicThemedTemplate(prompt: String): ServerTemplate {
        val lower = prompt.lowercase()
        val (emoji, tag, title) = when {
            lower.contains("game") || lower.contains("esport") || lower.contains("clan") || lower.contains("scrim") || lower.contains("valorant") || lower.contains("ml") || lower.contains("mobile legend") ->
                Triple("🎮", "Esports & Clan", "Custom Gaming Clan Server: $prompt")
            lower.contains("anime") || lower.contains("wibu") || lower.contains("manga") || lower.contains("genshin") || lower.contains("honkai") ->
                Triple("🌸", "Anime Guild", "Anime Sanctuary: $prompt")
            lower.contains("coding") || lower.contains("dev") || lower.contains("program") || lower.contains("tech") || lower.contains("ai") || lower.contains("bot") ->
                Triple("⚡", "Dev Lab", "Tech & Developer Hub: $prompt")
            lower.contains("musik") || lower.contains("music") || lower.contains("beat") || lower.contains("band") ->
                Triple("🎵", "Music Studio", "Music & Sound Community: $prompt")
            lower.contains("roleplay") || lower.contains("rp") || lower.contains("cyberpunk") || lower.contains("gta") ->
                Triple("🎭", "Roleplay Realm", "Custom RP Server: $prompt")
            lower.contains("belajar") || lower.contains("study") || lower.contains("sekolah") || lower.contains("kuliah") ->
                Triple("📚", "Study Hub", "Academic & Study Space: $prompt")
            else ->
                Triple("✨", "Special AI", "Server Komunitas: $prompt")
        }

        val slug = prompt.replace(Regex("[^a-zA-Z0-9]+"), "-").lowercase().take(15)

        val categories = listOf(
            ServerCategoryDraft(
                name = "$emoji・INFO & RULES",
                channels = listOf(
                    ServerChannelDraft(name = "📢・announcements", topic = "Pengumuman utama server $prompt"),
                    ServerChannelDraft(name = "📜・server-rules", topic = "Peraturan dan tata tertib"),
                    ServerChannelDraft(name = "👋・welcome-hub", topic = "Selamat datang member baru!")
                )
            ),
            ServerCategoryDraft(
                name = "💬・$tag DISCUSSIONS",
                channels = listOf(
                    ServerChannelDraft(name = "💬・general-$slug", topic = "Obrolan santai topik $prompt"),
                    ServerChannelDraft(name = "💡・ideas-showcase", topic = "Karya, ide, dan diskusi hangat"),
                    ServerChannelDraft(name = "🤖・bot-lounge", topic = "Perintah bot musik dan AI assistance")
                )
            ),
            ServerCategoryDraft(
                name = "🔊・VOICE SUITE",
                channels = listOf(
                    ServerChannelDraft(name = "➕ Join to Create VC", type = DiscordChannelType.GUILD_VOICE, userLimit = 0),
                    ServerChannelDraft(name = "🔊 Duo Room [2]", type = DiscordChannelType.GUILD_VOICE, userLimit = 2, bitrate = 96000),
                    ServerChannelDraft(name = "🔊 Squad Room [4]", type = DiscordChannelType.GUILD_VOICE, userLimit = 4, bitrate = 96000),
                    ServerChannelDraft(name = "☕ Chill & Talk", type = DiscordChannelType.GUILD_VOICE, userLimit = 0, bitrate = 128000),
                    ServerChannelDraft(name = "🔴 Live Stream Room", type = DiscordChannelType.GUILD_VOICE, userLimit = 15, bitrate = 128000)
                )
            )
        )

        return ServerTemplate(
            id = "ai_" + System.currentTimeMillis(),
            title = title,
            description = "Dirancang otomatis sesuai konsep: \"$prompt\"",
            tag = tag,
            icon = emoji,
            categories = categories
        )
    }
}
