<p align="center">
  <a href="https://morphe.software/add-source?github=Ayufa/ayufa-patches">
    <img src=".github/assets/add-to-morphe.svg" alt="Morphe に追加" height="48">
  </a>
</p>

# 🧩 Ayufa パッチ

<p align="center">

![GitHub Release](https://img.shields.io/github/v/release/Ayufa/ayufa-patches?include_prereleases&style=for-the-badge)
![License](https://img.shields.io/github/license/Ayufa/ayufa-patches?style=for-the-badge)
![GitHub Stars](https://img.shields.io/github/stars/Ayufa/ayufa-patches?style=for-the-badge)
![GitHub Forks](https://img.shields.io/github/forks/Ayufa/ayufa-patches?style=for-the-badge)
![GitHub Issues](https://img.shields.io/github/issues/Ayufa/ayufa-patches?style=for-the-badge)

</p>

日本語 | [English](README.en.md)

> [!NOTE]
>
> 個人的に作っている、Morphe 用の独自パッチ集です。公式の Morphe パッチではありません。
>
> YouTube のパッチは本家のパッチと一緒に使う想定です。Simeji のパッチは、本家に Simeji 向けがないため単体で動作します。YouTube のパッチは [Ayufa YouTube Patches](https://github.com/Ayufa/ayufa-youtube-patches) へ分離しました。今後の YouTube パッチの更新はそちらで配布します。

## Contents

- [このリポジトリについて](#-このリポジトリについて)
- [使い方](#使い方)
- [パッチの詳細](#パッチの詳細)
- [パッチ一覧](#-パッチ一覧)
- [不具合の報告](#不具合の報告)
- [FAQ](#faq)
- [本家との関係](#本家との関係)
- [自分でビルドする場合](#️-自分でビルドする場合)
- [ライセンス](#-ライセンス)

## ❓ このリポジトリについて

本家のパッチ（[MorpheApp/morphe-patches](https://github.com/MorpheApp/morphe-patches)）で満足できなかったところや、自分が欲しい機能だけを少し足す、という位置づけのパッチ集です。

| 対象アプリ | 対象バージョン | パッチ数 |
|---|---|---|
| YouTube | 21.30.216 / 20.26.46 | 2 |
| Simeji | 20.7 | 11 |

## ⚠️ 免責

個人の趣味で作っているものです。動作は自分の環境で確認していますが、すべての環境での動作は保証できません。大事な端末で使う前に、よく確認してください。

## 使い方

上のボタンから、このリポジトリを Morphe のパッチソースに追加して、使いたいパッチを選んで適用します（YouTube のパッチは本家のパッチと一緒に選んでください。Simeji はこのリポジトリのパッチだけで大丈夫です）。

⚠️ **対象バージョンは厳密に合わせてください**（バージョンごとに動作を確認しているため、対象以外には当てられません）。

- YouTube「ライブチャットの余計な表示を隠す」→ **21.30.216**
- YouTube「ライブ遅延キーパー」→ **20.26.46**
- Simeji の全パッチ → **20.7**（23.7 には当てないでください）

## パッチの詳細

### 📺 YouTube

#### ライブ遅延キーパー

| 項目 | 内容 |
|---|---|
| 対象 | YouTube **20.26.46**（Android 8.0 で動く最後の YouTube）のみ |
| 何をするか | ライブ配信を長時間つけっぱなしにしていると、再生位置が配信の先端から少しずつ遅れていきます。このパッチは、一定の間隔ごとに、再生を瞬間的に停止→再生します。ライブ配信は停止→再生すると最新の位置に戻るため、遅れが何秒たまっていても常に最新に合わせ直せます（遅れの秒数を測って少しずつ進める方式ではありません） |
| 調整 | 適用後は Morphe 設定の「Ayufaパッチ設定」で、再パッチなしに変更できます。「ライブ配信を最新の状態に保つ」でオン/オフ、「先端に合わせる間隔（分）」で合わせ直す間隔を **0〜120分で自由に入力** できます（0分で自動更新オフ、どちらもその場で反映）。パッチ適用時にも、間隔の初期値を変更できます（初期値は10分） |

使う場面の例：Android 8.0 の古い端末（SOV34 など）をライブ配信のつけっぱなし用にしていて、気づいたら数分遅れている、という場合の対策です。

#### ライブチャットの余計な表示を隠す

| 項目 | 内容 |
|---|---|
| 対象 | YouTube **21.30.216**（本家が推奨している最新の安定版。最新の Android 向け） |
| 何をするか | ライブチャットに出てくる固定コメント（ピン留めされたメッセージ）やアンケート（投票カード）など、チャットを読むときに邪魔な表示を隠します。あわせて、コメントを書くときにチャットがキーボードに合わせて動かず、入力欄がキーボードに隠れてしまう問題も直します（キーボードが開いている間、キーボードの高さに合わせてチャットを持ち上げます） |
| 調整 | パッチ適用時に、項目を個別に選べます。さらに、適用後は Morphe 設定に専用の「Ayufaパッチ設定」画面ができ、その中の「固定メッセージ（バナー）を隠す」「アンケートを隠す」「ライブチャットのキーボード重なりを修正」で、再パッチなしに切り替えられます（次にチャットを開いたときから反映） |

### ⌨️ Simeji

対象はすべて Simeji **20.7** です。各パッチの有効・無効は、アプリ内の「Ayufaパッチ設定」から再パッチなしで切り替えられます。

| パッチ | 何をするか |
|---|---|
| 広告を非表示 | 候補欄に出る広告、ストア入場時の全面広告、Webキャンペーンのポップアップを消します。リワード広告もブロックします（広告を見ると解放される機能も止まります） |
| プロモーションを非表示 | 全画面のキャンペーン／宣伝アクティビティが開かないようにします |
| VIP案内画面をブロック | VIPの案内画面を開かないようにします。この画面はアプリ内課金の購入画面も兼ねているため、ブロック中はアプリ内からは契約できなくなります |
| プッシュポップアップを無効化 | プッシュキャンペーンのダイアログ型ポップアップを表示しません（通常の通知は影響なし） |
| 解約時の引き止め画面をスキップ | VIP解約時の引き止め画面を飛ばして、解約手続きに直行します |
| 解析レポートを無効化 | AppsFlyer へのイベント送信と、Simeji 独自の統計レポート送信を止めます。キーボード・変換・同期などの本体機能には触りません |
| ポイント入口を非表示 | ポイントストアやキャンペーンへの入口（ポイント導線）を表示しません |
| レビュー依頼を無効化 | Google Play の星評価依頼（独自ダイアログと Play 内レビュー依頼の両方）を出さないようにします |
| AIアシスタントのアイコンを非表示 | キーボード右上に表示されるロボット（AIアシスタント）のアイコンと、その吹き出しメッセージを表示しません。アイコンがあった一番上の帯も畳むので、空の帯は残りません。入力・変換には影響しません |
| VIPを偽装 | 端末内のVIP判定だけを有効にします。ツールバーのカスタマイズや、自作きせかえのVIPパーツなど、端末内で完結する機能が使えるようになります（初期値はオフ） |
| アプリ内設定画面を追加 | Simeji の設定タブの一番下に「Ayufaパッチ設定」の入口を追加します。各パッチの切り替えにはこのパッチが必要です |

#### Simeji の注意点

- **設定の場所**：「アプリ内設定画面を追加」を当てると、Simeji アプリ下部の「設定」タブの一番下に「Ayufaパッチ設定」が追加されます。初期値は、VIP偽装以外すべてオンです
- **Google アカウントでログインできません**：再署名されたアプリでは、Google ログインの署名照合（パッケージ名＋証明書）が通らないためです。パッチでは回避できません。Baidu アカウントの ID／パスワードでのログインは影響を受けません
- **VIP偽装の限界**：VIP特典のうちサーバー側で管理されているもの（クラウドきせかえ・バックアップ・翻訳・AI枠など）は、偽装しても使えません

## 🩹 パッチ一覧

<!-- PATCHES_START EXPANDED -->
> **[v1.3.0-dev.10](https://github.com/Ayufa/ayufa-patches/releases/tag/v1.3.0-dev.10)**&nbsp;&nbsp;•&nbsp;&nbsp;`dev`&nbsp;&nbsp;•&nbsp;&nbsp;13 patches total
<details open>
<summary>📦 Simeji&nbsp;&nbsp;•&nbsp;&nbsp;11 patches</summary>
<br>

**Package name:** `com.adamrocker.android.input.simeji`

**🎯 Supported versions:**

| 20.7 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [AIアシスタントのアイコンを非表示](#ai) | キーボード右上に表示されるロボット（AIアシスタント）のアイコンと吹き出しメッセージを非表示にします。アイコンがあった一番上の帯も畳むので、空の帯は残りません。入力・変換には影響しません。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [VIPを偽装](#vip) | 端末内のVIP判定を有効化します。ツールバーのカスタマイズや自作きせかえのVIPパーツなど、端末内で完結する機能が使えるようになります。クラウドきせかえ・クラウド定型文・AIチャットなど、サーバー側で判定される特典は使えません。有効・無効はアプリ内設定（Ayufaパッチ設定）で切り替えます（初期値はオフです）。 |  |
| [VIP案内画面をブロック](#vip) | VIPの案内画面を開かないようにします。注意: この画面はアプリ内のサブスクリプション購入画面も兼ねているため、ブロック中はアプリ内から契約できなくなります。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [アプリ内設定画面を追加](#) | Simeji の設定タブの一覧に「Ayufaパッチ設定」の入口を追加します。各 Simeji パッチの有効・無効や VIP偽装を、再パッチなしでアプリ内から切り替えられるようになります。他の Simeji パッチを切り替えるには、このパッチの適用が必要です（未適用の場合は各パッチの初期値どおりに動作します）。 |  |
| [プッシュポップアップを無効化](#) | プッシュキャンペーンのポップアップダイアログを表示しなくします。通常の通知には影響しません。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [プロモーションを非表示](#) | 全画面のプロモーション画面（キャンペーンやアップセルのページ）を開かないようにします。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [ポイント入口を非表示](#) | ポイントストアやキャンペーンへの入口となる Simejiポイントの入口を非表示にします。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [レビュー依頼を無効化](#) | Google Play の評価依頼を無効化します。Simeji 独自のガイドダイアログと、Play のアプリ内レビューの両方が対象です。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [広告を非表示](#) | Simeji の広告（候補欄の広告、ストア入口の全画面広告、Webキャンペーンのポップアップ）を非表示にします。リワード広告もブロックします。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [解析レポートを無効化](#) | Simeji の利用状況イベント送信を止めます。AppsFlyer 向けの入口と、自社統計の送信処理が、アプリ内設定（Ayufaパッチ設定）のトグルがオンの間だけ無効になります。統計モジュールの初期化には触りません（キーボード表示時に設定オブジェクトを参照するため、潰すとキーボードが落ちます）。 |  |
| [解約時の引き止め画面をスキップ](#) | VIPサブスクリプション解約時に表示される引き止め画面をスキップし、そのまま解約フローへ進みます。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |

</details>

<details open>
<summary>📦 YouTube&nbsp;&nbsp;•&nbsp;&nbsp;2 patches</summary>
<br>

**Package name:** `com.google.android.youtube`

**🎯 Supported versions:**

| 21.30.216 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Hide live chat clutter](#hide-live-chat-clutter) | Hides the pinned message banner and poll cards in live chat, and fixes the chat input field being covered by the on-screen keyboard. Each can be toggled in Morphe settings (Ayufa patch settings) without re-patching. For YouTube 21.30.216. | • Hide pinned message banner<br>• Hide live chat polls<br>• Fix live chat keyboard overlap |
| [Live latency keeper](#live-latency-keeper) | At a fixed interval, momentarily pauses and resumes live stream playback so it snaps back to the live edge (the newest position), no matter how far behind it has fallen. The interval (any value from 0 to 120 minutes, typed in directly) and the on/off switch can be changed in Morphe settings (Ayufa patch settings) without re-patching. For YouTube 20.26.46 (Android 8.0) only. Requires the official Morphe patches to be applied as well. | • Default sync interval (minutes) |

</details>

<!-- PATCHES_END -->

## 不具合の報告

このリポジトリの Issue に報告してください。その際、次の情報を添えてもらえると助かります。

- アプリのバージョン
- 使ったパッチソースのリリース（例：`stable v1.2.0` / `dev v1.3.0-dev.9`）
- APK の入手元と形式（例：APKMirror の単一APK、XAPK など）
- 可能ならログ

本家のパッチ自体の不具合は、ここでは直せません。

## FAQ

### Google アカウントでログインできないのはなぜ？

再署名されたアプリは、Google ログインの署名照合（パッケージ名＋証明書）を通らないためです。パッチでは回避できません。Baidu アカウントの ID／パスワードでのログインは影響を受けません。

### 対象バージョン以外には当てられますか？

当てられません。バージョンごとに動作を確認しているため、対象バージョンの APK を用意してください。

### 「VIPを偽装」をオンにすれば、VIP特典が全部使えますか？

使えません。端末内で完結するもの（ツールバーのカスタマイズ、自作きせかえのVIPパーツなど）だけです。クラウドきせかえ・バックアップ・翻訳・AI枠など、サーバー側で管理されている特典は使えません。

### Simeji の設定はどこにありますか？

「アプリ内設定画面を追加」を当てると、Simeji アプリ下部の「設定」タブの一番下に「Ayufaパッチ設定」が追加されます。そこで各パッチのオン／オフを切り替えます。

### 本家のパッチと一緒に使うのですか？

YouTube のパッチは本家のパッチと一緒に使う想定です。Simeji のパッチは単体で動作します（本家に Simeji 向けのパッチはありません）。

## 本家との関係

- 本家のリポジトリには、プルリクエスト・Issue・コメントなどを一切しません。見るだけ（読み取り専用）で使わせてもらっています
- ライセンスやクレジットは本家に従います（GPL-3.0）

### 本家の更新についていく仕組み

- 本家のパッチャーなどの依存更新は、Dependabot が定期的に知らせてくれます
- 本家の対応 YouTube バージョンが変わったら、GitHub Actions（upstream-watch）が検知して、このリポジトリの中にだけ Issue を立てます。本家側には何も書き込みません
- 詳しくは [本家への追従方法](docs/UPSTREAM.md) に書いてあります
- リリースの署名はしていません（署名鍵は未設定です）

## 🛠️ 自分でビルドする場合

- `./gradlew buildAndroid` を実行すると、`patches/build/libs/` に `.mpp` ファイルができます
- できたファイルは Morphe Desktop などから、他のパッチバンドルと同じように使えます
- 開発は `dev` ブランチで行い、`main` にマージすると安定版のリリースが作られます

## ❤️ 寄付

寄付は完全に任意です。使ってくれること、不具合を報告してくれることが一番の支援です。

もし開発の支援をしたい場合は、GitHub Sponsors からお願いします。

- [GitHub Sponsors](https://github.com/sponsors/Ayufa)

## ❤️ クレジット

- [Morphe](https://github.com/MorpheApp/)
- 土台は本家の [Morphe Patches テンプレート](https://github.com/MorpheApp/morphe-patches-template)です

## 📜 ライセンス

GPL-3.0（[LICENSE](LICENSE)）。[NOTICE](NOTICE) もあわせてご覧ください。
