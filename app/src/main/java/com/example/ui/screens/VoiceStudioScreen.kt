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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun VoiceStudioScreen(
    state: DiscordUiState,
    viewModel: DiscordViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
                        .background(DiscordGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Voice Studio",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column {
                    Text(
                        text = "Voice Channel Studio",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = DiscordTextWhite
                    )
                    Text(
                        text = "Desain channel voice estetik & sistem Auto-Voice otomatis",
                        fontSize = 12.sp,
                        color = DiscordTextMuted
                    )
                }
            }
        }

        // Section 1: Custom Voice Channel Designer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_channel_designer_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordBgSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DiscordBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "1. Desain Custom Voice Channel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DiscordTextWhite
                    )

                    OutlinedTextField(
                        value = state.voiceChannelName,
                        onValueChange = { viewModel.updateVoiceName(it) },
                        label = { Text("Nama Voice Channel") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("voice_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiscordGreen,
                            unfocusedBorderColor = DiscordBorder,
                            focusedContainerColor = DiscordBgInput,
                            unfocusedContainerColor = DiscordBgInput,
                            focusedTextColor = DiscordTextWhite,
                            unfocusedTextColor = DiscordTextWhite
                        )
                    )

                    // Aesthetic Presets
                    Text("Gaya Font Estetik:", fontSize = 12.sp, color = DiscordTextMuted)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "japanese" to "「 🌸・Lounge 」",
                            "boxed" to "【 🔊 】Gaming",
                            "border" to "〢🔊-Squad",
                            "numbered" to "🔊 Room [4]"
                        ).forEach { (styleKey, sampleText) ->
                            Surface(
                                color = DiscordBgElevated,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DiscordBorder),
                                modifier = Modifier.clickable { viewModel.applyVoiceStyle(styleKey) }
                            ) {
                                Text(
                                    text = sampleText,
                                    fontSize = 12.sp,
                                    color = DiscordTextWhite,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // User Limit Selector
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Batas Member (User Limit):", fontSize = 13.sp, color = DiscordTextWhite)
                            Text(
                                text = if (state.voiceUserLimit == 0) "Unlimited (Bebas)" else "${state.voiceUserLimit} Orang",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DiscordGreen
                            )
                        }
                        Slider(
                            value = state.voiceUserLimit.toFloat(),
                            onValueChange = { viewModel.updateVoiceUserLimit(it.toInt()) },
                            valueRange = 0f..25f,
                            steps = 24,
                            colors = SliderDefaults.colors(
                                thumbColor = DiscordGreen,
                                activeTrackColor = DiscordGreen,
                                inactiveTrackColor = DiscordBgElevated
                            )
                        )
                    }

                    // Quick User Limit Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0 to "Bebas", 2 to "Duo [2]", 3 to "Trio [3]", 4 to "Squad [4]", 5 to "Team [5]", 10 to "Room [10]").forEach { (limit, title) ->
                            val isSel = state.voiceUserLimit == limit
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) DiscordGreen else DiscordBgElevated)
                                    .clickable { viewModel.updateVoiceUserLimit(limit) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else DiscordTextMuted
                                )
                            }
                        }
                    }

                    // Bitrate Selector
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Kualitas Suara (Audio Bitrate):", fontSize = 13.sp, color = DiscordTextWhite)
                            Text(
                                text = "${state.voiceBitrateKbps} kbps",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DiscordYellow
                            )
                        }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            listOf(64 to "64k (Standard)", 96 to "96k (HQ)", 128 to "128k (Nitro 1)", 256 to "256k (Nitro 2)").forEach { (kbps, label) ->
                                val isSel = state.voiceBitrateKbps == kbps
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) DiscordYellow else DiscordBgElevated)
                                        .clickable { viewModel.updateVoiceBitrate(kbps) }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.Black else DiscordTextMuted
                                    )
                                }
                            }
                        }
                    }

                    // Create Voice Button
                    Button(
                        onClick = { viewModel.createCustomVoiceChannel() },
                        enabled = !state.isCreatingVoice,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("create_voice_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordGreen)
                    ) {
                        if (state.isCreatingVoice) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Membuat Channel...", color = Color.Black, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buat Channel Voice di Server", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    state.voiceMessage?.let { msg ->
                        Text(
                            text = msg,
                            fontSize = 12.sp,
                            color = DiscordGreen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DiscordGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        )
                    }
                }
            }
        }

        // Section 2: Auto Voice Channel System ("Join to Create")
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auto_voice_card"),
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
                            tint = DiscordBlurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "2. Sistem Auto-Voice (\"Join to Create\")",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DiscordTextWhite
                        )
                    }

                    Text(
                        text = "Fitur di mana bot kamu otomatis membuatkan channel voice sendiri ketika member join ke channel master '➕ Join to Create VC', sesuai desain yang sudah kamu atur di APK!",
                        fontSize = 13.sp,
                        color = DiscordTextMuted
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DiscordBgElevated, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, tint = DiscordGreen, modifier = Modifier.size(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Master Trigger Channel:", fontSize = 11.sp, color = DiscordTextMuted)
                            Text("➕ Join to Create VC", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DiscordTextWhite)
                            Text("Otomatis membuat room: ${state.voiceChannelName}", fontSize = 11.sp, color = DiscordYellow)
                        }
                    }

                    Button(
                        onClick = { viewModel.setupAutoVoiceSystem() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("setup_auto_voice_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pasang Master Auto-Voice di Server", fontWeight = FontWeight.Bold)
                    }

                    // Bot Logic Code Snippet (Copyable for Discord.js / Python bot)
                    Text(
                        text = "Kode Logika Bot (Discord.js / Python) untuk Otomatisasi:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DiscordTextWhite
                    )

                    val codeSnippet = """
// Discord.js v14 Auto-Voice Handler
client.on('voiceStateUpdate', async (oldState, newState) => {
  const joinToCreateId = 'TRIGGER_CHANNEL_ID';
  if (newState.channelId === joinToCreateId) {
    const parentCategory = newState.channel.parentId;
    const voiceChannel = await newState.guild.channels.create({
      name: '${state.voiceChannelName}'.replace('{user}', newState.member.displayName),
      type: 2, // GuildVoice
      parent: parentCategory,
      userLimit: ${state.voiceUserLimit},
      bitrate: ${state.voiceBitrateKbps * 1000}
    });
    await newState.member.voice.setChannel(voiceChannel);
  }
  // Auto delete saat kosong
  if (oldState.channel && oldState.channel.members.size === 0 && oldState.channelId !== joinToCreateId) {
    await oldState.channel.delete().catch(() => {});
  }
});
                    """.trimIndent()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DiscordBgInput, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = codeSnippet,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = DiscordTextWhite,
                            lineHeight = 16.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Auto Voice Code", codeSnippet)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Kode bot berhasil disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Salin Kode Bot Otomatisasi", color = DiscordTextWhite, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
