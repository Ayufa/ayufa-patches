<p align="center">
  <a href="https://morphe.software/add-source?github=Ayufa/ayufa-patches">
    <img src=".github/assets/add-to-morphe.svg" alt="Morphe に追加" height="48">
  </a>
</p>

# Ayufa パッチ

Morphe 用に個人的に作っている、いろいろなアプリ向けの独自パッチ集です。
公式の Morphe パッチではありません。Ayufa 個人のリポジトリで開発しています。

本家のパッチ（[MorpheApp/morphe-patches](https://github.com/MorpheApp/morphe-patches)）と一緒に使う想定です。
本家のパッチで満足できなかったところ、自分が欲しい機能だけを少し足す、という位置づけです。

現在は YouTube 向けと Simeji 向けのパッチが入っています。今後、他のアプリ向けのパッチもこのリポジトリに追加していきます。

## 入っているパッチ

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

対象はすべて Simeji **20.7** です。**最新の 23.7 には当てないでください**：23.7 の変換エンジンはネイティブコード側でアプリの署名を検証するようになっており、再署名されたAPKでは変換エンジンが初期化されず、変換候補が一切出なくなります（パッチなしの再ビルドでも再現することを確認済み）。20.7 はその検証が入る前の版で、単一APKで配布されていたため、そのまま当てられます。署名が変わるため、Play版が入っている場合は先にアンインストールが必要です（ユーザー辞書や設定は引き継がれません）。

| パッチ | 何をするか |
|---|---|
| 広告を隠す | 候補欄に出る広告、ストア入場時の全面広告、Webキャンペーンのポップアップを消します。広告の中心判定を止め、広告候補の取得・保存自体も止めます。リワード広告はオプションで追加ブロックできます（広告を見ると解放される機能も止まるため初期値はオフ） |
| 宣伝画面を出さない | 全画面のキャンペーン／宣伝アクティビティが開かないようにします |
| VIPガイド画面をブロック | VIPのガイド画面を開かないようにします。この画面はアプリ内課金の購入画面も兼ねているため、アプリ内からは契約できなくなります |
| プッシュのポップアップを無効化 | プッシュキャンペーンのダイアログ型ポップアップを表示しません（通常の通知は影響なし） |
| 解約時の引き止め画面をスキップ | VIP解約時の引き止め画面を飛ばして、解約手続きに直行します |
| 解析レポートを無効化 | AppsFlyer へのイベント送信と、Simeji 独自の統計レポート送信を止めます。キーボード・変換・同期などの本体機能には触りません |
| ポイント導線を隠す | ポイントストアやキャンペーンへの入口（ポイント導線）を表示しません |
| レビュー依頼を無効化 | Google Play の星評価依頼（独自ダイアログと Play 内レビュー依頼の両方）を出さないようにします |

なお、VIP特典そのもの（専用きせかえ・バックアップ・翻訳・AI枠など）はサーバー側で管理されているため、パッチでは解放できません。

## 使い方

1. 上のボタンから、このリポジトリを Morphe のパッチソースに追加します
2. いつもどおり本家のパッチと一緒に、使いたいパッチを選んで適用します

- ライブチャットのパッチは YouTube **21.30.216** の APK に、ライブ遅延キーパーは YouTube **20.26.46** の APK に当ててください
- Simeji のパッチは Simeji **20.7** に当ててください（23.7 以降は署名検証のため変換が使えなくなります）
- 対象以外のバージョンには当てられません（バージョンごとに動作を確認しているためです）

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
> **[v1.2.0](https://github.com/Ayufa/ayufa-patches/releases/tag/v1.2.0)**&nbsp;&nbsp;•&nbsp;&nbsp;`main`&nbsp;&nbsp;•&nbsp;&nbsp;10 patches total
<details open>
<summary>📦 Simeji&nbsp;&nbsp;•&nbsp;&nbsp;8 patches</summary>
<br>

**Package name:** `com.adamrocker.android.input.simeji`

**🎯 Supported versions:**

| 23.7 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Block VIP guide screen](#block-vip-guide-screen) | Prevents the VIP guide screen from opening. Note: this screen is also the in-app subscription purchase screen, so subscribing from inside the app becomes impossible. |  |
| [Disable analytics reporting](#disable-analytics-reporting) | Stops Simeji's event reporting: every AppsFlyer event entry point and the in-house statistic event dispatch become no-ops, so usage events are no longer sent. The statistic module initialization is deliberately left intact (the keyboard reads its config when the window is shown), and core keyboard, conversion and account features are not touched. |  |
| [Disable push popups](#disable-push-popups) | Prevents push campaign popup dialogs from appearing on screen. Normal notifications are not affected. |  |
| [Disable review prompts](#disable-review-prompts) | Disables the Google Play rating prompts: both Simeji's own guide dialog and the Play in-app review flow. |  |
| [Hide ads](#hide-ads) | Hides ads in Simeji: suggestion bar ads, store entry interstitials and web campaign popups. The central ad gate is forced off, and ad candidates are never fetched or stored. | • Also block rewarded ads |
| [Hide point entry](#hide-point-entry) | Hides the Simeji Point entry point, which leads to the point store and its campaign recommendations. |  |
| [Hide promotions](#hide-promotions) | Prevents the full-screen promotion activity from opening, so campaign and upsell pages never appear. |  |
| [Skip subscription retention screen](#skip-subscription-retention-screen) | Skips the retention screen shown during VIP subscription cancellation, going straight to the cancellation flow. |  |

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
