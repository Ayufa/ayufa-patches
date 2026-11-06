<p align="center">
  <a href="https://morphe.software/add-source?github=Ayufa/ayufa-patches">
    <img src=".github/assets/add-to-morphe.svg" alt="Morphe に追加" height="48">
  </a>
</p>

# Ayufa パッチ

日本語 | [English](README.en.md)

Morphe 用の独自パッチ集です。公式の Morphe パッチではありません（Ayufa 個人のリポジトリで開発しています）。

YouTube のパッチは、本家のパッチ（[MorpheApp/morphe-patches](https://github.com/MorpheApp/morphe-patches)）と一緒に使う想定で、本家で満足できなかったところを少し足す位置づけです。Simeji のパッチは本家に Simeji 向けがないため、このリポジトリのパッチだけで動作します。

## 概要

| 対象アプリ | 対象バージョン | パッチ数 |
|---|---|---|
| YouTube | 21.30.216 / 20.26.46 | 2 |
| Simeji | 20.7 | 11 |

## 使い方

1. 上のボタンから、このリポジトリを Morphe のパッチソースに追加します
2. 使いたいパッチを選んで適用します（YouTube のパッチは本家のパッチと一緒に選んでください。Simeji はこのリポジトリのパッチだけで大丈夫です）

⚠️ **対象バージョンは厳密に合わせてください**（バージョンごとに動作を確認しているため、対象以外には当てられません）。

- YouTube「ライブチャットの余計な表示を隠す」→ **21.30.216**
- YouTube「ライブ遅延キーパー」→ **20.26.46**
- Simeji の全パッチ → **20.7**（23.7 には当てないでください。理由は下の「Simeji の注意点」）

## パッチの詳細

### YouTube

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

### Simeji

対象はすべて Simeji **20.7** です。各パッチの有効・無効は、アプリ内の「Ayufaパッチ設定」から再パッチなしで切り替えられます。

| パッチ | 何をするか |
|---|---|
| Hide ads | 候補欄に出る広告、ストア入場時の全面広告、Webキャンペーンのポップアップを消します。リワード広告もブロックします（広告を見ると解放される機能も止まります） |
| Hide promotions | 全画面のキャンペーン／宣伝アクティビティが開かないようにします |
| Block VIP guide screen | VIPの案内画面を開かないようにします。この画面はアプリ内課金の購入画面も兼ねているため、ブロック中はアプリ内からは契約できなくなります |
| Disable push popups | プッシュキャンペーンのダイアログ型ポップアップを表示しません（通常の通知は影響なし） |
| Skip subscription retention screen | VIP解約時の引き止め画面を飛ばして、解約手続きに直行します |
| Disable analytics reporting | AppsFlyer へのイベント送信と、Simeji 独自の統計レポート送信を止めます。キーボード・変換・同期などの本体機能には触りません |
| Hide point entry | ポイントストアやキャンペーンへの入口（ポイント導線）を表示しません |
| Disable review prompts | Google Play の星評価依頼（独自ダイアログと Play 内レビュー依頼の両方）を出さないようにします |
| Hide AI assistant icon | キーボード右上に表示されるロボット（AIアシスタント）のアイコンと、その吹き出しメッセージを表示しません。アイコンがあった一番上の帯も畳むので、空の帯は残りません。入力・変換には影響しません |
| Spoof VIP | 端末内のVIP判定だけを有効にします。ツールバーのカスタマイズや、自作きせかえのVIPパーツなど、端末内で完結する機能が使えるようになります（初期値はオフ） |
| Add in-app settings screen | Simeji の設定タブの一番下に「Ayufaパッチ設定」の入口を追加します。各パッチの切り替えにはこのパッチが必要です |

#### Simeji の注意点

- **対象は 20.7 のみ（23.7 には当てないでください）**：23.7 の変換エンジンはネイティブコード側でアプリの署名を検証するようになっており、再署名されたAPKでは変換エンジンが初期化されず、変換候補が一切出なくなります（パッチなしの再ビルドでも再現することを確認済み）。20.7 はその検証が入る前の版で、単一APKで配布されていたため、そのまま当てられます
- **入れ替えが必要です**：署名が変わるため、Play版が入っている場合は先にアンインストールしてください（ユーザー辞書や設定は引き継がれません）
- **設定の場所**：「Add in-app settings screen」を当てると、Simeji アプリ下部の「設定」タブの一番下に「Ayufaパッチ設定」が追加されます。初期値は、VIP偽装以外すべてオンです
- **Google アカウントでログインできません**：再署名されたアプリでは、Google ログインの署名照合（パッケージ名＋証明書）が通らないためです。パッチでは回避できません。Baidu アカウントの ID／パスワードでのログインは影響を受けません
- **VIP偽装の限界**：VIP特典のうちサーバー側で管理されているもの（クラウドきせかえ・バックアップ・翻訳・AI枠など）は、偽装しても使えません

## 本家との関係

- 本家のリポジトリには、プルリクエスト・Issue・コメントなどを一切しません。見るだけ（読み取り専用）で使わせてもらっています
- 本家のパッチ自体の不具合は、ここでは直せません。このリポジトリの Issue は、この独自パッチについてのものだけにしてください
- ライセンスやクレジットは本家に従います（GPL-3.0）

## 本家の更新についていく仕組み

- 本家のパッチャーなどの依存更新は、Dependabot が定期的に知らせてくれます
- 本家の対応 YouTube バージョンが変わったら、GitHub Actions（upstream-watch）が検知して、このリポジトリの中にだけ Issue を立てます。本家側には何も書き込みません
- 詳しくは [本家への追従方法](docs/UPSTREAM.md) に書いてあります
- リリースの署名はしていません（署名鍵は未設定です）

## 🩹 パッチ一覧

<!-- PATCHES_START EXPANDED -->
> **[v1.3.0-dev.8](https://github.com/Ayufa/ayufa-patches/releases/tag/v1.3.0-dev.8)**&nbsp;&nbsp;•&nbsp;&nbsp;`dev`&nbsp;&nbsp;•&nbsp;&nbsp;13 patches total
<details open>
<summary>📦 Simeji&nbsp;&nbsp;•&nbsp;&nbsp;11 patches</summary>
<br>

**Package name:** `com.adamrocker.android.input.simeji`

**🎯 Supported versions:**

| 20.7 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Add in-app settings screen](#add-in-app-settings-screen) | Simeji の設定タブの一覧に「Ayufaパッチ設定」の入口を追加します。各 Simeji パッチの有効・無効や VIP偽装を、再パッチなしでアプリ内から切り替えられるようになります。他の Simeji パッチを切り替えるには、このパッチの適用が必要です（未適用の場合は各パッチの初期値どおりに動作します）。 |  |
| [Block VIP guide screen](#block-vip-guide-screen) | VIPの案内画面を開かないようにします。注意: この画面はアプリ内のサブスクリプション購入画面も兼ねているため、ブロック中はアプリ内から契約できなくなります。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [Disable analytics reporting](#disable-analytics-reporting) | Simeji の利用状況イベント送信を止めます。AppsFlyer 向けの入口と、自社統計の送信処理が、アプリ内設定（Ayufaパッチ設定）のトグルがオンの間だけ無効になります。統計モジュールの初期化には触りません（キーボード表示時に設定オブジェクトを参照するため、潰すとキーボードが落ちます）。 |  |
| [Disable push popups](#disable-push-popups) | プッシュキャンペーンのポップアップダイアログを表示しなくします。通常の通知には影響しません。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [Disable review prompts](#disable-review-prompts) | Google Play の評価依頼を無効化します。Simeji 独自のガイドダイアログと、Play のアプリ内レビューの両方が対象です。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [Hide AI assistant icon](#hide-ai-assistant-icon) | キーボード右上に表示されるロボット（AIアシスタント）のアイコンと吹き出しメッセージを非表示にします。アイコンがあった一番上の帯も畳むので、空の帯は残りません。入力・変換には影響しません。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [Hide ads](#hide-ads) | Simeji の広告（候補欄の広告、ストア入口の全画面広告、Webキャンペーンのポップアップ）を非表示にします。リワード広告もブロックします。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [Hide point entry](#hide-point-entry) | ポイントストアやキャンペーンへの入口となる Simejiポイントの入口を非表示にします。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [Hide promotions](#hide-promotions) | 全画面のプロモーション画面（キャンペーンやアップセルのページ）を開かないようにします。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [Skip subscription retention screen](#skip-subscription-retention-screen) | VIPサブスクリプション解約時に表示される引き止め画面をスキップし、そのまま解約フローへ進みます。有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替えられます。 |  |
| [Spoof VIP](#spoof-vip) | 端末内のVIP判定を有効化します。ツールバーのカスタマイズや自作きせかえのVIPパーツなど、端末内で完結する機能が使えるようになります。クラウドきせかえ・クラウド定型文・AIチャットなど、サーバー側で判定される特典は使えません。有効・無効はアプリ内設定（Ayufaパッチ設定）で切り替えます（初期値はオフです）。 |  |

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

## 自分でビルドする場合

- `./gradlew buildAndroid` を実行すると、`patches/build/libs/` に `.mpp` ファイルができます
- できたファイルは Morphe Desktop などから、他のパッチバンドルと同じように使えます
- 開発は `dev` ブランチで行い、`main` にマージすると安定版のリリースが作られます

## ライセンス

GPL-3.0（[LICENSE](LICENSE)）。土台は本家の [Morphe Patches テンプレート](https://github.com/MorpheApp/morphe-patches-template)です。[NOTICE](NOTICE) もあわせてご覧ください。
