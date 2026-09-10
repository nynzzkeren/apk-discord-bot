package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.DiscordBgElevated
import com.example.ui.theme.DiscordBgInput
import com.example.ui.theme.DiscordBgSurface
import com.example.ui.theme.DiscordBlurple
import com.example.ui.theme.DiscordBorder
import com.example.ui.theme.DiscordGreen
import com.example.ui.theme.DiscordRed
import com.example.ui.theme.DiscordTextMuted
import com.example.ui.theme.DiscordTextWhite
import com.example.viewmodel.DiscordUiState
import com.example.viewmodel.DiscordViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BotInviteScreen(
    state: DiscordUiState,
    viewModel: DiscordViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showTokenPassword by remember { mutableStateOf(false) }

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
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Bot Manager",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column {
                    Text(
                        text = "Discord Bot & Server Manager",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = DiscordTextWhite
                    )
                    Text(
                        text = "Add bot ke server mana saja & kelola bot Discord",
                        fontSize = 12.sp,
                        color = DiscordTextMuted
                    )
                }
            }
        }

        // Card 1: Add Bot to Server (Invite Generator)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bot_invite_card"),
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
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = DiscordBlurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "1. Add Bot ke Server Mana Saja",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DiscordTextWhite
                        )
                    }

                    Text(
                        text = "Masukkan Application / Client ID bot kamu dari Discord Developer Portal untuk membuat invite link resmi:",
                        fontSize = 13.sp,
                        color = DiscordTextMuted
                    )

                    OutlinedTextField(
                        value = state.clientId,
                        onValueChange = { viewModel.updateClientId(it) },
                        label = { Text("Application ID / Client ID") },
                        placeholder = { Text("Contoh: 124589302198302198") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_id_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiscordBlurple,
                            unfocusedBorderColor = DiscordBorder,
                            focusedContainerColor = DiscordBgInput,
                            unfocusedContainerColor = DiscordBgInput,
                            focusedTextColor = DiscordTextWhite,
                            unfocusedTextColor = DiscordTextWhite
                        )
                    )

                    // Permissions Selector
                    Text(
                        text = "Hak Akses / Permissions Bot:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = DiscordTextWhite
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.permissions.forEach { perm ->
                            FilterChip(
                                selected = perm.enabled,
                                onClick = { viewModel.togglePermission(perm.name) },
                                label = { Text(perm.name, fontSize = 12.sp) },
                                leadingIcon = if (perm.enabled) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DiscordBlurple,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                    containerColor = DiscordBgElevated,
                                    labelColor = DiscordTextMuted
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (perm.enabled) DiscordBlurple else DiscordBorder,
                                    enabled = true,
                                    selected = perm.enabled
                                )
                            )
                        }
                    }

                    // Calculated Perms Value
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DiscordBgElevated, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Nilai Permissions Bitwise:", fontSize = 12.sp, color = DiscordTextMuted)
                        Text(
                            text = viewModel.calculatePermissionsValue().toString(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = DiscordGreen
                        )
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val url = viewModel.getInviteUrl()
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("add_bot_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple)
                        ) {
                            Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add to Server", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val url = viewModel.getInviteUrl()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Discord Invite Link", url)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Link invite berhasil disalin!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("copy_invite_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DiscordTextWhite)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Link", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Card 2: Connect Bot Token for Real Server Management
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bot_token_card"),
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
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = DiscordGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "2. Koneksikan Bot Token (Live Control)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DiscordTextWhite
                        )
                    }

                    Text(
                        text = "Hubungkan bot token untuk bisa mendesain server, membuat channel voice, dan mengecek daftar server bot:",
                        fontSize = 13.sp,
                        color = DiscordTextMuted
                    )

                    OutlinedTextField(
                        value = state.botToken,
                        onValueChange = { viewModel.updateBotToken(it) },
                        label = { Text("Discord Bot Token") },
                        placeholder = { Text("OTQ2NDM5... (Rahasia)") },
                        visualTransformation = if (showTokenPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showTokenPassword = !showTokenPassword }) {
                                Icon(
                                    imageVector = if (showTokenPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle token visibility",
                                    tint = DiscordTextMuted
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bot_token_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiscordGreen,
                            unfocusedBorderColor = DiscordBorder,
                            focusedContainerColor = DiscordBgInput,
                            unfocusedContainerColor = DiscordBgInput,
                            focusedTextColor = DiscordTextWhite,
                            unfocusedTextColor = DiscordTextWhite
                        )
                    )

                    Button(
                        onClick = { viewModel.connectBot() },
                        enabled = !state.isConnectingBot,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("connect_bot_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordGreen)
                    ) {
                        if (state.isConnectingBot) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Memverifikasi Bot...", color = Color.Black, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hubungkan & Cek Bot", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Error message if any
                    state.botErrorMessage?.let { error ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DiscordRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, DiscordRed, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = DiscordRed, modifier = Modifier.size(20.dp))
                            Text(text = error, color = DiscordRed, fontSize = 12.sp)
                        }
                    }

                    // Connected Bot Profile Card
                    state.botUser?.let { bot ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DiscordBgElevated, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AsyncImage(
                                model = bot.avatarUrl,
                                contentDescription = "Bot Avatar",
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(DiscordBorder)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = bot.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = DiscordTextWhite
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = DiscordBlurple,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "BOT",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "ID: ${bot.id}",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = DiscordTextMuted
                                )
                                Text(
                                    text = "Status: Terhubung & Aktif",
                                    fontSize = 11.sp,
                                    color = DiscordGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        // Card 3: Server Selection (Guilds where bot is installed)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("guild_selection_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordBgSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DiscordBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "3. Pilih Server Target untuk Desain & Voice",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DiscordTextWhite
                    )

                    if (state.guilds.isEmpty()) {
                        Text(
                            text = "Belum ada server terdeteksi dari bot token. Kamu juga bisa memasukkan Server ID (Guild ID) secara manual di bawah:",
                            fontSize = 13.sp,
                            color = DiscordTextMuted
                        )
                    } else {
                        Text(
                            text = "Server tempat bot berada (${state.guilds.size} server):",
                            fontSize = 13.sp,
                            color = DiscordTextMuted
                        )
                        state.guilds.forEach { guild ->
                            val isSelected = state.selectedGuild?.id == guild.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) DiscordBlurple.copy(alpha = 0.25f) else DiscordBgElevated)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) DiscordBlurple else DiscordBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.selectGuild(guild) }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (guild.iconUrl != null) {
                                    AsyncImage(
                                        model = guild.iconUrl,
                                        contentDescription = guild.name,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(DiscordBlurple),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = guild.name.take(2).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = guild.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = DiscordTextWhite
                                    )
                                    Text(
                                        text = "ID: ${guild.id}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = DiscordTextMuted
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = DiscordBlurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.manualGuildId,
                        onValueChange = { viewModel.updateManualGuildId(it) },
                        label = { Text("Atau Masukkan Server ID (Guild ID) Manual") },
                        placeholder = { Text("Contoh: 109283746592837465") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_guild_id_input"),
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
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
