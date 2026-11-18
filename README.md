# KaraFun Queue View

A full-screen "Now singing / Up next" display for a KaraFun karaoke queue, built for a TV (tested for Fire TV's Silk browser).

It's a single static page (`index.html`) with no build step and no server. It connects to KaraFun's remote-control session as a read-only listener and mirrors the queue live. When KaraFun moves on to the next song, the display follows. Songs without a singer name show the artist in the big slot instead.

## Use

1. In KaraFun, turn on **Remote control** and note the 6-digit code.
2. Open the site on the TV. Enter the code on the on-screen number pad (D-pad + select). The code is saved on that device only and is never stored in this repo.
3. Press any arrow or select on the remote to open the menu: **Full screen**, **Change code**.

URL options:
- `?code=123456` sets and saves the code (handy as a bookmark on the TV)
- `?reset` forgets the saved code
- `?blank=Default Name` treats that singer name as "no singer", so those songs show the artist instead. Use this for the name KaraFun fills in when nobody types one. Separate several names with commas. Saved on the device.

Fire TV tip: set **Settings → Display & Sounds → Screensaver → Start Time → Never** so the screensaver doesn't cover the queue.

## Deploy

Import this repo into Vercel as a project (framework preset: **Other**, no build command). Every push to `main` redeploys.

## How it works

KaraFun's public remote page (`karafun.com/<code>/`) embeds a short-lived WebSocket address for the session. The page fetches that (KaraFun allows cross-origin reads), loads KaraFun's own `WebKCS` client from their CDN, and listens for `remote.QueueEvent` and `remote.StatusEvent`. It doesn't send anything to KaraFun.

This relies on KaraFun's undocumented remote protocol, so a KaraFun update could break it.

## Fire TV app

`android/` is a tiny full-screen wrapper app that opens the GitHub Pages site, keeps the screen awake, and reloads itself daily at 5 AM so it always runs the latest version. Display changes go live by pushing `index.html`; the app only needs reinstalling if `android/` changes.

GitHub Actions builds it on every change to `android/` and attaches it to the **Fire TV app** release. Install it on a Fire OS Fire TV Stick with the Downloader app:

`https://github.com/luxdataservices/karafun-queue-view/releases/latest/download/jukebox-queue.apk`

Remote buttons in the app: arrows or select open the display menu, Menu (☰) reloads, Back twice exits.
