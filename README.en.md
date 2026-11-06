<p align="center">
  <a href="https://morphe.software/add-source?github=Ayufa/ayufa-patches">
    <img src=".github/assets/add-to-morphe.svg" alt="Add to Morphe" height="48">
  </a>
</p>

# Ayufa Patches

[日本語](README.md) | English

A personal collection of custom patches for Morphe. These are not official Morphe patches (developed in Ayufa's personal repository).

The YouTube patches are meant to be used together with the official patches ([MorpheApp/morphe-patches](https://github.com/MorpheApp/morphe-patches)), adding the small things the official set left unsatisfied. The Simeji patches work entirely on their own, as upstream has no Simeji patches.

## Overview

| App | Supported versions | Patches |
|---|---|---|
| YouTube | 21.30.216 / 20.26.46 | 2 |
| Simeji | 20.7 | 11 |

## How to use

1. Use the button above to add this repository as a patch source in Morphe
2. Select the patches you want and apply them (select the YouTube patches together with the official patches; the Simeji patches are fine on their own)

⚠️ **Match the target versions exactly** (each patch is verified per version, and cannot be applied to other versions).

- YouTube "Hide live chat clutter" → **21.30.216**
- YouTube "Live latency keeper" → **20.26.46**
- All Simeji patches → **20.7** (do not apply them to 23.7)

## Patch details

### YouTube

#### Live latency keeper

| | |
|---|---|
| Target | YouTube **20.26.46** only (the last YouTube that runs on Android 8.0) |
| What it does | When a live stream plays for a long time, the playback position gradually falls behind the live edge. At a fixed interval, this patch momentarily pauses and resumes playback. Resuming a live stream jumps to the newest position, so playback snaps back to the live edge no matter how far behind it has fallen (it does not measure the delay and fast-forward bit by bit) |
| Adjusting | After patching, you can change it in Morphe settings ("Ayufa patch settings") without re-patching: an on/off switch ("Keep live playback at the live edge") and the interval, typed directly as **0–120 minutes** (0 turns automatic resync off; changes apply immediately). The initial interval can also be set when applying the patch (default 10 minutes) |

Example use: an old Android 8.0 device (e.g. SOV34) left playing live streams all day, which ends up minutes behind without you noticing.

#### Hide live chat clutter

| | |
|---|---|
| Target | YouTube **21.30.216** (the latest stable version recommended upstream; for recent Android) |
| What it does | Hides visual clutter in live chat, such as the pinned message banner and poll cards, that gets in the way while reading chat. It also fixes the chat input field being covered by the on-screen keyboard (while the keyboard is open, the chat is lifted by the keyboard's height) |
| Adjusting | Each item can be selected individually when applying the patch. After patching, a dedicated "Ayufa patch settings" screen appears in Morphe settings, where "Hide pinned message banner", "Hide live chat polls" and "Fix live chat keyboard overlap" can be toggled without re-patching (applied the next time you open chat) |

### Simeji

All patches target Simeji **20.7**. Every patch can be toggled in the in-app "Ayufaパッチ設定" screen without re-patching.

| Patch | What it does |
|---|---|
| 広告を非表示 | Removes ads in the suggestion bar, full-screen store-entry ads and web campaign popups. Rewarded ads are blocked too (features unlocked by watching ads stop working) |
| プロモーションを非表示 | Prevents full-screen campaign / upsell activities from opening |
| VIP案内画面をブロック | Prevents the VIP guide screen from opening. This screen doubles as the in-app subscription purchase screen, so while it is blocked you cannot subscribe from inside the app |
| プッシュポップアップを無効化 | Stops dialog-style push campaign popups (normal notifications are not affected) |
| 解約時の引き止め画面をスキップ | Skips the retention screen shown when cancelling VIP and goes straight to the cancellation flow |
| 解析レポートを無効化 | Stops event sending to AppsFlyer and Simeji's own statistic reporting. Core features (keyboard, conversion, sync) are not touched |
| ポイント入口を非表示 | Hides the entry points to the point store and campaigns |
| レビュー依頼を無効化 | Stops the Google Play rating prompts (both Simeji's own dialog and the Play in-app review flow) |
| AIアシスタントのアイコンを非表示 | Hides the robot (AI assistant) icon at the top right of the keyboard and its speech-bubble messages. The top strip the icon lived in is collapsed too, so no empty band remains. Input and conversion are not affected |
| VIPを偽装 | Turns on only the on-device VIP checks. Features that complete entirely on the device, such as toolbar customization and VIP parts in custom skins, become usable (off by default) |
| アプリ内設定画面を追加 | Adds an "Ayufaパッチ設定" entry at the bottom of Simeji's Settings tab. This patch is required to toggle the other Simeji patches |

#### Simeji notes

- **Where the settings are**: with 「アプリ内設定画面を追加」 applied, "Ayufaパッチ設定" appears at the very bottom of the **Settings** tab in the Simeji app. Defaults: everything on, except 「VIPを偽装」
- **Google account sign-in does not work**: a re-signed app cannot pass Google Sign-In's signature check (package name + certificate), and this cannot be worked around with a patch. Signing in with a Baidu account ID/password is not affected
- **Limits of Spoof VIP**: VIP perks managed on the server (cloud skins, backup, translation, AI quota, etc.) remain unavailable even with spoofing

## Relationship with upstream

- No pull requests, issues or comments are ever sent to the upstream repository. It is used read-only
- Bugs in the official patches themselves cannot be fixed here. Please keep issues in this repository to these custom patches only
- Licensing and credits follow upstream (GPL-3.0)

## How upstream updates are tracked

- Dependabot periodically reports dependency updates (such as the patcher) from upstream
- When upstream changes its supported YouTube version, GitHub Actions (upstream-watch) detects it and opens an issue in this repository only. Nothing is ever written to upstream
- See [How upstream is tracked](docs/UPSTREAM.md) for details
- Releases are unsigned (no signing key is configured)

## Building it yourself

- Run `./gradlew buildAndroid` and the `.mpp` file is produced under `patches/build/libs/`
- The resulting file can be used with Morphe Desktop, like any other patch bundle
- Development happens on the `dev` branch; merging into `main` creates a stable release

## License

GPL-3.0 ([LICENSE](LICENSE)). Based on the official [Morphe Patches template](https://github.com/MorpheApp/morphe-patches-template). See also [NOTICE](NOTICE).
