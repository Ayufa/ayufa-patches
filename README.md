<p align="center">
  <a href="https://morphe.software/add-source?github=Ayufa/youtube-custom-patches">
    <img src=".github/assets/add-to-morphe.svg" alt="Add to Morphe" height="48">
  </a>
</p>

# Ayufa YouTube Patches（独自パッチ）

[Morphe](https://morphe.software) 用の、Ayufa 個人の YouTube 独自パッチです（非公式。本家 Morphe とは無関係の個人リポジトリです）。
本家 [MorpheApp/morphe-patches](https://github.com/MorpheApp/morphe-patches) のパッチと**併用**することを前提にしています。

## パッチ

### 1. ライブ遅延キーパー（Live latency keeper）

- 対象: **YouTube 20.26.46**（Android 8.0 で動作する最後の YouTube。本家 Morphe の対応表でも「Last version for Android 8.0」とされている版）
- ライブ配信を長時間つけっぱなしにすると再生位置がライブの先端（ライブエッジ）から遅れていく問題への対策として、再生中に定期的に遅延をチェックし、しきい値以上遅れていたら自動でライブエッジへ進めます。
- パッチ適用時のオプションで、チェック間隔としきい値を変更できます。

### 2. ライブチャットの不要表示を隠す（Hide live chat clutter）

- 対象: **YouTube 21.30.216**（本家 Morphe の対応表で最新の安定ターゲット。最新の Android 向け）
- ライブチャット内の固定コメント（ピン留めメッセージ）、アンケート（投票カード）など、視聴の邪魔になる表示を非表示にします。
- 項目ごとにパッチ適用時のオプションで ON / OFF できます。

## 使い方

上のボタンから Morphe にこのリポジトリをパッチソースとして追加し、本家パッチと一緒に使いたいパッチを選んで適用してください。

- Android 8.0 の端末（例: SOV34）では YouTube **20.26.46** の APK に対して使います。
- 最新の Android の端末では YouTube **21.30.216** の APK に対して使います。
- 対象バージョン以外ではフィンガープリントが一致せず適用できません。これは仕様です（バージョンごとに検証しています）。

## 本家への配慮

- 本家リポジトリ（MorpheApp/*、および Morphe 本体）への **プルリクエスト・Issue・マージは行いません**。本家は読み取り専用の参照のみです。
- パッチ本体の不具合は本家の領分です。このリポジトリの Issue は、この独自パッチに関するものにお使いください。

## 本家の更新への追従

- パッチャー／テンプレートの依存バージョンは Dependabot が定期的に更新します。
- 本家 `MorpheApp/morphe-patches` の対応 YouTube バージョン表（`Constants.kt`）が変わったら、GitHub Actions の `upstream-watch` ワークフローが検知して、このリポジトリ内に Issue を自動で立てます（本家には何も書き込みません）。詳しくは [docs/UPSTREAM.md](docs/UPSTREAM.md) を参照してください。
- リリースは署名なしです（署名鍵は未設定）。

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->
<!-- PATCHES_END -->

## 🛠️ Building locally

- Run `./gradlew buildAndroid`
- The built patches .mpp file is found in `patches/build/libs/patches-*.mpp`
- Patch the mpp file using [Morphe Desktop](https://github.com/MorpheApp/morphe-desktop) like any other patch bundle.

開発は `dev` ブランチで行い、`main` へのマージで安定版リリースが作られます（semantic-release）。

## 📜 License

Ayufa YouTube Patches are licensed under the [GNU General Public License v3.0](LICENSE).
Based on the [Morphe Patches template](https://github.com/MorpheApp/morphe-patches-template). See [NOTICE](NOTICE).
