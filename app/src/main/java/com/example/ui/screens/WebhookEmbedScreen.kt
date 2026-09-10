package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DiscordButtonStyle
import com.example.ui.theme.DiscordBgElevated
import com.example.ui.theme.DiscordBgInput
import com.example.ui.theme.DiscordBgSurface
import com.example.ui.theme.DiscordBlurple
import com.example.ui.theme.DiscordBorder
import com.example.ui.theme.DiscordFuchsia
import com.example.ui.theme.DiscordGreen
import com.example.ui.theme.DiscordRed
import com.example.ui.theme.DiscordTextMuted
import com.example.ui.theme.DiscordTextWhite
import com.example.ui.theme.DiscordYellow
import com.example.viewmodel.DiscordUiState
import com.example.viewmodel.DiscordViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WebhookEmbedScreen(
    state: DiscordUiState,
    viewModel: DiscordViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddButtonDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DiscordBlurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartButton,
                        contentDescription = "Webhook & Component v2",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column {
                    Text(
                        text = "Webhook Embed & Component v2",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = DiscordTextWhite
                    )
                    Text(
                        text = "Custom embed warna-warni & tombol interaktif Discord",
                        fontSize = 12.sp,
                        color = DiscordTextMuted
                    )
                }
            }
        }

        // Section 1: Webhook URL & Identity
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("webhook_config_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordBgSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DiscordBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "1. Konfigurasi Discord Webhook",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DiscordTextWhite
                    )

                    OutlinedTextField(
                        value = state.webhookUrl,
                        onValueChange = { viewModel.updateWebhookUrl(it) },
                        label = { Text("Webhook URL") },
                        placeholder = { Text("https://discord.com/api/webhooks/...") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("webhook_url_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiscordBlurple,
                            unfocusedBorderColor = DiscordBorder,
                            focusedContainerColor = DiscordBgInput,
                            unfocusedContainerColor = DiscordBgInput,
                            focusedTextColor = DiscordTextWhite,
                            unfocusedTextColor = DiscordTextWhite
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = state.webhookBotName,
                            onValueChange = { viewModel.updateWebhookBotName(it) },
                            label = { Text("Bot Name Override") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DiscordBlurple,
                                unfocusedBorderColor = DiscordBorder,
                                focusedContainerColor = DiscordBgInput,
                                unfocusedContainerColor = DiscordBgInput,
                                focusedTextColor = DiscordTextWhite,
                                unfocusedTextColor = DiscordTextWhite
                            )
                        )
                        OutlinedTextField(
                            value = state.webhookAvatarUrl,
                            onValueChange = { viewModel.updateWebhookAvatarUrl(it) },
                            label = { Text("Avatar URL") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DiscordBlurple,
                                unfocusedBorderColor = DiscordBorder,
                                focusedContainerColor = DiscordBgInput,
                                unfocusedContainerColor = DiscordBgInput,
                                focusedTextColor = DiscordTextWhite,
                                unfocusedTextColor = DiscordTextWhite
                            )
                        )
                    }

                    OutlinedTextField(
                        value = state.messageContent,
                        onValueChange = { viewModel.updateMessageContent(it) },
                        label = { Text("Message Content (Teks di atas Embed)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiscordBlurple,
                            unfocusedBorderColor = DiscordBorder,
                            focusedContainerColor = DiscordBgInput,
                            unfocusedContainerColor = DiscordBgInput,
                            focusedTextColor = DiscordTextWhite,
                            unfocusedTextColor = DiscordTextWhite
                        )
                    )
                }
            }
        }

        // Section 2: Embed Customizer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("embed_designer_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordBgSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DiscordBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "2. Desain Custom Embed",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DiscordTextWhite
                    )

                    OutlinedTextField(
                        value = state.embedTitle,
                        onValueChange = { viewModel.updateEmbedTitle(it) },
                        label = { Text("Embed Title") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiscordBlurple,
                            unfocusedBorderColor = DiscordBorder,
                            focusedContainerColor = DiscordBgInput,
                            unfocusedContainerColor = DiscordBgInput,
                            focusedTextColor = DiscordTextWhite,
                            unfocusedTextColor = DiscordTextWhite
                        )
                    )

                    OutlinedTextField(
                        value = state.embedDescription,
                        onValueChange = { viewModel.updateEmbedDescription(it) },
                        label = { Text("Embed Description (Mendukung Markdown)") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiscordBlurple,
                            unfocusedBorderColor = DiscordBorder,
                            focusedContainerColor = DiscordBgInput,
                            unfocusedContainerColor = DiscordBgInput,
                            focusedTextColor = DiscordTextWhite,
                            unfocusedTextColor = DiscordTextWhite
                        )
                    )

                    // Color Picker Swatches
                    Text("Pilih Warna Garis Samping Embed:", fontSize = 12.sp, color = DiscordTextMuted)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "5865F2" to DiscordBlurple,
                            "57F287" to DiscordGreen,
                            "FEE75C" to DiscordYellow,
                            "EB459E" to DiscordFuchsia,
                            "ED4245" to DiscordRed,
                            "2B2D31" to DiscordBgSurface
                        ).forEach { (hex, color) ->
                            val isSelected = state.embedColorHex.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else DiscordBorder,
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.updateEmbedColor(hex) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = state.embedColorHex,
                        onValueChange = { viewModel.updateEmbedColor(it) },
                        label = { Text("Custom Color Hex") },
                        prefix = { Text("#") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiscordBlurple,
                            unfocusedBorderColor = DiscordBorder,
                            focusedContainerColor = DiscordBgInput,
                            unfocusedContainerColor = DiscordBgInput,
                            focusedTextColor = DiscordTextWhite,
                            unfocusedTextColor = DiscordTextWhite
                        )
                    )

                    // Media URLs
                    OutlinedTextField(
                        value = state.embedThumbnailUrl,
                        onValueChange = { viewModel.updateEmbedMedia(it, state.embedImageUrl) },
                        label = { Text("Thumbnail Image URL (Kecil Kanan)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiscordBlurple,
                            unfocusedBorderColor = DiscordBorder,
                            focusedContainerColor = DiscordBgInput,
                            unfocusedContainerColor = DiscordBgInput,
                            focusedTextColor = DiscordTextWhite,
                            unfocusedTextColor = DiscordTextWhite
                        )
                    )

                    OutlinedTextField(
                        value = state.embedImageUrl,
                        onValueChange = { viewModel.updateEmbedMedia(state.embedThumbnailUrl, it) },
                        label = { Text("Main Banner Image URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiscordBlurple,
                            unfocusedBorderColor = DiscordBorder,
                            focusedContainerColor = DiscordBgInput,
                            unfocusedContainerColor = DiscordBgInput,
                            focusedTextColor = DiscordTextWhite,
                            unfocusedTextColor = DiscordTextWhite
                        )
                    )

                    // Fields Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Embed Fields (${state.embedFields.size}):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DiscordTextWhite
                        )
                        OutlinedButton(
                            onClick = { viewModel.addField() },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah Field", fontSize = 12.sp)
                        }
                    }

                    state.embedFields.forEachIndexed { index, field ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DiscordBgElevated, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                OutlinedTextField(
                                    value = field.name,
                                    onValueChange = { viewModel.updateField(index, it, field.value, field.inline) },
                                    label = { Text("Field Name") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = field.value,
                                    onValueChange = { viewModel.updateField(index, field.name, it, field.inline) },
                                    label = { Text("Field Value") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            IconButton(onClick = { viewModel.removeField(index) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = DiscordRed)
                            }
                        }
                    }

                    // Timestamp Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sertakan Waktu Otomatis (Timestamp):", fontSize = 13.sp, color = DiscordTextWhite)
                        Switch(
                            checked = state.embedTimestampEnabled,
                            onCheckedChange = { viewModel.toggleTimestamp() },
                            colors = SwitchDefaults.colors(checkedThumbColor = DiscordBlurple, checkedTrackColor = DiscordBlurple.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }

        // Section 3: Discord Components v2 (Buttons)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("components_v2_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordBgSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DiscordBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "3. Discord Components v2 (Buttons)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DiscordTextWhite
                            )
                            Text(
                                text = "Tombol interaktif Action Row di bawah pesan",
                                fontSize = 12.sp,
                                color = DiscordTextMuted
                            )
                        }
                        Button(
                            onClick = { showAddButtonDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tombol Baru", fontSize = 12.sp)
                        }
                    }

                    state.components.forEachIndexed { index, comp ->
                        val styleColor = when (comp.style) {
                            DiscordButtonStyle.PRIMARY.styleId -> DiscordBlurple
                            DiscordButtonStyle.SUCCESS.styleId -> DiscordGreen
                            DiscordButtonStyle.DANGER.styleId -> DiscordRed
                            else -> DiscordBorder
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DiscordBgElevated, RoundedCornerShape(8.dp))
                                .border(1.dp, styleColor, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                comp.emoji?.name?.let {
                                    Text(text = it, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Column {
                                    Text(
                                        text = comp.label ?: "Button",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = DiscordTextWhite
                                    )
                                    Text(
                                        text = if (comp.url != null) "URL: ${comp.url}" else "ID: ${comp.customId}",
                                        fontSize = 11.sp,
                                        color = DiscordTextMuted
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.removeComponent(index) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = DiscordRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Realistic Discord Live Preview
        item {
            Text(
                text = "Live Discord Message Preview:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DiscordTextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Realistic Discord Dark Theme Container
            val embedColor = try {
                Color(android.graphics.Color.parseColor("#${state.embedColorHex}"))
            } catch (e: Exception) {
                DiscordBlurple
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF313338)) // Authentic Discord Dark Chat Background
                    .padding(14.dp)
                    .testTag("discord_live_preview")
            ) {
                // Header (Bot Avatar + Username + BOT Badge + Timestamp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AsyncImage(
                        model = state.webhookAvatarUrl.ifBlank { "https://cdn.discordapp.com/embed/avatars/0.png" },
                        contentDescription = "Bot Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DiscordBorder)
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.webhookBotName.ifBlank { "Discord Architect" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = DiscordBlurple,
                                shape = RoundedCornerShape(3.dp)
                            ) {
                                Text(
                                    text = "BOT",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hari ini pukul 12:00",
                                fontSize = 11.sp,
                                color = Color(0xFF949BA4)
                            )
                        }
                        if (state.messageContent.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = state.messageContent,
                                fontSize = 13.sp,
                                color = Color(0xFFDBDEE1)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // The Embed Box
                Row(
                    modifier = Modifier
                        .padding(start = 48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF2B2D31)) // Discord Embed Body Color
                        .fillMaxWidth()
                ) {
                    // Left Colored Border
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .background(embedColor)
                    )

                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Author
                        if (state.embedAuthorName.isNotBlank()) {
                            Text(
                                text = state.embedAuthorName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Title
                        if (state.embedTitle.isNotBlank()) {
                            Text(
                                text = state.embedTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Description
                        if (state.embedDescription.isNotBlank()) {
                            Text(
                                text = state.embedDescription,
                                fontSize = 13.sp,
                                color = Color(0xFFDBDEE1),
                                lineHeight = 18.sp
                            )
                        }

                        // Fields Grid
                        if (state.embedFields.isNotEmpty()) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                state.embedFields.forEach { field ->
                                    Column(modifier = if (field.inline) Modifier.width(130.dp) else Modifier.fillMaxWidth()) {
                                        Text(
                                            text = field.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = field.value,
                                            fontSize = 12.sp,
                                            color = Color(0xFFB5BAC1)
                                        )
                                    }
                                }
                            }
                        }

                        // Banner Image
                        if (state.embedImageUrl.isNotBlank()) {
                            AsyncImage(
                                model = state.embedImageUrl,
                                contentDescription = "Embed Banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                        }

                        // Footer & Timestamp
                        if (state.embedFooterText.isNotBlank() || state.embedTimestampEnabled) {
                            Text(
                                text = "${state.embedFooterText} • ${if (state.embedTimestampEnabled) "Hari ini" else ""}".trim(' ', '•'),
                                fontSize = 10.sp,
                                color = Color(0xFF949BA4)
                            )
                        }
                    }
                }

                // Component v2 Buttons Live Preview
                if (state.components.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        modifier = Modifier
                            .padding(start = 48.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.components.forEach { comp ->
                            val btnBg = when (comp.style) {
                                DiscordButtonStyle.PRIMARY.styleId -> Color(0xFF5865F2)
                                DiscordButtonStyle.SUCCESS.styleId -> Color(0xFF248046)
                                DiscordButtonStyle.DANGER.styleId -> Color(0xFFDA373C)
                                DiscordButtonStyle.LINK.styleId -> Color(0xFF4E5058)
                                else -> Color(0xFF4E5058)
                            }
                            Surface(
                                color = btnBg,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    comp.emoji?.name?.let {
                                        Text(text = it, fontSize = 13.sp)
                                    }
                                    Text(
                                        text = comp.label ?: "Button",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (comp.url != null) {
                                        Icon(
                                            imageVector = Icons.Default.OpenInNew,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Dispatch Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.sendWebhook() },
                    enabled = !state.isSendingWebhook,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("send_webhook_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple)
                ) {
                    if (state.isSendingWebhook) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mengirim...")
                    } else {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kirim ke Discord", fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.showPayloadDialog() },
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("JSON", color = DiscordTextWhite)
                }
            }

            state.webhookStatusMessage?.let { status ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = status,
                    fontSize = 13.sp,
                    color = if (status.contains("Gagal")) DiscordRed else DiscordGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (status.contains("Gagal")) DiscordRed.copy(alpha = 0.15f) else DiscordGreen.copy(alpha = 0.15f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Button Dialog
    if (showAddButtonDialog) {
        var label by remember { mutableStateOf("Tombol Kustom") }
        var emoji by remember { mutableStateOf("✨") }
        var style by remember { mutableStateOf(DiscordButtonStyle.PRIMARY) }
        var customId by remember { mutableStateOf("btn_custom") }

        AlertDialog(
            onDismissRequest = { showAddButtonDialog = false },
            title = { Text("Tambah Component v2 Button", color = DiscordTextWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("Label Tombol") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = emoji,
                        onValueChange = { emoji = it },
                        label = { Text("Emoji") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = customId,
                        onValueChange = { customId = it },
                        label = { Text("Custom ID / Action Key") },
                        singleLine = true
                    )
                    Text("Gaya Warna Tombol:", fontSize = 12.sp, color = DiscordTextMuted)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DiscordButtonStyle.values().forEach { bStyle ->
                            Surface(
                                color = if (style == bStyle) DiscordBlurple else DiscordBgElevated,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { style = bStyle }
                            ) {
                                Text(
                                    text = bStyle.title,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addButton(label, style, emoji, customId)
                        showAddButtonDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple)
                ) {
                    Text("Tambahkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddButtonDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = DiscordBgSurface
        )
    }

    // JSON Payload Dialog
    if (state.showPayloadDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPayloadDialog() },
            title = { Text("Discord Webhook Payload JSON", color = DiscordTextWhite, fontSize = 16.sp) },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(DiscordBgInput, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    LazyColumn {
                        item {
                            Text(
                                text = state.rawPayloadJson,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = DiscordTextWhite
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Discord Webhook JSON", state.rawPayloadJson)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "JSON Payload disalin!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Salin JSON")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissPayloadDialog() }) {
                    Text("Tutup")
                }
            },
            containerColor = DiscordBgSurface
        )
    }
}
