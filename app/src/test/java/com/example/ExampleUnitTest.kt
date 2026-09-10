package com.example

import com.example.data.ai.AiServerGenerator
import com.example.data.model.DiscordButtonStyle
import com.example.data.model.DiscordChannelType
import com.example.viewmodel.DiscordViewModel
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testPermissionsCalculation() {
    val vm = DiscordViewModel()
    val permVal = vm.calculatePermissionsValue()
    // Administrator(8) + Manage Channels(16) + Manage Server(32) + Send Messages(2048) + ... > 0
    assertTrue(permVal > 0)
    assertTrue((permVal and 8L) == 8L) // Has Administrator
  }

  @Test
  fun testServerTemplates() {
    val generator = AiServerGenerator()
    val presets = generator.presetTemplates
    assertTrue(presets.isNotEmpty())

    val gaming = presets.first { it.id == "gaming" }
    assertEquals("Gaming & Esports Lounge", gaming.title)
    assertTrue(gaming.categories.any { it.name.contains("VOICE", ignoreCase = true) })
    assertTrue(gaming.categories.flatMap { it.channels }.any { it.type == DiscordChannelType.GUILD_VOICE })
  }

  @Test
  fun testWebhookPayloadGeneration() {
    val vm = DiscordViewModel()
    vm.updateWebhookBotName("Test Bot")
    vm.updateEmbedTitle("Turnamen Baru")
    vm.addButton("Register", DiscordButtonStyle.PRIMARY, "🎮", "btn_reg")

    val payload = vm.buildWebhookPayload()
    assertEquals("Test Bot", payload.username)
    assertEquals(1, payload.embeds.size)
    assertEquals("Turnamen Baru", payload.embeds.first().title)
    assertTrue(payload.components.isNotEmpty())
    assertEquals(1, payload.components.first().type) // Action Row
  }
}

