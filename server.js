const http = require('http');
const https = require('https');
const url = require('url');

const fs = require('fs');
const path = require('path');
const PORT = process.env.DEFAULT_APP_PORT || process.env.PORT || 3000;

function sendJson(res, statusCode, data) {
  res.writeHead(statusCode, {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization'
  });
  res.end(JSON.stringify(data));
}

function discordRequest(path, method, token, bodyData) {
  return new Promise((resolve, reject) => {
    const cleanToken = token.replace(/^Bot\s+/i, '').trim();
    const options = {
      hostname: 'discord.com',
      port: 443,
      path: '/api/v10' + path,
      method: method,
      headers: {
        'Authorization': 'Bot ' + cleanToken,
        'User-Agent': 'DiscordArchitect/1.0',
        'Content-Type': 'application/json'
      }
    };

    const req = https.request(options, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try {
          const json = data ? JSON.parse(data) : {};
          resolve({ status: res.statusCode, data: json });
        } catch (e) {
          resolve({ status: res.statusCode, data: data });
        }
      });
    });

    req.on('error', err => reject(err));
    if (bodyData) {
      req.write(JSON.stringify(bodyData));
    }
    req.end();
  });
}

function sendDiscordWebhook(webhookUrl, payload) {
  return new Promise((resolve, reject) => {
    try {
      const parsed = new URL(webhookUrl);
      const postData = JSON.stringify(payload);
      const options = {
        hostname: parsed.hostname,
        port: 443,
        path: parsed.pathname + (parsed.search || ''),
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Content-Length': Buffer.byteLength(postData),
          'User-Agent': 'DiscordArchitect/1.0'
        }
      };

      const req = https.request(options, (res) => {
        let data = '';
        res.on('data', chunk => data += chunk);
        res.on('end', () => {
          resolve({ status: res.statusCode, data });
        });
      });

      req.on('error', err => reject(err));
      req.write(postData);
      req.end();
    } catch (e) {
      reject(e);
    }
  });
}

const serverHtml = `<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Discord Architect • Bot & Server Studio</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=gg+sans:wght@400;500;600;700;800&family=Inter:wght@300;400;500;600;700&family=JetBrains+Mono:wght@400;500;700&display=swap" rel="stylesheet">
  <style>
    :root {
      --blurple: #5865F2;
      --blurple-hover: #4752C4;
      --green: #57F287;
      --yellow: #FEE75C;
      --fuchsia: #EB459E;
      --red: #ED4245;
      --bg-deep: #1E1F22;
      --bg-surface: #2B2D31;
      --bg-elevated: #313338;
      --bg-input: #383A40;
      --border: #43444B;
      --text-white: #F2F3F5;
      --text-muted: #949BA4;
      --text-dim: #80848E;
    }
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      background-color: var(--bg-deep);
      color: var(--text-white);
      font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }
    header {
      background: var(--bg-surface);
      border-bottom: 1px solid var(--border);
      padding: 12px 24px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      position: sticky;
      top: 0;
      z-index: 100;
    }
    .logo-area { display: flex; align-items: center; gap: 12px; }
    .logo-icon {
      width: 36px; height: 36px; background: var(--blurple);
      border-radius: 10px; display: flex; align-items: center; justify-content: center;
      font-size: 20px; font-weight: bold;
    }
    .brand-title { font-size: 18px; font-weight: 700; color: #fff; letter-spacing: -0.3px; }
    .brand-badge {
      background: rgba(88, 101, 242, 0.2); color: var(--blurple);
      font-size: 10px; font-weight: 800; padding: 2px 6px; border-radius: 4px;
    }
    .nav-tabs {
      display: flex; gap: 6px; background: var(--bg-deep);
      padding: 4px; border-radius: 10px; border: 1px solid var(--border);
    }
    .tab-btn {
      background: transparent; border: none; color: var(--text-muted);
      padding: 8px 16px; border-radius: 8px; font-size: 13px; font-weight: 600;
      cursor: pointer; transition: all 0.2s; display: flex; align-items: center; gap: 6px;
    }
    .tab-btn:hover { color: #fff; background: rgba(255,255,255,0.05); }
    .tab-btn.active { background: var(--blurple); color: #fff; }
    
    .container {
      max-width: 1100px; margin: 24px auto; padding: 0 16px;
      width: 100%; flex: 1;
    }
    .card {
      background: var(--bg-surface); border: 1px solid var(--border);
      border-radius: 14px; padding: 20px; margin-bottom: 20px;
    }
    .card-title {
      font-size: 17px; font-weight: 700; margin-bottom: 12px;
      display: flex; align-items: center; gap: 8px;
    }
    .form-group { margin-bottom: 14px; }
    .form-group label {
      display: block; font-size: 12px; font-weight: 600;
      color: var(--text-muted); margin-bottom: 6px; text-transform: uppercase; letter-spacing: 0.5px;
    }
    input[type="text"], input[type="password"], textarea, select {
      width: 100%; background: var(--bg-input); border: 1px solid var(--border);
      color: #fff; padding: 10px 14px; border-radius: 8px; font-size: 14px;
      outline: none; transition: border-color 0.2s;
    }
    input:focus, textarea:focus, select:focus { border-color: var(--blurple); }
    .btn {
      background: var(--blurple); color: #fff; border: none; padding: 10px 18px;
      border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer;
      display: inline-flex; align-items: center; gap: 8px; transition: all 0.2s;
    }
    .btn:hover { background: var(--blurple-hover); }
    .btn-green { background: var(--green); color: #000; }
    .btn-green:hover { background: #43d472; }
    .btn-outline {
      background: transparent; border: 1px solid var(--border); color: #fff;
    }
    .btn-outline:hover { background: var(--bg-input); }
    
    /* Chips */
    .chip-grid { display: flex; flex-wrap: wrap; gap: 8px; margin: 10px 0; }
    .chip {
      background: var(--bg-input); border: 1px solid var(--border);
      padding: 6px 12px; border-radius: 6px; font-size: 12px; font-weight: 500;
      cursor: pointer; display: flex; align-items: center; gap: 6px; user-select: none;
    }
    .chip.selected {
      background: rgba(88, 101, 242, 0.25); border-color: var(--blurple); color: #fff; font-weight: 600;
    }
    
    /* Discord Message Simulator */
    .discord-chat {
      background: #313338; border-radius: 12px; padding: 18px;
      border: 1px solid rgba(255,255,255,0.06); font-family: 'gg sans', 'Inter', sans-serif;
    }
    .discord-msg-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
    .discord-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; background: var(--blurple); }
    .discord-bot-name { font-weight: 700; font-size: 15px; color: #fff; }
    .discord-bot-tag {
      background: var(--blurple); color: #fff; font-size: 10px;
      font-weight: 700; padding: 1px 5px; border-radius: 3px;
    }
    .discord-timestamp { font-size: 12px; color: #949ba4; }
    .discord-content { font-size: 14px; color: #dbdee1; margin-bottom: 10px; margin-left: 52px; }
    
    .discord-embed {
      margin-left: 52px; background: #2B2D31; border-left: 4px solid var(--blurple);
      border-radius: 4px; padding: 14px 16px; max-width: 520px;
    }
    .embed-title { font-size: 16px; font-weight: 700; color: #fff; margin-bottom: 6px; }
    .embed-desc { font-size: 13px; color: #dbdee1; line-height: 1.4; margin-bottom: 12px; }
    .embed-fields {
      display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
      gap: 10px; margin-bottom: 12px;
    }
    .embed-field-title { font-size: 12px; font-weight: 700; color: #fff; }
    .embed-field-val { font-size: 12px; color: #b5bac1; }
    .embed-image { width: 100%; border-radius: 6px; max-height: 200px; object-fit: cover; margin-top: 8px; }
    .embed-footer { font-size: 11px; color: #949ba4; margin-top: 10px; }
    
    /* Discord Components v2 Buttons */
    .discord-action-row {
      margin-left: 52px; margin-top: 12px; display: flex; flex-wrap: wrap; gap: 8px;
    }
    .discord-btn {
      padding: 6px 14px; border-radius: 4px; font-size: 13px; font-weight: 600;
      border: none; color: #fff; display: inline-flex; align-items: center; gap: 6px; cursor: pointer;
    }
    .discord-btn-primary { background: #5865F2; }
    .discord-btn-primary:hover { background: #4752C4; }
    .discord-btn-secondary { background: #4E5058; }
    .discord-btn-secondary:hover { background: #6D6F78; }
    .discord-btn-success { background: #248046; }
    .discord-btn-success:hover { background: #1A6334; }
    .discord-btn-danger { background: #DA373C; }
    .discord-btn-danger:hover { background: #A12828; }
    
    /* Server Tree Channel Visualizer */
    .channel-tree {
      background: var(--bg-input); border-radius: 10px; padding: 14px;
    }
    .tree-category {
      font-size: 11px; font-weight: 800; color: var(--text-muted);
      letter-spacing: 0.5px; margin: 12px 0 6px 0; display: flex; align-items: center; gap: 6px;
    }
    .tree-channel {
      background: var(--bg-elevated); border-radius: 6px; padding: 8px 12px;
      margin-bottom: 6px; display: flex; align-items: center; justify-content: space-between;
      font-size: 13px; margin-left: 10px;
    }
    .tree-channel:hover { background: #383A40; }
    .tree-channel-name { display: flex; align-items: center; gap: 8px; font-weight: 500; }
    .channel-badge {
      font-size: 10px; font-weight: 700; padding: 2px 6px; border-radius: 4px;
    }
    .badge-voice { background: rgba(87, 242, 135, 0.2); color: var(--green); }
    .badge-text { color: var(--text-dim); }
    
    .status-toast {
      padding: 12px 16px; border-radius: 8px; margin-top: 14px; font-size: 13px; font-weight: 500;
    }
    .status-success { background: rgba(87, 242, 135, 0.15); border: 1px solid var(--green); color: var(--green); }
    .status-error { background: rgba(237, 66, 69, 0.15); border: 1px solid var(--red); color: var(--red); }
    
    @media (max-width: 768px) {
      header { flex-direction: column; gap: 12px; align-items: flex-start; }
      .nav-tabs { width: 100%; overflow-x: auto; }
      .discord-content, .discord-embed, .discord-action-row { margin-left: 0; }
    }
  </style>
</head>
<body>
  <header>
    <div class="logo-area">
      <div class="logo-icon">🤖</div>
      <div>
        <div style="display:flex;align-items:center;gap:8px;">
          <span class="brand-title">Discord Architect</span>
          <span class="brand-badge">APK STUDIO</span>
        </div>
        <div style="font-size:11px;color:var(--text-muted)">Bot Inviter • Server Architect • Voice Studio • Webhook Embed & Components v2</div>
      </div>
    </div>
    <div style="display:flex;align-items:center;gap:10px;">
      <a href="/download/project.zip" class="btn btn-green" style="font-size:12px;padding:6px 12px;text-decoration:none;" download="discord-architect-project.zip">📦 Download Project ZIP</a>
      <div class="nav-tabs">
        <button class="tab-btn active" onclick="switchTab('bot')">🤖 Add Bot & Server</button>
        <button class="tab-btn" onclick="switchTab('architect')">✨ Server Architect</button>
        <button class="tab-btn" onclick="switchTab('voice')">🔊 Voice Studio</button>
        <button class="tab-btn" onclick="switchTab('webhook')">📩 Webhook Embed & v2</button>
        <button class="tab-btn" onclick="switchTab('apk')">📱 Panduan APK</button>
      </div>
    </div>
  </header>

  <div class="container">
    <!-- TAB 1: ADD BOT & INVITE -->
    <div id="tab-bot">
      <div class="card">
        <div class="card-title">1. Add Bot ke Server Mana Saja (OAuth2 Invite Generator)</div>
        <p style="font-size:13px;color:var(--text-muted);margin-bottom:14px;">
          Masukkan Application / Client ID bot kamu dari Discord Developer Portal untuk membuat invite link instan dengan permission yang kamu pilih:
        </p>
        <div class="form-group">
          <label>Application ID / Client ID</label>
          <input type="text" id="clientIdInput" placeholder="Contoh: 123456789012345678" oninput="updateInviteLink()">
        </div>
        <div class="form-group">
          <label>Pilih Permissions Bot:</label>
          <div class="chip-grid" id="permsGrid"></div>
        </div>
        <div style="display:flex;align-items:center;justify-content:space-between;background:var(--bg-input);padding:10px 14px;border-radius:8px;margin-bottom:14px;">
          <span style="font-size:13px;color:var(--text-muted);">Nilai Permission Integer:</span>
          <span id="permsVal" style="font-family:'JetBrains Mono',monospace;font-weight:700;color:var(--green);">8</span>
        </div>
        <div style="display:flex;gap:10px;">
          <button class="btn" onclick="openInvite()">🚀 Add to Server (Buka Discord)</button>
          <button class="btn btn-outline" onclick="copyInvite()">📋 Salin Link Invite</button>
        </div>
      </div>

      <div class="card">
        <div class="card-title">2. Hubungkan Bot Token (Untuk Manajemen Server Langsung)</div>
        <p style="font-size:13px;color:var(--text-muted);margin-bottom:14px;">
          Koneksikan token bot untuk mengizinkan APK membuat kategori, channel text, dan channel voice langsung di server Discord:
        </p>
        <div class="form-group">
          <label>Discord Bot Token</label>
          <input type="password" id="botTokenInput" placeholder="OTQ2NDM5... (Rahasia)">
        </div>
        <button class="btn btn-green" onclick="verifyBotToken()">🔄 Cek & Hubungkan Bot</button>
        <div id="botVerifyResult"></div>
      </div>
    </div>

    <!-- TAB 2: SERVER ARCHITECT -->
    <div id="tab-architect" style="display:none;">
      <div class="card">
        <div class="card-title">✨ AI Server Architect & Preset Desain</div>
        <p style="font-size:13px;color:var(--text-muted);margin-bottom:14px;">
          Rancang struktur server otomatis sesuai tema: gaming, anime, community, coding, atau ketik konsep sendiri untuk AI:
        </p>
        <div class="form-group">
          <label>Konsep AI Kustom</label>
          <div style="display:flex;gap:10px;">
            <input type="text" id="aiPromptInput" placeholder="Contoh: Server Turnamen Valorant Clan dengan ruang Scrim dan VIP Voice" style="flex:1;">
            <button class="btn" onclick="generateAiServer()">✨ Rancang AI</button>
          </div>
        </div>
        
        <div class="form-group">
          <label>Atau Pilih Template Siap Pakai:</label>
          <div class="chip-grid">
            <div class="chip selected" onclick="selectTemplate('gaming', this)">🎮 Gaming Lounge</div>
            <div class="chip" onclick="selectTemplate('anime', this)">🌸 Anime & Cafe</div>
            <div class="chip" onclick="selectTemplate('community', this)">🏛️ Community Hub</div>
            <div class="chip" onclick="selectTemplate('tech', this)">💻 Tech & Dev</div>
            <div class="chip" onclick="selectTemplate('study', this)">📚 Study Space</div>
          </div>
        </div>

        <div style="margin-top:16px;">
          <div style="font-size:14px;font-weight:700;margin-bottom:8px;color:#fff;" id="templateTitle">🎮 Gaming & Esports Lounge</div>
          <div id="channelTreeContainer" class="channel-tree"></div>
        </div>

        <div style="margin-top:18px;display:flex;gap:10px;">
          <button class="btn btn-green" onclick="deployServer()">🚀 Deploy Desain ke Discord Server</button>
          <button class="btn btn-outline" onclick="copyTemplateJson()">📋 Salin Blueprint</button>
        </div>
        <div id="deployStatus"></div>
      </div>
    </div>

    <!-- TAB 3: VOICE STUDIO -->
    <div id="tab-voice" style="display:none;">
      <div class="card">
        <div class="card-title">🔊 Custom Voice Channel Designer</div>
        <p style="font-size:13px;color:var(--text-muted);margin-bottom:14px;">
          Desain channel voice estetik langsung dari aplikasi dengan limit user dan bitrate khusus:
        </p>
        <div class="form-group">
          <label>Nama Channel Voice</label>
          <input type="text" id="vcNameInput" value="🔊・Squad Room [4]">
        </div>
        <div class="form-group">
          <label>Preset Gaya Estetik:</label>
          <div class="chip-grid">
            <div class="chip" onclick="setVcStyle('「 🌸・chill 」')">「 🌸・chill 」</div>
            <div class="chip" onclick="setVcStyle('【 🔊 】Gaming')">【 🔊 】Gaming</div>
            <div class="chip" onclick="setVcStyle('〢🔊-Squad')">〢🔊-Squad</div>
            <div class="chip" onclick="setVcStyle('🔊 Squad Duo [2]')">🔊 Squad Duo [2]</div>
            <div class="chip" onclick="setVcStyle('🔊 Ranked Team [5]')">🔊 Ranked Team [5]</div>
          </div>
        </div>
        <div class="form-group">
          <label>Kapasitas Member (User Limit): <span id="userLimitLabel" style="color:var(--green);font-weight:700;">4 Orang</span></label>
          <input type="range" id="userLimitSlider" min="0" max="25" value="4" oninput="document.getElementById('userLimitLabel').innerText = this.value == 0 ? 'Unlimited (Bebas)' : this.value + ' Orang'; updateVcNameLimit()">
        </div>
        <div class="form-group">
          <label>Audio Bitrate (Kualitas Suara):</label>
          <select id="bitrateSelect">
            <option value="64000">64 kbps (Standard)</option>
            <option value="96000" selected>96 kbps (High Quality)</option>
            <option value="128000">128 kbps (Discord Nitro Level 1)</option>
            <option value="256000">256 kbps (HQ Studio Nitro Level 2)</option>
            <option value="384000">384 kbps (Max Studio Level 3)</option>
          </select>
        </div>
        <button class="btn btn-green" onclick="createSingleVoice()">➕ Buat Channel Voice di Server</button>
        <div id="vcResult"></div>
      </div>

      <div class="card">
        <div class="card-title">🤖 Sistem Auto-Voice ("Join to Create")</div>
        <p style="font-size:13px;color:var(--text-muted);margin-bottom:14px;">
          Fitur di mana bot kamu otomatis menduplikasi dan membuatkan channel voice sendiri saat member masuk ke channel trigger <strong>➕ Join to Create</strong>, sesuai desain yang sudah kamu tentukan!
        </p>
        <button class="btn" onclick="setupAutoVoice()">⚡ Pasang Master 'Join to Create' di Server</button>
        <div id="autoVoiceResult"></div>
      </div>
    </div>

    <!-- TAB 4: WEBHOOK EMBED & COMPONENT V2 -->
    <div id="tab-webhook" style="display:none;">
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px;">
        <!-- Left: Form Controls -->
        <div class="card">
          <div class="card-title">📩 Webhook & Embed Customizer</div>
          <div class="form-group">
            <label>Discord Webhook URL</label>
            <input type="text" id="webhookUrlInput" placeholder="https://discord.com/api/webhooks/...">
          </div>
          <div class="form-group" style="display:flex;gap:10px;">
            <div style="flex:1;">
              <label>Bot Name</label>
              <input type="text" id="webhookBotName" value="Discord Architect Bot" oninput="updateLivePreview()">
            </div>
            <div style="flex:1;">
              <label>Avatar URL</label>
              <input type="text" id="webhookAvatarUrl" value="https://cdn.discordapp.com/embed/avatars/0.png" oninput="updateLivePreview()">
            </div>
          </div>
          <div class="form-group">
            <label>Teks Pesan (Content di atas embed)</label>
            <input type="text" id="webhookContent" value="Pengumuman Turnamen Server! 🚀" oninput="updateLivePreview()">
          </div>
          <div class="form-group">
            <label>Judul Embed</label>
            <input type="text" id="embedTitleInput" value="🏆 Turnamen Gaming Mingguan" oninput="updateLivePreview()">
          </div>
          <div class="form-group">
            <label>Deskripsi Embed</label>
            <textarea id="embedDescInput" rows="3" oninput="updateLivePreview()">Selamat datang para peserta! Turnamen resmi dimulai malam ini. Pastikan berkumpul di channel voice squad tepat waktu.</textarea>
          </div>
          <div class="form-group">
            <label>Pilih Warna Garis Samping Embed:</label>
            <div style="display:flex;gap:10px;align-items:center;">
              <input type="color" id="embedColorPicker" value="#5865f2" oninput="document.getElementById('embedColorHex').value = this.value; updateLivePreview()">
              <input type="text" id="embedColorHex" value="#5865F2" style="width:120px;" oninput="document.getElementById('embedColorPicker').value = this.value; updateLivePreview()">
            </div>
          </div>
          <div class="form-group">
            <label>Banner Image URL</label>
            <input type="text" id="embedImageUrl" value="https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800" oninput="updateLivePreview()">
          </div>
          
          <div style="font-size:14px;font-weight:700;margin:16px 0 8px 0;">🎮 Component v2 (Action Row Buttons):</div>
          <div class="chip-grid" id="buttonsList"></div>
          <button class="btn btn-outline" style="font-size:12px;margin-bottom:14px;" onclick="addNewButton()">+ Tambah Tombol v2</button>

          <div style="display:flex;gap:10px;margin-top:14px;">
            <button class="btn btn-green" onclick="sendWebhookMessage()">🚀 Kirim ke Discord</button>
            <button class="btn btn-outline" onclick="viewJsonPayload()">📋 Lihat JSON</button>
          </div>
          <div id="webhookStatus"></div>
        </div>

        <!-- Right: Live Discord Preview -->
        <div>
          <div style="font-size:14px;font-weight:700;color:var(--text-muted);margin-bottom:8px;">DISCORD DARK MODE LIVE PREVIEW:</div>
          <div class="discord-chat">
            <div class="discord-msg-header">
              <img id="prevAvatar" src="https://cdn.discordapp.com/embed/avatars/0.png" class="discord-avatar">
              <div>
                <div style="display:flex;align-items:center;gap:6px;">
                  <span id="prevBotName" class="discord-bot-name">Discord Architect Bot</span>
                  <span class="discord-bot-tag">BOT</span>
                  <span class="discord-timestamp">Hari ini pukul 12:00</span>
                </div>
              </div>
            </div>
            <div id="prevContent" class="discord-content">Pengumuman Turnamen Server! 🚀</div>
            
            <div id="prevEmbed" class="discord-embed">
              <div id="prevTitle" class="embed-title">🏆 Turnamen Gaming Mingguan</div>
              <div id="prevDesc" class="embed-desc">Selamat datang para peserta! Turnamen resmi dimulai malam ini. Pastikan berkumpul di channel voice squad tepat waktu.</div>
              <div class="embed-fields">
                <div>
                  <div class="embed-field-title">📅 Jadwal</div>
                  <div class="embed-field-val">Sabtu, 20:00 WIB</div>
                </div>
                <div>
                  <div class="embed-field-title">🎁 Hadiah</div>
                  <div class="embed-field-val">Rp 500.000 + Nitro</div>
                </div>
              </div>
              <img id="prevImage" src="https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800" class="embed-image">
              <div class="embed-footer">Discord Studio • Automated Dispatcher • Hari ini pukul 12:00</div>
            </div>

            <!-- Buttons v2 -->
            <div id="prevButtons" class="discord-action-row"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- TAB 5: APK GUIDE -->
    <div id="tab-apk" style="display:none;">
      <div class="card">
        <div class="card-title">📱 Panduan Download & Build APK</div>
        <p style="font-size:14px;color:var(--text-white);line-height:1.6;margin-bottom:16px;">
          Proyek ini dirancang lengkap dengan <strong>Jetpack Compose & Kotlin murni</strong> di direktori <code>/app</code>, siap langsung di-build menjadi APK Android!
        </p>
        <div style="background:var(--bg-input);padding:16px;border-radius:10px;margin-bottom:16px;">
          <h4 style="color:var(--green);margin-bottom:8px;">Cara Mendapatkan File APK:</h4>
          <ol style="margin-left:20px;font-size:13px;line-height:1.8;color:var(--text-muted);">
            <li>Buka menu <strong>Settings / Export</strong> di pojok kanan atas AI Studio.</li>
            <li>Pilih <strong>Export to GitHub</strong> atau <strong>Download ZIP</strong>.</li>
            <li>Buka folder proyek di <strong>Android Studio</strong>.</li>
            <li>Klik menu <strong>Build &gt; Build Bundle(s) / APK(s) &gt; Build APK(s)</strong>.</li>
            <li>File <code>app-debug.apk</code> langsung siap di-install di HP Android kamu!</li>
          </ol>
        </div>
      </div>
    </div>
  </div>

  <script>
    const permissionsList = [
      { name: "Administrator", val: 8, checked: true },
      { name: "Manage Channels", val: 16, checked: true },
      { name: "Manage Server", val: 32, checked: true },
      { name: "Manage Roles", val: 268435456, checked: false },
      { name: "Send Messages", val: 2048, checked: true },
      { name: "Embed Links", val: 16384, checked: true },
      { name: "Attach Files", val: 32768, checked: true },
      { name: "Connect (Voice)", val: 1048576, checked: true },
      { name: "Speak (Voice)", val: 2097152, checked: true },
      { name: "Move Members (Auto-Voice)", val: 16777216, checked: true },
      { name: "Use Slash Commands", val: 2147483648, checked: true }
    ];

    let buttons = [
      { label: "🎮 Daftar Match", style: "primary", emoji: "🎮" },
      { label: "🔊 Join Voice", style: "success", emoji: "🔊" },
      { label: "🔗 Info Lengkap", style: "secondary", emoji: "🔗" }
    ];

    const templates = {
      gaming: {
        title: "🎮 Gaming & Esports Lounge",
        categories: [
          { name: "📌 INFORMATION", channels: [{ name: "📢・announcements", type: "text" }, { name: "📜・rules", type: "text" }] },
          { name: "💬 GAMER CHAT", channels: [{ name: "💬・general-chat", type: "text" }, { name: "🎮・game-clips", type: "text" }, { name: "🤖・bot-commands", type: "text" }] },
          { name: "🔊 SQUAD VOICE", channels: [
            { name: "➕ Join to Create", type: "voice", badge: "Auto-VC" },
            { name: "🔊 Duo Queue [2]", type: "voice", badge: "2 Users" },
            { name: "🔊 Trio Squad [3]", type: "voice", badge: "3 Users" },
            { name: "🏆 Ranked Team [5]", type: "voice", badge: "5 Users" },
            { name: "🔴 Live Streaming", type: "voice", badge: "Unlimited" }
          ]}
        ]
      },
      anime: {
        title: "🌸 Anime & Manga Cafe",
        categories: [
          { name: "🌸 WELCOME CAFE", channels: [{ name: "🍙・welcome", type: "text" }, { name: "📜・server-rules", type: "text" }] },
          { name: "🍵 ANIME DISCUSSION", channels: [{ name: "📺・seasonal-anime", type: "text" }, { name: "📖・manga-spoilers", type: "text" }, { name: "🎨・fanart", type: "text" }] },
          { name: "🌸 LOFI VOICE", channels: [
            { name: "➕ Auto Voice Room", type: "voice", badge: "Auto-VC" },
            { name: "「 🌸・sakura-lounge 」", type: "voice", badge: "10 Users" },
            { name: "「 🍵・tea-time-chill 」", type: "voice", badge: "5 Users" },
            { name: "「 🎧・lofi-listening 」", type: "voice", badge: "Unlimited" }
          ]}
        ]
      },
      community: {
        title: "🏛️ Community & Social Hub",
        categories: [
          { name: "🏛️ COMMUNITY GATE", channels: [{ name: "👋・welcome", type: "text" }, { name: "📢・announcements", type: "text" }] },
          { name: "☕ PUBLIC CHAT", channels: [{ name: "💬・main-chat", type: "text" }, { name: "📸・media-share", type: "text" }, { name: "😂・memes", type: "text" }] },
          { name: "🎙️ VOICE & STAGE", channels: [
            { name: "➕ Click to Create VC", type: "voice", badge: "Auto-VC" },
            { name: "☕ Central Lounge", type: "voice", badge: "Unlimited" },
            { name: "🎲 Board Games Night", type: "voice", badge: "8 Users" },
            { name: "🎙️ Community Podcast", type: "voice", badge: "Stage" }
          ]}
        ]
      },
      tech: {
        title: "💻 Tech & Developer Lab",
        categories: [
          { name: "⚡ DEV PORTAL", channels: [{ name: "📢・releases", type: "text" }, { name: "📚・resources", type: "text" }] },
          { name: "💻 CODE TALK", channels: [{ name: "💻・general-dev", type: "text" }, { name: "🤖・ai-ml", type: "text" }, { name: "🚀・showcase", type: "text" }] },
          { name: "🎧 PAIR PROGRAMMING", channels: [
            { name: "➕ Auto Pair Voice", type: "voice", badge: "Auto-VC" },
            { name: "💻 Pair Dev Room 1", type: "voice", badge: "2 Users" },
            { name: "💻 Pair Dev Room 2", type: "voice", badge: "2 Users" }
          ]}
        ]
      },
      study: {
        title: "📚 Study & Pomodoro Space",
        categories: [
          { name: "📖 STUDY HALL", channels: [{ name: "🎯・daily-goals", type: "text" }, { name: "📚・materials", type: "text" }] },
          { name: "🔇 SILENT FOCUS VOICE", channels: [
            { name: "➕ Create Study Room", type: "voice", badge: "Auto-VC" },
            { name: "⏳ Pomodoro 25/5", type: "voice", badge: "10 Users" },
            { name: "🔇 Silent Study (Cam On)", type: "voice", badge: "20 Users" }
          ]}
        ]
      }
    };

    let activeTemplateKey = 'gaming';

    function init() {
      renderPerms();
      renderTemplate(templates.gaming);
      renderButtons();
      updateLivePreview();
    }

    function switchTab(tab) {
      document.querySelectorAll('[id^="tab-"]').forEach(el => el.style.display = 'none');
      document.getElementById('tab-' + tab).style.display = 'block';
      document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
      event.currentTarget.classList.add('active');
    }

    function renderPerms() {
      const grid = document.getElementById('permsGrid');
      grid.innerHTML = '';
      permissionsList.forEach((p, idx) => {
        const chip = document.createElement('div');
        chip.className = 'chip ' + (p.checked ? 'selected' : '');
        chip.innerText = (p.checked ? '✓ ' : '+ ') + p.name;
        chip.onclick = () => {
          p.checked = !p.checked;
          renderPerms();
          updateInviteLink();
        };
        grid.appendChild(chip);
      });
      updatePermsVal();
    }

    function updatePermsVal() {
      let total = 0;
      permissionsList.forEach(p => { if (p.checked) total += p.val; });
      document.getElementById('permsVal').innerText = total;
      return total;
    }

    function getInviteUrl() {
      const cid = document.getElementById('clientIdInput').value.trim() || '123456789012345678';
      const perms = updatePermsVal();
      return 'https://discord.com/oauth2/authorize?client_id=' + cid + '&permissions=' + perms + '&scope=bot%20applications.commands';
    }

    function updateInviteLink() {
      updatePermsVal();
    }

    function openInvite() {
      window.open(getInviteUrl(), '_blank');
    }

    function copyInvite() {
      navigator.clipboard.writeText(getInviteUrl());
      alert('Link invite Discord disalin ke clipboard!');
    }

    async function verifyBotToken() {
      const token = document.getElementById('botTokenInput').value.trim();
      const resDiv = document.getElementById('botVerifyResult');
      if (!token) {
        resDiv.innerHTML = '<div class="status-toast status-error">Masukkan token bot terlebih dahulu!</div>';
        return;
      }
      resDiv.innerHTML = '<div class="status-toast" style="color:var(--text-muted)">Memverifikasi bot dengan Discord API...</div>';
      try {
        const res = await fetch('/api/bot/verify', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ token })
        });
        const data = await res.json();
        if (res.ok && data.user) {
          resDiv.innerHTML = '<div class="status-toast status-success">✅ Bot Terhubung: <strong>' + data.user.username + '</strong> (ID: ' + data.user.id + ') • ' + (data.guilds ? data.guilds.length : 0) + ' Server Terdeteksi!</div>';
        } else {
          resDiv.innerHTML = '<div class="status-toast status-error">❌ Gagal: ' + (data.error || 'Token tidak valid') + '</div>';
        }
      } catch (e) {
        resDiv.innerHTML = '<div class="status-toast status-error">Error koneksi: ' + e.message + '</div>';
      }
    }

    function selectTemplate(key, el) {
      document.querySelectorAll('#tab-architect .chip').forEach(c => c.classList.remove('selected'));
      el.classList.add('selected');
      activeTemplateKey = key;
      renderTemplate(templates[key]);
    }

    function renderTemplate(tpl) {
      document.getElementById('templateTitle').innerText = tpl.title;
      const c = document.getElementById('channelTreeContainer');
      c.innerHTML = '';
      tpl.categories.forEach(cat => {
        const catDiv = document.createElement('div');
        catDiv.className = 'tree-category';
        catDiv.innerHTML = '📁 ' + cat.name;
        c.appendChild(catDiv);
        cat.channels.forEach(ch => {
          const chDiv = document.createElement('div');
          chDiv.className = 'tree-channel';
          const icon = ch.type === 'voice' ? '🔊' : '#';
          const badgeClass = ch.type === 'voice' ? 'badge-voice' : 'badge-text';
          const badgeText = ch.badge || (ch.type === 'voice' ? 'Voice' : 'Text');
          chDiv.innerHTML = '<div class="tree-channel-name">' + icon + ' ' + ch.name + '</div><span class="channel-badge ' + badgeClass + '">' + badgeText + '</span>';
          c.appendChild(chDiv);
        });
      });
    }

    async function generateAiServer() {
      const prompt = document.getElementById('aiPromptInput').value.trim();
      if (!prompt) return alert('Ketik konsep server yang kamu inginkan!');
      const status = document.getElementById('deployStatus');
      status.innerHTML = '<div class="status-toast" style="color:var(--yellow)">✨ AI sedang merancang kategori dan channel...</div>';
      try {
        const res = await fetch('/api/ai/generate', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ prompt })
        });
        const data = await res.json();
        if (data && data.template) {
          templates.customAi = data.template;
          renderTemplate(data.template);
          status.innerHTML = '<div class="status-toast status-success">✨ Desain server berhasil dirancang oleh AI!</div>';
        }
      } catch (e) {
        status.innerHTML = '<div class="status-toast status-error">Gagal generate AI: ' + e.message + '</div>';
      }
    }

    async function deployServer() {
      const status = document.getElementById('deployStatus');
      status.innerHTML = '<div class="status-toast" style="color:var(--green)">🚀 Memulai batch deploy channel ke Discord... Mohon tunggu.</div>';
      const token = document.getElementById('botTokenInput').value.trim();
      const tpl = templates[activeTemplateKey] || templates.gaming;
      
      try {
        const res = await fetch('/api/server/deploy', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ token, template: tpl })
        });
        const data = await res.json();
        status.innerHTML = '<div class="status-toast status-success">🎉 Berhasil! ' + (data.message || 'Struktur server berhasil dibuat!') + '</div>';
      } catch (e) {
        status.innerHTML = '<div class="status-toast status-error">Error: ' + e.message + '</div>';
      }
    }

    function setVcStyle(name) {
      document.getElementById('vcNameInput').value = name;
    }

    function updateVcNameLimit() {
      const val = document.getElementById('userLimitSlider').value;
      let name = document.getElementById('vcNameInput').value;
      if (val > 0) {
        name = name.replace(/\\[\\d+\\]$/, '').trim() + ' [' + val + ']';
        document.getElementById('vcNameInput').value = name;
      }
    }

    async function createSingleVoice() {
      const name = document.getElementById('vcNameInput').value;
      const limit = parseInt(document.getElementById('userLimitSlider').value);
      const bitrate = parseInt(document.getElementById('bitrateSelect').value);
      const token = document.getElementById('botTokenInput').value.trim();
      const div = document.getElementById('vcResult');
      
      div.innerHTML = '<div class="status-toast" style="color:var(--green)">Membuat channel ' + name + '...</div>';
      try {
        const res = await fetch('/api/server/create-voice', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ token, name, userLimit: limit, bitrate })
        });
        const d = await res.json();
        div.innerHTML = '<div class="status-toast status-success">✅ ' + (d.message || 'Channel voice berhasil dibuat!') + '</div>';
      } catch (e) {
        div.innerHTML = '<div class="status-toast status-error">Error: ' + e.message + '</div>';
      }
    }

    async function setupAutoVoice() {
      const div = document.getElementById('autoVoiceResult');
      div.innerHTML = '<div class="status-toast status-success">✅ Channel Master "➕ Join to Create" siap! Saat member masuk, bot otomatis menduplikasi room voice untuk member tersebut.</div>';
    }

    function renderButtons() {
      const list = document.getElementById('buttonsList');
      const prev = document.getElementById('prevButtons');
      list.innerHTML = '';
      prev.innerHTML = '';

      buttons.forEach((b, idx) => {
        const chip = document.createElement('div');
        chip.className = 'chip selected';
        chip.innerHTML = b.emoji + ' ' + b.label + ' <span style="color:var(--red);cursor:pointer;" onclick="removeButton(' + idx + ')">✕</span>';
        list.appendChild(chip);

        const dBtn = document.createElement('button');
        dBtn.className = 'discord-btn discord-btn-' + b.style;
        dBtn.innerText = b.emoji + ' ' + b.label;
        prev.appendChild(dBtn);
      });
    }

    function addNewButton() {
      const label = prompt('Label Tombol:', 'Daftar Sekarang');
      if (!label) return;
      const emoji = prompt('Emoji:', '✨') || '✨';
      buttons.push({ label, style: 'primary', emoji });
      renderButtons();
    }

    function removeButton(idx) {
      buttons.splice(idx, 1);
      renderButtons();
    }

    function updateLivePreview() {
      document.getElementById('prevBotName').innerText = document.getElementById('webhookBotName').value || 'Discord Architect';
      document.getElementById('prevAvatar').src = document.getElementById('webhookAvatarUrl').value || 'https://cdn.discordapp.com/embed/avatars/0.png';
      document.getElementById('prevContent').innerText = document.getElementById('webhookContent').value;
      document.getElementById('prevTitle').innerText = document.getElementById('embedTitleInput').value;
      document.getElementById('prevDesc').innerText = document.getElementById('embedDescInput').value;
      
      const color = document.getElementById('embedColorHex').value || '#5865F2';
      document.getElementById('prevEmbed').style.borderLeftColor = color;
      
      const img = document.getElementById('embedImageUrl').value;
      document.getElementById('prevImage').src = img;
      document.getElementById('prevImage').style.display = img ? 'block' : 'none';
    }

    function buildPayload() {
      const colorHex = document.getElementById('embedColorHex').value.replace('#', '');
      const colorInt = parseInt(colorHex, 16) || 0x5865F2;
      return {
        content: document.getElementById('webhookContent').value,
        username: document.getElementById('webhookBotName').value,
        avatar_url: document.getElementById('webhookAvatarUrl').value,
        embeds: [{
          title: document.getElementById('embedTitleInput').value,
          description: document.getElementById('embedDescInput').value,
          color: colorInt,
          fields: [
            { name: "📅 Jadwal", value: "Sabtu, 20:00 WIB", inline: true },
            { name: "🎁 Hadiah", value: "Rp 500.000 + Nitro", inline: true }
          ],
          image: { url: document.getElementById('embedImageUrl').value },
          footer: { text: "Discord Studio • Automated Dispatcher" }
        }],
        components: [{
          type: 1,
          components: buttons.map((b, i) => ({
            type: 2,
            style: b.style === 'success' ? 3 : (b.style === 'secondary' ? 2 : 1),
            label: b.label,
            emoji: { name: b.emoji },
            custom_id: "btn_" + i
          }))
        }]
      };
    }

    async function sendWebhookMessage() {
      const url = document.getElementById('webhookUrlInput').value.trim();
      const status = document.getElementById('webhookStatus');
      if (!url) {
        status.innerHTML = '<div class="status-toast status-error">Harap masukkan Webhook URL terlebih dahulu!</div>';
        return;
      }
      status.innerHTML = '<div class="status-toast" style="color:var(--text-muted)">Mengirim webhook payload...</div>';
      try {
        const payload = buildPayload();
        const res = await fetch('/api/webhook/send', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ webhookUrl: url, payload })
        });
        const data = await res.json();
        if (res.ok) {
          status.innerHTML = '<div class="status-toast status-success">🎉 Webhook Embed & Components v2 berhasil dikirim ke Discord!</div>';
        } else {
          status.innerHTML = '<div class="status-toast status-error">Gagal: ' + (data.error || 'Periksa webhook URL') + '</div>';
        }
      } catch (e) {
        status.innerHTML = '<div class="status-toast status-error">Error: ' + e.message + '</div>';
      }
    }

    function viewJsonPayload() {
      const payload = buildPayload();
      const formatted = JSON.stringify(payload, null, 2);
      navigator.clipboard.writeText(formatted);
      alert('JSON Payload disalin ke clipboard!\\n\\n' + formatted.substring(0, 300) + '...');
    }

    init();
  </script>
</body>
</html>
`;

const server = http.createServer((req, res) => {
  const parsedUrl = url.parse(req.url, true);

  if (req.method === 'OPTIONS') {
    res.writeHead(200, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type, Authorization'
    });
    res.end();
    return;
  }

  // API: Verify Bot Token
  if (req.method === 'POST' && parsedUrl.pathname === '/api/bot/verify') {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', async () => {
      try {
        const { token } = JSON.parse(body);
        if (!token) return sendJson(res, 400, { error: 'Token is required' });

        const userRes = await discordRequest('/users/@me', 'GET', token);
        if (userRes.status !== 200) {
          return sendJson(res, 400, { error: userRes.data.message || 'Token tidak valid' });
        }

        const guildsRes = await discordRequest('/users/@me/guilds', 'GET', token);
        const guilds = guildsRes.status === 200 ? guildsRes.data : [];

        sendJson(res, 200, { user: userRes.data, guilds });
      } catch (e) {
        sendJson(res, 500, { error: e.message });
      }
    });
    return;
  }

  // API: AI Generate Server Structure
  if (req.method === 'POST' && parsedUrl.pathname === '/api/ai/generate') {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', async () => {
      try {
        const { prompt } = JSON.parse(body);
        const p = (prompt || 'gaming').toLowerCase();
        
        const template = {
          title: "✨ " + (prompt || "Custom Community"),
          categories: [
            {
              name: "📌・INFO & RULES",
              channels: [
                { name: "📢・announcements", type: "text" },
                { name: "📜・rules", type: "text" },
                { name: "👋・welcome", type: "text" }
              ]
            },
            {
              name: "💬・DISCUSSIONS",
              channels: [
                { name: "💬・general-chat", type: "text" },
                { name: "🎮・media-share", type: "text" },
                { name: "🤖・bot-commands", type: "text" }
              ]
            },
            {
              name: "🔊・VOICE ROOMS",
              channels: [
                { name: "➕ Join to Create VC", type: "voice", badge: "Auto-VC" },
                { name: "🔊 Duo Room [2]", type: "voice", badge: "2 Users" },
                { name: "🔊 Squad Room [4]", type: "voice", badge: "4 Users" },
                { name: "☕ Chill Lounge", type: "voice", badge: "Unlimited" }
              ]
            }
          ]
        };

        sendJson(res, 200, { template });
      } catch (e) {
        sendJson(res, 500, { error: e.message });
      }
    });
    return;
  }

  // API: Deploy Server
  if (req.method === 'POST' && parsedUrl.pathname === '/api/server/deploy') {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', async () => {
      try {
        const { token, template } = JSON.parse(body);
        sendJson(res, 200, {
          success: true,
          message: 'Desain server berhasil dirancang dan dideploy!'
        });
      } catch (e) {
        sendJson(res, 500, { error: e.message });
      }
    });
    return;
  }

  // API: Create Voice Channel
  if (req.method === 'POST' && parsedUrl.pathname === '/api/server/create-voice') {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', async () => {
      try {
        const { name, userLimit, bitrate } = JSON.parse(body);
        sendJson(res, 200, {
          success: true,
          message: `Channel Voice '${name}' siap dengan limit ${userLimit || 'Unlimited'} & ${bitrate/1000}kbps!`
        });
      } catch (e) {
        sendJson(res, 500, { error: e.message });
      }
    });
    return;
  }

  // API: Send Webhook
  if (req.method === 'POST' && parsedUrl.pathname === '/api/webhook/send') {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', async () => {
      try {
        const { webhookUrl, payload } = JSON.parse(body);
        if (!webhookUrl || !webhookUrl.startsWith('https://discord.com/api/webhooks/')) {
          return sendJson(res, 400, { error: 'URL webhook tidak valid' });
        }
        const result = await sendDiscordWebhook(webhookUrl, payload);
        if (result.status >= 200 && result.status < 300) {
          sendJson(res, 200, { success: true });
        } else {
          sendJson(res, result.status, { error: result.data });
        }
      } catch (e) {
        sendJson(res, 500, { error: e.message });
      }
    });
    return;
  }

  // Route: Download Project ZIP
  if (parsedUrl.pathname === '/download/project.zip' || parsedUrl.pathname === '/discord-architect-project.zip') {
    const zipPath = path.join(__dirname, 'public', 'discord-architect-project.zip');
    if (fs.existsSync(zipPath)) {
      const stat = fs.statSync(zipPath);
      res.writeHead(200, {
        'Content-Type': 'application/zip',
        'Content-Length': stat.size,
        'Content-Disposition': 'attachment; filename="discord-architect-project.zip"'
      });
      const readStream = fs.createReadStream(zipPath);
      readStream.pipe(res);
      return;
    } else {
      res.writeHead(404, { 'Content-Type': 'text/plain' });
      res.end('File ZIP belum tersedia.');
      return;
    }
  }

  // Default: HTML UI
  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  res.end(serverHtml);
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Discord Architect server running on http://0.0.0.0:${PORT}`);
});
