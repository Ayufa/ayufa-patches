<p align="center">
  <a href="https://morphe.software/add-source?github=Ayufa/ayufa-patches">
    <img src=".github/assets/add-to-morphe.svg" alt="Add to Morphe" height="48">
  </a>
</p>

# Ayufa Patches

[日本語](README.md) | English

A personal collection of custom patches for Morphe. These are not official Morphe patches (developed in Ayufa's personal repository).

They are meant to be used together with the official patches ([MorpheApp/morphe-patches](https://github.com/MorpheApp/morphe-patches)): this collection only adds the small things the official set left unsatisfied, plus features I wanted myself.

## Overview

| App | Supported versions | Patches |
|---|---|---|
| YouTube | 21.30.216 / 20.26.46 | 2 |
| Simeji | 23.7 | 8 |

## How to use

1. Use the button above to add this repository as a patch source in Morphe
2. Select the patches you want and apply them together with the official patches, as usual

⚠️ **Match the target versions exactly** (each patch is verified per version, and cannot be applied to other versions).

- YouTube "Hide live chat clutter" → **21.30.216**
- YouTube "Live latency keeper" → **20.26.46**
- All Simeji patches → **23.7** (split APKs must be merged first; also be sure to read "Simeji notes" below)

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

All patches target Simeji **23.7**.

| Patch | What it does |
|---|---|
| Hide ads | Removes ads in the suggestion bar, full-screen store-entry ads and web campaign popups. The central ad gate is forced off, and ad candidates are never fetched or stored. Rewarded ads can additionally be blocked with a patch option (off by default, as features unlocked by watching ads stop working too) |
| Hide promotions | Prevents full-screen campaign / upsell activities from opening |
| Block VIP guide screen | Prevents the VIP guide screen from opening. This screen doubles as the in-app subscription purchase screen, so subscribing from inside the app becomes impossible |
| Disable push popups | Stops dialog-style push campaign popups (normal notifications are not affected) |
| Skip subscription retention screen | Skips the retention screen shown when cancelling VIP and goes straight to the cancellation flow |
| Disable analytics reporting | Stops event sending to AppsFlyer and Simeji's own statistic reporting. Core features (keyboard, conversion, sync) are not touched |
| Hide point entry | Hides the entry points to the point store and campaigns |
| Disable review prompts | Stops the Google Play rating prompts (both Simeji's own dialog and the Play in-app review flow) |

#### Simeji notes

- ⚠️ **Known issue (no conversion candidates)**: in 23.7, the conversion engine verifies the app's signature in native code, so on a re-signed APK the engine never initializes and no conversion candidates ever appear. This is caused by re-signing itself, regardless of patches. The development builds (prerelease channel) avoid this by targeting **Simeji 20.7**, which has no such check. If you need working conversion, use the development build from Morphe's prerelease channel
- **Split APKs must be merged**: Simeji is distributed as split APKs (XAPK etc.), so merge them into a single APK with Morphe's split-merge feature first, or use an already-merged single APK
- **Replacement install required**: the signature changes, so if the Play version is installed, uninstall it first (your user dictionary and settings are not carried over)
- **Google account sign-in does not work**: a re-signed app cannot pass Google Sign-In's signature check (package name + certificate). Signing in with a Baidu account ID/password is not affected
- **VIP perks themselves cannot be unlocked**: VIP-only skins, backup, translation, AI quota and similar perks are managed on the server, so no patch can unlock them

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
