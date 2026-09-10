package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiscordUser(
    val id: String = "",
    val username: String = "",
    val discriminator: String = "0",
    @Json(name = "global_name") val globalName: String? = null,
    val avatar: String? = null,
    val bot: Boolean = false
) {
    val displayName: String
        get() = globalName ?: username

    val avatarUrl: String
        get() = if (!avatar.isNullOrEmpty()) {
            "https://cdn.discordapp.com/avatars/$id/$avatar.png"
        } else {
            "https://cdn.discordapp.com/embed/avatars/0.png"
        }
}

@JsonClass(generateAdapter = true)
data class DiscordGuild(
    val id: String = "",
    val name: String = "",
    val icon: String? = null,
    val owner: Boolean = false,
    val permissions: String? = null
) {
    val iconUrl: String?
        get() = if (!icon.isNullOrEmpty()) {
            "https://cdn.discordapp.com/icons/$id/$icon.png"
        } else null
}

enum class DiscordChannelType(val rawValue: Int, val displayName: String, val icon: String) {
    GUILD_TEXT(0, "Text Channel", "#"),
    GUILD_VOICE(2, "Voice Channel", "🔊"),
    GUILD_CATEGORY(4, "Category", "📁"),
    GUILD_ANNOUNCEMENT(5, "Announcement", "📢"),
    GUILD_STAGE_VOICE(13, "Stage Voice", "🎙️")
}

data class ServerChannelDraft(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val type: DiscordChannelType = DiscordChannelType.GUILD_TEXT,
    val topic: String = "",
    val userLimit: Int = 0,
    val bitrate: Int = 64000,
    val categoryName: String = "CHANNELS"
)

data class ServerCategoryDraft(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val channels: List<ServerChannelDraft> = emptyList()
)

data class ServerTemplate(
    val id: String,
    val title: String,
    val description: String,
    val tag: String,
    val icon: String,
    val categories: List<ServerCategoryDraft>
)

// Webhook & Embed Models
@JsonClass(generateAdapter = true)
data class WebhookEmbedPayload(
    val content: String? = null,
    val username: String? = null,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    val tts: Boolean = false,
    val embeds: List<DiscordEmbedPayload> = emptyList(),
    val components: List<DiscordActionRowPayload> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DiscordEmbedPayload(
    val title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val color: Int? = null,
    val author: EmbedAuthorPayload? = null,
    val footer: EmbedFooterPayload? = null,
    val thumbnail: EmbedMediaPayload? = null,
    val image: EmbedMediaPayload? = null,
    val fields: List<EmbedFieldPayload>? = null,
    val timestamp: String? = null
)

@JsonClass(generateAdapter = true)
data class EmbedAuthorPayload(
    val name: String,
    val url: String? = null,
    @Json(name = "icon_url") val iconUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class EmbedFooterPayload(
    val text: String,
    @Json(name = "icon_url") val iconUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class EmbedMediaPayload(
    val url: String
)

@JsonClass(generateAdapter = true)
data class EmbedFieldPayload(
    val name: String,
    val value: String,
    val inline: Boolean = false
)

// Discord Component v2 (Action Rows, Buttons, Select Menus)
@JsonClass(generateAdapter = true)
data class DiscordActionRowPayload(
    val type: Int = 1, // 1 = Action Row
    val components: List<DiscordComponentItemPayload> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DiscordComponentItemPayload(
    val type: Int, // 2 = Button, 3 = String Select Menu
    val style: Int? = null, // 1=Primary, 2=Secondary, 3=Success, 4=Danger, 5=Link
    val label: String? = null,
    val emoji: DiscordEmojiPayload? = null,
    @Json(name = "custom_id") val customId: String? = null,
    val url: String? = null,
    val disabled: Boolean = false,
    val placeholder: String? = null,
    val options: List<DiscordSelectOptionPayload>? = null
)

@JsonClass(generateAdapter = true)
data class DiscordEmojiPayload(
    val name: String,
    val id: String? = null
)

@JsonClass(generateAdapter = true)
data class DiscordSelectOptionPayload(
    val label: String,
    val value: String,
    val description: String? = null,
    val emoji: DiscordEmojiPayload? = null,
    val default: Boolean = false
)

enum class DiscordButtonStyle(val styleId: Int, val title: String, val hexColor: Long) {
    PRIMARY(1, "Primary (Blurple)", 0xFF5865F2),
    SECONDARY(2, "Secondary (Grey)", 0xFF4E5058),
    SUCCESS(3, "Success (Green)", 0xFF57F287),
    DANGER(4, "Danger (Red)", 0xFFED4245),
    LINK(5, "Link (URL)", 0xFF4E5058)
}

data class BotPermission(
    val name: String,
    val description: String,
    val value: Long,
    val category: String,
    val enabled: Boolean = false
)
