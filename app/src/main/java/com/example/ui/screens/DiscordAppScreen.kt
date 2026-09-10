package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.DiscordBgDeep
import com.example.ui.theme.DiscordBgElevated
import com.example.ui.theme.DiscordBlurple
import com.example.ui.theme.DiscordBorder
import com.example.ui.theme.DiscordTextMuted
import com.example.ui.theme.DiscordTextWhite
import com.example.viewmodel.DiscordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscordAppScreen(
    viewModel: DiscordViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DiscordBgDeep,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    RowHeader()
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DiscordBgDeep,
                    titleContentColor = DiscordTextWhite
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DiscordBgElevated,
                tonalElevation = 4.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = state.currentTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = { Icon(Icons.Default.SmartToy, contentDescription = "Bot Invite") },
                    label = { Text("Bot & Invite", fontSize = 11.sp, fontWeight = if (state.currentTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = DiscordBlurple,
                        unselectedIconColor = DiscordTextMuted,
                        unselectedTextColor = DiscordTextMuted
                    ),
                    modifier = Modifier.testTag("nav_bot_invite")
                )

                NavigationBarItem(
                    selected = state.currentTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Server Architect") },
                    label = { Text("Architect", fontSize = 11.sp, fontWeight = if (state.currentTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = DiscordBlurple,
                        unselectedIconColor = DiscordTextMuted,
                        unselectedTextColor = DiscordTextMuted
                    ),
                    modifier = Modifier.testTag("nav_server_architect")
                )

                NavigationBarItem(
                    selected = state.currentTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = { Icon(Icons.Default.VolumeUp, contentDescription = "Voice Studio") },
                    label = { Text("Voice Studio", fontSize = 11.sp, fontWeight = if (state.currentTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = DiscordBlurple,
                        unselectedIconColor = DiscordTextMuted,
                        unselectedTextColor = DiscordTextMuted
                    ),
                    modifier = Modifier.testTag("nav_voice_studio")
                )

                NavigationBarItem(
                    selected = state.currentTab == 3,
                    onClick = { viewModel.setTab(3) },
                    icon = { Icon(Icons.Default.Send, contentDescription = "Webhook & Embed") },
                    label = { Text("Embed & v2", fontSize = 11.sp, fontWeight = if (state.currentTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = DiscordBlurple,
                        unselectedIconColor = DiscordTextMuted,
                        unselectedTextColor = DiscordTextMuted
                    ),
                    modifier = Modifier.testTag("nav_webhook_embed")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state.currentTab) {
                0 -> BotInviteScreen(state = state, viewModel = viewModel)
                1 -> ServerArchitectScreen(state = state, viewModel = viewModel)
                2 -> VoiceStudioScreen(state = state, viewModel = viewModel)
                3 -> WebhookEmbedScreen(state = state, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun RowHeader() {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(DiscordBlurple, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = "Discord Architect",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = DiscordTextWhite
        )
        Surface(
            color = DiscordBlurple.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = "APK STUDIO",
                color = DiscordBlurple,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
