package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiServerGenerator
import com.example.data.api.DiscordApiClient
import com.example.data.model.BotPermission
import com.example.data.model.DiscordActionRowPayload
import com.example.data.model.DiscordButtonStyle
import com.example.data.model.DiscordChannelType
import com.example.data.model.DiscordComponentItemPayload
import com.example.data.model.DiscordEmbedPayload
import com.example.data.model.DiscordEmojiPayload
import com.example.data.model.DiscordGuild
import com.example.data.model.DiscordSelectOptionPayload
import com.example.data.model.DiscordUser
import com.example.data.model.EmbedAuthorPayload
import com.example.data.model.EmbedFieldPayload
import com.example.data.model.EmbedFooterPayload
import com.example.data.model.EmbedMediaPayload
import com.example.data.model.ServerCategoryDraft
import com.example.data.model.ServerChannelDraft
import com.example.data.model.ServerTemplate
import com.example.data.model.WebhookEmbedPayload
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class DiscordUiState(
    // Active Tab: 0 = Bot/Invite, 1 = Server Architect, 2 = Voice Studio, 3 = Webhook Embed
    val currentTab: Int = 0,

    // Bot & Invite
    val clientId: String = "",
    val botToken: String = "",
    val permissions: List<BotPermission> = defaultPermissions,
    val isConnectingBot: Boolean = false,
    val botUser: DiscordUser? = null,
    val botErrorMessage: String? = null,
    val guilds: List<DiscordGuild> = emptyList(),
    val selectedGuild: DiscordGuild? = null,
    val manualGuildId: String = "",

    // Server Architect
    val templates: List<ServerTemplate> = emptyList(),
    val currentTemplate: ServerTemplate? = null,
    val aiPrompt: String = "",
    val isGeneratingAi: Boolean = false,
    val aiErrorMessage: String? = null,
    val isDeployingServer: Boolean = false,
    val deployProgress: Float = 0f,
    val deployLogs: List<String> = emptyList(),
    val showDeployDialog: Boolean = false,

    // Voice Studio
    val voiceChannelName: String = "🔊・Squad Room [4]",
    val voiceUserLimit: Int = 4,
    val voiceBitrateKbps: Int = 96,
    val voiceTargetCategory: String = "VOICE CHANNELS",
    val isCreatingVoice: Boolean = false,
    val voiceMessage: String? = null,
    val autoVoiceInstalled: Boolean = false,

    // Webhook & Embed
    val webhookUrl: String = "",
    val webhookBotName: String = "Discord Architect Bot",
    val webhookAvatarUrl: String = "https://cdn.discordapp.com/embed/avatars/0.png",
    val messageContent: String = "Halo! Ini pesan embed kustom dari Discord Architect APK 🚀",
    val embedTitle: String = "🎮 Pengumuman Turnamen Server",
    val embedTitleUrl: String = "",
    val embedDescription: String = "Selamat datang para member! Turnamen mingguan akan segera dimulai pada pukul 20:00 WIB. Pastikan join voice channel squad sebelum match.",
    val embedColorHex: String = "5865F2",
    val embedAuthorName: String = "Server Administrator",
    val embedAuthorIcon: String = "",
    val embedThumbnailUrl: String = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=200",
    val embedImageUrl: String = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800",
    val embedFooterText: String = "Discord Studio • Automated Dispatcher",
    val embedFooterIcon: String = "",
    val embedTimestampEnabled: Boolean = true,
    val embedFields: List<EmbedFieldPayload> = defaultEmbedFields,
    val components: List<DiscordComponentItemPayload> = defaultComponents,
    val isSendingWebhook: Boolean = false,
    val webhookStatusMessage: String? = null,
    val showPayloadDialog: Boolean = false,
    val rawPayloadJson: String = ""
)

private val defaultPermissions = listOf(
    BotPermission("Administrator", "Hak akses penuh ke seluruh server", 8L, "Admin", true),
    BotPermission("Manage Channels", "Bisa membuat, edit, dan hapus channel text/voice", 16L, "General", true),
    BotPermission("Manage Server", "Bisa mengelola konfigurasi server", 32L, "General", true),
    BotPermission("Manage Roles", "Bisa membuat dan mengatur peran pengguna", 268435456L, "General", false),
    BotPermission("Send Messages", "Bisa mengirim chat ke channel", 2048L, "Text", true),
    BotPermission("Embed Links", "Bisa mengirim embed berwarna & interaktif", 16384L, "Text", true),
    BotPermission("Attach Files", "Bisa upload gambar dan file", 32768L, "Text", true),
    BotPermission("Connect", "Bisa masuk ke Voice Channel", 1048576L, "Voice", true),
    BotPermission("Speak", "Bisa berbicara dalam Voice Channel", 2097152L, "Voice", true),
    BotPermission("Move Members", "Bisa memindahkan user antar voice room (Auto-Voice)", 16777216L, "Voice", true),
    BotPermission("Use Slash Commands", "Bisa menggunakan perintah aplikasi", 2147483648L, "General", true)
)

private val defaultEmbedFields = listOf(
    EmbedFieldPayload("📅 Jadwal", "Sabtu, 20:00 WIB", inline = true),
    EmbedFieldPayload("🏆 Hadiah", "Rp 500.000 + Nitro", inline = true),
    EmbedFieldPayload("🔊 Lokasi Voice", "Voice: Squad Ranked", inline = false)
)

private val defaultComponents = listOf(
    DiscordComponentItemPayload(
        type = 2,
        style = DiscordButtonStyle.PRIMARY.styleId,
        label = "Daftar Sekarang",
        emoji = DiscordEmojiPayload(name = "🎮"),
        customId = "btn_register_tournament"
    ),
    DiscordComponentItemPayload(
        type = 2,
        style = DiscordButtonStyle.SUCCESS.styleId,
        label = "Join Voice Room",
        emoji = DiscordEmojiPayload(name = "🔊"),
        customId = "btn_join_vc"
    ),
    DiscordComponentItemPayload(
        type = 2,
        style = DiscordButtonStyle.LINK.styleId,
        label = "Website Info",
        emoji = DiscordEmojiPayload(name = "🔗"),
        url = "https://discord.com"
    )
)

class DiscordViewModel : ViewModel() {

    private val apiClient = DiscordApiClient()
    private val aiGenerator = AiServerGenerator()

    private val _uiState = MutableStateFlow(
        DiscordUiState(
            templates = aiGenerator.presetTemplates,
            currentTemplate = aiGenerator.presetTemplates.first()
        )
    )
    val uiState: StateFlow<DiscordUiState> = _uiState.asStateFlow()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    fun setTab(tabIndex: Int) {
        _uiState.update { it.copy(currentTab = tabIndex) }
    }

    // --- Bot & Invite Section ---

    fun updateClientId(clientId: String) {
        _uiState.update { it.copy(clientId = clientId.trim()) }
    }

    fun updateBotToken(token: String) {
        _uiState.update { it.copy(botToken = token.trim()) }
    }

    fun togglePermission(permissionName: String) {
        _uiState.update { state ->
            val updated = state.permissions.map {
                if (it.name == permissionName) it.copy(enabled = !it.enabled) else it
            }
            state.copy(permissions = updated)
        }
    }

    fun calculatePermissionsValue(): Long {
        return _uiState.value.permissions
            .filter { it.enabled }
            .fold(0L) { acc, p -> acc or p.value }
    }

    fun getInviteUrl(): String {
        val cid = _uiState.value.clientId.ifBlank { "123456789012345678" }
        val perms = calculatePermissionsValue()
        return "https://discord.com/oauth2/authorize?client_id=$cid&permissions=$perms&scope=bot%20applications.commands"
    }

    fun connectBot() {
        val token = _uiState.value.botToken
        if (token.isBlank()) {
            _uiState.update { it.copy(botErrorMessage = "Harap masukkan Bot Token terlebih dahulu!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isConnectingBot = true, botErrorMessage = null) }

            val botResult = apiClient.validateBotToken(token)
            if (botResult.isSuccess) {
                val user = botResult.getOrThrow()
                // Fetch guilds
                val guildResult = apiClient.fetchBotGuilds(token)
                val guilds = guildResult.getOrDefault(emptyList())

                _uiState.update {
                    it.copy(
                        isConnectingBot = false,
                        botUser = user,
                        guilds = guilds,
                        selectedGuild = guilds.firstOrNull(),
                        botErrorMessage = null,
                        clientId = if (it.clientId.isBlank()) user.id else it.clientId
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isConnectingBot = false,
                        botErrorMessage = botResult.exceptionOrNull()?.message ?: "Gagal terhubung ke Discord Bot"
                    )
                }
            }
        }
    }

    fun selectGuild(guild: DiscordGuild) {
        _uiState.update { it.copy(selectedGuild = guild, manualGuildId = guild.id) }
    }

    fun updateManualGuildId(guildId: String) {
        _uiState.update { it.copy(manualGuildId = guildId.trim()) }
    }

    // --- Server Architect Section ---

    fun selectTemplate(template: ServerTemplate) {
        _uiState.update { it.copy(currentTemplate = template) }
    }

    fun updateAiPrompt(prompt: String) {
        _uiState.update { it.copy(aiPrompt = prompt) }
    }

    fun generateWithAi() {
        val prompt = _uiState.value.aiPrompt
        if (prompt.isBlank()) {
            _uiState.update { it.copy(aiErrorMessage = "Ketik deskripsi atau tema server yang diinginkan!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingAi = true, aiErrorMessage = null) }
            try {
                val generated = aiGenerator.generateServerDesign(prompt)
                _uiState.update {
                    val updatedTemplates = listOf(generated) + it.templates.filter { t -> t.id != generated.id }
                    it.copy(
                        isGeneratingAi = false,
                        templates = updatedTemplates,
                        currentTemplate = generated,
                        aiErrorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGeneratingAi = false,
                        aiErrorMessage = "Gagal membuat desain AI: ${e.message}"
                    )
                }
            }
        }
    }

    fun deployServerStructure() {
        val template = _uiState.value.currentTemplate ?: return
        val token = _uiState.value.botToken
        val guildId = _uiState.value.selectedGuild?.id ?: _uiState.value.manualGuildId

        val logs = mutableListOf<String>()
        logs.add("🚀 Memulai deploy arsitektur: ${template.title}")

        if (token.isBlank() || guildId.isBlank()) {
            // Run simulation mode with rich interactive feedback
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        isDeployingServer = true,
                        showDeployDialog = true,
                        deployProgress = 0.1f,
                        deployLogs = listOf("⚠️ Catatan: Mode Simulasi Aktif (Hubungkan Bot Token & Guild ID untuk eksekusi live ke Discord)", "▶️ Merancang kategori...")
                    )
                }

                val totalItems = template.categories.size + template.categories.sumOf { it.channels.size }
                var finished = 0

                for (cat in template.categories) {
                    kotlinx.coroutines.delay(400)
                    logs.add("📁 Kategori dibuat: ${cat.name}")
                    finished++
                    _uiState.update {
                        it.copy(deployProgress = finished.toFloat() / totalItems, deployLogs = logs.toList())
                    }

                    for (ch in cat.channels) {
                        kotlinx.coroutines.delay(250)
                        val icon = if (ch.type == DiscordChannelType.GUILD_VOICE) "🔊" else "#"
                        logs.add("  └ $icon Channel: ${ch.name} (Topic: ${ch.topic.ifBlank { "Aktif" }})")
                        finished++
                        _uiState.update {
                            it.copy(deployProgress = finished.toFloat() / totalItems, deployLogs = logs.toList())
                        }
                    }
                }

                logs.add("🎉 Arsitektur Server '${template.title}' siap digunakan!")
                _uiState.update {
                    it.copy(
                        isDeployingServer = false,
                        deployProgress = 1.0f,
                        deployLogs = logs.toList()
                    )
                }
            }
            return
        }

        // Real Discord API Deployment
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDeployingServer = true,
                    showDeployDialog = true,
                    deployProgress = 0.05f,
                    deployLogs = listOf("🌐 Menghubungi Discord API untuk Guild: $guildId ...")
                )
            }

            val totalSteps = template.categories.size + template.categories.sumOf { it.channels.size }
            var currentStep = 0

            for (cat in template.categories) {
                // 1. Create Category
                val catResult = apiClient.createChannel(
                    rawToken = token,
                    guildId = guildId,
                    name = cat.name,
                    type = 4 // GUILD_CATEGORY
                )

                val catId = if (catResult.isSuccess) {
                    val id = catResult.getOrThrow()
                    logs.add("✅ Kategori '${cat.name}' dibuat [ID: $id]")
                    id
                } else {
                    logs.add("❌ Gagal membuat kategori '${cat.name}': ${catResult.exceptionOrNull()?.message}")
                    null
                }

                currentStep++
                _uiState.update {
                    it.copy(deployProgress = currentStep.toFloat() / totalSteps, deployLogs = logs.toList())
                }

                // 2. Create Channels inside category
                for (ch in cat.channels) {
                    val typeRaw = if (ch.type == DiscordChannelType.GUILD_VOICE) 2 else 0
                    val chResult = apiClient.createChannel(
                        rawToken = token,
                        guildId = guildId,
                        name = ch.name,
                        type = typeRaw,
                        parentId = catId,
                        topic = ch.topic,
                        userLimit = ch.userLimit,
                        bitrate = ch.bitrate
                    )

                    if (chResult.isSuccess) {
                        val chId = chResult.getOrThrow()
                        val icon = if (typeRaw == 2) "🔊" else "#"
                        logs.add("  └ ✅ $icon Channel '${ch.name}' dibuat [ID: $chId]")
                    } else {
                        logs.add("  └ ❌ Gagal channel '${ch.name}': ${chResult.exceptionOrNull()?.message}")
                    }

                    currentStep++
                    _uiState.update {
                        it.copy(deployProgress = currentStep.toFloat() / totalSteps, deployLogs = logs.toList())
                    }
                }
            }

            logs.add("✨ Selesai mendesain server di Discord!")
            _uiState.update {
                it.copy(
                    isDeployingServer = false,
                    deployProgress = 1.0f,
                    deployLogs = logs.toList()
                )
            }
        }
    }

    fun dismissDeployDialog() {
        _uiState.update { it.copy(showDeployDialog = false) }
    }

    // --- Voice Studio Section ---

    fun updateVoiceName(name: String) {
        _uiState.update { it.copy(voiceChannelName = name) }
    }

    fun updateVoiceUserLimit(limit: Int) {
        _uiState.update { it.copy(voiceUserLimit = limit) }
    }

    fun updateVoiceBitrate(bitrateKbps: Int) {
        _uiState.update { it.copy(voiceBitrateKbps = bitrateKbps) }
    }

    fun applyVoiceStyle(style: String) {
        val raw = _uiState.value.voiceChannelName
            .replace(Regex("^[「【〢🔊・ ]+"), "")
            .replace(Regex("[」】\\[\\]0-9 ]+$"), "")
            .trim()
            .ifBlank { "gaming-room" }

        val styled = when (style) {
            "japanese" -> "「 🔊・$raw 」"
            "boxed" -> "【 🔊 】$raw"
            "border" -> "〢🔊-$raw"
            "numbered" -> "🔊 $raw [${_uiState.value.voiceUserLimit}]"
            else -> "🔊・$raw"
        }
        _uiState.update { it.copy(voiceChannelName = styled) }
    }

    fun createCustomVoiceChannel() {
        val token = _uiState.value.botToken
        val guildId = _uiState.value.selectedGuild?.id ?: _uiState.value.manualGuildId
        val name = _uiState.value.voiceChannelName
        val limit = _uiState.value.voiceUserLimit
        val bitrate = _uiState.value.voiceBitrateKbps * 1000

        if (token.isBlank() || guildId.isBlank()) {
            _uiState.update {
                it.copy(
                    voiceMessage = "Mode Preview: Voice channel '$name' dengan limit $limit user & ${bitrate / 1000}kbps siap dibuat. (Sambungkan Bot Token & Guild ID untuk buat langsung di Discord)."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingVoice = true, voiceMessage = null) }
            val res = apiClient.createChannel(
                rawToken = token,
                guildId = guildId,
                name = name,
                type = 2, // GUILD_VOICE
                userLimit = limit,
                bitrate = bitrate
            )

            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        isCreatingVoice = false,
                        voiceMessage = "Berhasil membuat Voice Channel '$name' di Discord! [ID: ${res.getOrNull()}]"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isCreatingVoice = false,
                        voiceMessage = "Gagal: ${res.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun setupAutoVoiceSystem() {
        val token = _uiState.value.botToken
        val guildId = _uiState.value.selectedGuild?.id ?: _uiState.value.manualGuildId

        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingVoice = true, voiceMessage = null) }

            if (token.isNotBlank() && guildId.isNotBlank()) {
                // Create master Join to Create channel in Discord
                val res = apiClient.createChannel(
                    rawToken = token,
                    guildId = guildId,
                    name = "➕ Join to Create VC",
                    type = 2,
                    userLimit = 0
                )
                if (res.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isCreatingVoice = false,
                            autoVoiceInstalled = true,
                            voiceMessage = "Channel '➕ Join to Create VC' berhasil dibuat di server! Anggota yang masuk akan otomatis dibuatkan room voice tersendiri."
                        )
                    }
                    return@launch
                }
            }

            _uiState.update {
                it.copy(
                    isCreatingVoice = false,
                    autoVoiceInstalled = true,
                    voiceMessage = "Sistem Auto-Voice aktif! Siapkan master channel '➕ Join to Create' agar bot otomatis menduplikasi room saat member join."
                )
            }
        }
    }

    // --- Webhook & Embed & Components v2 Section ---

    fun updateWebhookUrl(url: String) {
        _uiState.update { it.copy(webhookUrl = url.trim()) }
    }

    fun updateWebhookBotName(name: String) {
        _uiState.update { it.copy(webhookBotName = name) }
    }

    fun updateWebhookAvatarUrl(url: String) {
        _uiState.update { it.copy(webhookAvatarUrl = url) }
    }

    fun updateMessageContent(content: String) {
        _uiState.update { it.copy(messageContent = content) }
    }

    fun updateEmbedTitle(title: String) {
        _uiState.update { it.copy(embedTitle = title) }
    }

    fun updateEmbedTitleUrl(url: String) {
        _uiState.update { it.copy(embedTitleUrl = url) }
    }

    fun updateEmbedDescription(desc: String) {
        _uiState.update { it.copy(embedDescription = desc) }
    }

    fun updateEmbedColor(colorHex: String) {
        _uiState.update { it.copy(embedColorHex = colorHex.replace("#", "").trim()) }
    }

    fun updateEmbedAuthor(name: String, icon: String) {
        _uiState.update { it.copy(embedAuthorName = name, embedAuthorIcon = icon) }
    }

    fun updateEmbedMedia(thumbnail: String, image: String) {
        _uiState.update { it.copy(embedThumbnailUrl = thumbnail, embedImageUrl = image) }
    }

    fun updateEmbedFooter(text: String, icon: String) {
        _uiState.update { it.copy(embedFooterText = text, embedFooterIcon = icon) }
    }

    fun toggleTimestamp() {
        _uiState.update { it.copy(embedTimestampEnabled = !it.embedTimestampEnabled) }
    }

    fun addField(name: String = "Field Baru", value: String = "Nilai field", inline: Boolean = true) {
        _uiState.update {
            it.copy(embedFields = it.embedFields + EmbedFieldPayload(name, value, inline))
        }
    }

    fun removeField(index: Int) {
        _uiState.update {
            val list = it.embedFields.toMutableList()
            if (index in list.indices) {
                list.removeAt(index)
            }
            it.copy(embedFields = list)
        }
    }

    fun updateField(index: Int, name: String, value: String, inline: Boolean) {
        _uiState.update {
            val list = it.embedFields.toMutableList()
            if (index in list.indices) {
                list[index] = EmbedFieldPayload(name, value, inline)
            }
            it.copy(embedFields = list)
        }
    }

    fun addButton(label: String, style: DiscordButtonStyle, emoji: String = "✨", customId: String = "btn_custom") {
        val newButton = DiscordComponentItemPayload(
            type = 2,
            style = style.styleId,
            label = label,
            emoji = if (emoji.isNotBlank()) DiscordEmojiPayload(name = emoji) else null,
            customId = if (style != DiscordButtonStyle.LINK) customId else null,
            url = if (style == DiscordButtonStyle.LINK) "https://discord.com" else null
        )
        _uiState.update {
            it.copy(components = it.components + newButton)
        }
    }

    fun removeComponent(index: Int) {
        _uiState.update {
            val list = it.components.toMutableList()
            if (index in list.indices) {
                list.removeAt(index)
            }
            it.copy(components = list)
        }
    }

    fun buildWebhookPayload(): WebhookEmbedPayload {
        val state = _uiState.value

        val colorInt = try {
            state.embedColorHex.toInt(16)
        } catch (e: Exception) {
            0x5865F2
        }

        val timestampStr = if (state.embedTimestampEnabled) {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.format(Date())
        } else null

        val embed = DiscordEmbedPayload(
            title = state.embedTitle.ifBlank { null },
            description = state.embedDescription.ifBlank { null },
            url = state.embedTitleUrl.ifBlank { null },
            color = colorInt,
            author = if (state.embedAuthorName.isNotBlank()) {
                EmbedAuthorPayload(name = state.embedAuthorName, iconUrl = state.embedAuthorIcon.ifBlank { null })
            } else null,
            footer = if (state.embedFooterText.isNotBlank()) {
                EmbedFooterPayload(text = state.embedFooterText, iconUrl = state.embedFooterIcon.ifBlank { null })
            } else null,
            thumbnail = if (state.embedThumbnailUrl.isNotBlank()) {
                EmbedMediaPayload(url = state.embedThumbnailUrl)
            } else null,
            image = if (state.embedImageUrl.isNotBlank()) {
                EmbedMediaPayload(url = state.embedImageUrl)
            } else null,
            fields = state.embedFields.ifEmpty { null },
            timestamp = timestampStr
        )

        val actionRows = if (state.components.isNotEmpty()) {
            listOf(DiscordActionRowPayload(type = 1, components = state.components))
        } else emptyList()

        return WebhookEmbedPayload(
            content = state.messageContent.ifBlank { null },
            username = state.webhookBotName.ifBlank { null },
            avatarUrl = state.webhookAvatarUrl.ifBlank { null },
            embeds = listOf(embed),
            components = actionRows
        )
    }

    fun generatePayloadJsonString(): String {
        val payload = buildWebhookPayload()
        val adapter = moshi.adapter(WebhookEmbedPayload::class.java).indent("  ")
        return adapter.toJson(payload)
    }

    fun showPayloadDialog() {
        val json = generatePayloadJsonString()
        _uiState.update { it.copy(showPayloadDialog = true, rawPayloadJson = json) }
    }

    fun dismissPayloadDialog() {
        _uiState.update { it.copy(showPayloadDialog = false) }
    }

    fun sendWebhook() {
        val url = _uiState.value.webhookUrl
        if (url.isBlank()) {
            _uiState.update { it.copy(webhookStatusMessage = "Harap masukkan Discord Webhook URL terlebih dahulu!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSendingWebhook = true, webhookStatusMessage = null) }
            val json = generatePayloadJsonString()
            val result = apiClient.sendWebhookMessage(url, json)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isSendingWebhook = false,
                        webhookStatusMessage = "Pesan Webhook Embed & Components v2 berhasil dikirim ke Discord! 🎉"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isSendingWebhook = false,
                        webhookStatusMessage = "Gagal kirim webhook: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }
}
