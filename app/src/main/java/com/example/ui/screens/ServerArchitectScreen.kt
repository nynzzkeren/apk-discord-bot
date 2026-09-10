package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiscordChannelType
import com.example.ui.theme.DiscordBgElevated
import com.example.ui.theme.DiscordBgInput
import com.example.ui.theme.DiscordBgSurface
import com.example.ui.theme.DiscordBlurple
import com.example.ui.theme.DiscordBorder
import com.example.ui.theme.DiscordGreen
import com.example.ui.theme.DiscordTextMuted
import com.example.ui.theme.DiscordTextWhite
import com.example.ui.theme.DiscordYellow
import com.example.viewmodel.DiscordUiState
import com.example.viewmodel.DiscordViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServerArchitectScreen(
    state: DiscordUiState,
    viewModel: DiscordViewModel,
    modifier: Modifier = Modifier
) {
    val currentTemplate = state.currentTemplate ?: state.templates.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Target Server Indicator
            val targetName = state.selectedGuild?.name ?: if (state.manualGuildId.isNotBlank()) "Server ID: ${state.manualGuildId}" else "Belum dipilih (Mode Simulasi)"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DiscordBlurple.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .border(1.dp, DiscordBlurple, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Target Server Discord:", fontSize = 11.sp, color = DiscordTextMuted)
                    Text(
                        text = targetName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DiscordTextWhite
                    )
                }
                Surface(
                    color = DiscordBlurple,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (state.selectedGuild != null || state.manualGuildId.isNotBlank()) "SIAP DEPLOY" else "SIMULASI",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Section: AI Prompt Designer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_architect_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordBgSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DiscordBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = DiscordYellow,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "AI Server Architect (Custom Design)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DiscordTextWhite
                        )
                    }

                    Text(
                        text = "Ketik konsep server yang kamu inginkan, AI akan merancang kategori, text channel, dan voice channel lengkap dengan emoji & setting:",
                        fontSize = 13.sp,
                        color = DiscordTextMuted
                    )

                    OutlinedTextField(
                        value = state.aiPrompt,
                        onValueChange = { viewModel.updateAiPrompt(it) },
                        label = { Text("Konsep / Tema Server") },
                        placeholder = { Text("Contoh: Server Guild Mobile Legends dengan ruang Scrim dan VIP Voice") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_prompt_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiscordYellow,
                            unfocusedBorderColor = DiscordBorder,
                            focusedContainerColor = DiscordBgInput,
                            unfocusedContainerColor = DiscordBgInput,
                            focusedTextColor = DiscordTextWhite,
                            unfocusedTextColor = DiscordTextWhite
                        )
                    )

                    Button(
                        onClick = { viewModel.generateWithAi() },
                        enabled = !state.isGeneratingAi,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("generate_ai_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple)
                    ) {
                        if (state.isGeneratingAi) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Sedang Merancang Server...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rancang dengan AI", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: Preset Template Selector
        item {
            Text(
                text = "Pilih Template Desain Server:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DiscordTextWhite
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.templates.forEach { template ->
                    val isSelected = currentTemplate?.id == template.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) DiscordBlurple.copy(alpha = 0.3f) else DiscordBgSurface)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) DiscordBlurple else DiscordBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.selectTemplate(template) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = template.icon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = template.tag,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else DiscordTextMuted
                            )
                        }
                    }
                }
            }
        }

        // Section: Structure View
        currentTemplate?.let { template ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DiscordBgSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DiscordBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = template.icon, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = template.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DiscordTextWhite
                                )
                                Text(
                                    text = template.description,
                                    fontSize = 12.sp,
                                    color = DiscordTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Render Categories and Channels
                        template.categories.forEach { category ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = DiscordYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DiscordTextMuted,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            category.channels.forEach { channel ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
                                        .background(DiscordBgElevated, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (channel.type == DiscordChannelType.GUILD_VOICE) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Voice",
                                                tint = DiscordGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Tag,
                                                contentDescription = "Text",
                                                tint = DiscordTextMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = channel.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = DiscordTextWhite
                                        )
                                    }

                                    if (channel.type == DiscordChannelType.GUILD_VOICE) {
                                        Surface(
                                            color = DiscordGreen.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (channel.userLimit > 0) "${channel.userLimit} Users" else "Unlimited",
                                                fontSize = 10.sp,
                                                color = DiscordGreen,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else if (channel.topic.isNotBlank()) {
                                        Text(
                                            text = channel.topic,
                                            fontSize = 11.sp,
                                            color = DiscordTextMuted,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Deploy Button
                        Button(
                            onClick = { viewModel.deployServerStructure() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("deploy_server_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DiscordGreen)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Deploy Desain ke Server Discord",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Deploy Progress Dialog
    if (state.showDeployDialog) {
        AlertDialog(
            onDismissRequest = { if (!state.isDeployingServer) viewModel.dismissDeployDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.isDeployingServer) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = DiscordBlurple)
                    } else {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = DiscordGreen)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isDeployingServer) "Mendesain Server..." else "Desain Server Selesai!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DiscordTextWhite
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LinearProgressIndicator(
                        progress = { state.deployProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = DiscordGreen,
                        trackColor = DiscordBgElevated
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(DiscordBgInput, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(state.deployLogs) { log ->
                                Text(
                                    text = log,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (log.contains("❌")) DiscordYellow else DiscordTextWhite
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissDeployDialog() },
                    enabled = !state.isDeployingServer,
                    colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple)
                ) {
                    Text("Tutup")
                }
            },
            containerColor = DiscordBgSurface
        )
    }
}
