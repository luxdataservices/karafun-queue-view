# KaraFun Queue View

A free, full-screen "Now Singing / Up Next" display for your KaraFun karaoke queue. Open it in any web browser on a TV and it mirrors your KaraFun queue live.

**Live site:** https://luxdataservices.github.io/karafun-queue-view/

No accounts, no sign-up, nothing to install. It's one static HTML page.

## Why not Rotato?

Rotato is a good tool, but it's built to *run* your singer rotation: singers cycle back to the bottom of its list, and it doesn't follow along when you advance songs in KaraFun. This project does the opposite. KaraFun stays in charge of the queue, and this page just shows it nicely on a TV.

## What it shows

- **Now Singing:** appears once you press play. A green box that pulses slowly, with a mic icon and animated sound bars.
- **Up Next:** the next singer, big and yellow, so they know to get ready. Before you press play, the singer whose song is loaded shows here.
- **Coming Up:** the next eight people in two columns, with names fading from orange to red down the line.

It follows KaraFun exactly: skip, reorder or delete in KaraFun and the screen updates within a second. A song queued with no singer name shows the artist in the big slot instead. Song lines too long to fit take turns showing the title and the artist.

It only **reads** the queue. It never sends anything to KaraFun and never changes your queue.

## Setup

1. In KaraFun, turn on **Remote control** and note the 6-digit code. To show singer names, turn on **Ask options** in KaraFun's queue settings.
2. Open the site in the TV's browser (Fire Stick Silk, a smart TV browser, or a laptop on HDMI).
3. Enter your code on the on-screen number pad. A Fire TV remote's arrows and select button work.

The code is saved on that TV, so next time you just open the page. You can also bookmark it with your code built in: `?code=123456`.

On the TV, press any arrow or select to open a small menu with **Full screen** and **Change code**.

## Make it your own

Set your own logo and colors right in the link. Add any of these to the end of the URL, separated by `&`. Anything you leave out uses the default look (The Jukebox's logo, a purple fade, and orange-to-red names).

| Option | What it does | Example |
|---|---|---|
| `code` | Your KaraFun remote control code | `code=123456` |
| `logo` | A direct link to your logo image. A transparent PNG looks best. `logo=none` hides it | `logo=https://example.com/logo.png` |
| `bg1`, `bg2` | Background fade colors, top-left to bottom-right | `bg1=0b3d2e&bg2=1f7a5a` |
| `up1`, `up2` | Coming Up name colors, from next in line to last | `up1=00e5ff&up2=0055ff` |
| `blank` | A singer name to treat as "no name" (shows the artist instead). Separate several with commas | `blank=Guest` |

Colors are hex codes **without** the `#`. A full example:

```
https://luxdataservices.github.io/karafun-queue-view/?code=123456&logo=https://example.com/logo.png&bg1=0b3d2e&bg2=1f7a5a&up1=00e5ff&up2=0055ff
```

Notes:

- **Logo links** must point at the image file itself (usually ending in .png or .jpg). Links to a Facebook or Google Drive page usually won't work. If the logo doesn't load, the corner stays empty and the queue still works.
- **Settings are remembered** on that device, so after the first visit you can bookmark the short version. Add `?reset` to clear the saved code and settings.
- **Short links** (bit.ly, or [go.aftvnews.com](https://go.aftvnews.com) for Fire Sticks) work fine and save a lot of typing with a remote.

## Fire Stick tips

- **Hide Silk's address bar:** once the page loads, press **down** on the remote.
- **Stop the screensaver:** turn off **Ambient Experience**, or set **Settings → Display & Sounds → Screensaver → Start Time → Never**.
- **Edges cut off?** The TV is cropping the picture. Try **Settings → Display & Sounds → Display → Calibrate Display** on the Fire Stick, or set the TV's picture size to "Just Scan" / "Fit to Screen".
- **Typing the link:** Amazon's Fire TV phone app gives you a phone keyboard.
- **Not seeing an update?** Silk can keep an old copy for up to 10 minutes. Add something like `&v=2` to the end of the link to force a fresh one.

## How it works

KaraFun's public remote page (`karafun.com/<code>/`) embeds a short-lived WebSocket address for the session. The page fetches that (KaraFun allows cross-origin reads), loads KaraFun's own `WebKCS` client from their CDN, and listens for `remote.QueueEvent` and `remote.StatusEvent` as a read-only listener. It reconnects on its own if Wi-Fi drops or KaraFun restarts.

**Unofficial:** this isn't affiliated with KaraFun and relies on their undocumented remote protocol, so a KaraFun update could break it.

Tested with KaraFun on iPad and a 1st-gen Fire Stick in Silk, plus desktop browsers.

## Hosting your own copy

The site is served by GitHub Pages from the `main` branch (Settings → Pages → Deploy from a branch → `main` / root). Fork the repo and do the same to run your own copy; there's no build step.

## Fire TV app (experimental)

`android/` is a small full-screen wrapper app that opens the live site, keeps the screen awake, and reloads itself daily at 5 AM. It uses the default look; for your own logo and colors, use a browser with the link options above.

GitHub Actions builds it on every change to `android/` and attaches it to the **Fire TV app** release. On a Fire OS Fire Stick that allows outside apps, install it with the Downloader app using code **6084763**, which points to:

`https://github.com/luxdataservices/karafun-queue-view/releases/latest/download/jukebox-queue.apk`

In the app: arrows or select open the display menu, Menu (☰) reloads, Back twice exits.
