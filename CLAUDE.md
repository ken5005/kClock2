# CLAUDE.md — kClock2

このファイルはプロジェクト直下に置く永続メモ。Claude Code が毎回 context に読む。

## このプロジェクト
- 自作デスクトップ時計。常に最前面に浮く小さな窓に「日付＋曜日＋秒つき時刻」を大きく表示。
- 真の目的は **Claude Code と GitHub の練習**。Windows更新で壊れない自分専用ツールを作る。
- GitHub（公開）: https://github.com/ken5005/kClock2

## 技術スタック（確定）
- Java + Swing（`setAlwaysOnTop(true)` / 枠なし窓 / ドラッグ移動が標準で済む）
- Gradle（Groovy DSL = `build.gradle`）
- JDK: java 18.0.1
- パッケージ: `ken5005.kclock`（`src/main/java/ken5005/kclock/` に `Main.java` ＋ `Config.java`）

## マイルストーン
- [x] v0.1: `HH:mm:ss` を1秒ごと更新表示（commit 6268b00）
- [x] v0.2: 最前面固定＋枠なし＋ドラッグ移動（＋右クリックExit）
- [x] v0.3: 日付＋曜日＋秒つき時刻を横一列（`HH:mm:ss M/d(E)` 日本語曜日）
- [x] v0.4: 窓サイズ固定＋定数化（サイズ/フォント/色）
- [x] v0.5: 設定の永続化＋設定ダイアログ（`%APPDATA%\kClock\config.properties`、Config集約、右クリックSettings…、窓位置記憶）
- [ ] 次フェーズ: README / ホバーで年など詳細表示 / 配色・サイズの最終微調整  ← 今ここ

## 設定ファイル（v0.5〜）
- 保存先 **`%APPDATA%\kClock\config.properties`**（`.properties`形式）。**`Config` クラスが load/save/デフォルトを集約**。旧 static final 定数は「デフォルト値」扱いに格下げ。
- 項目: `window.width/height/x/y` `font.size` `font.family` `text.color(#RRGGBB)` `bg.color`。各値は欠損/不正ならデフォルトにフォールバック（1個壊れても落とさない）。
- 保存タイミング: 終了時 `addShutdownHook`（System.exit経由でも走る・I/Oのみ＝EDT非依存）＋ 設定ダイアログの OK/Apply。窓位置はドラッグ完了(mouseReleased)で記録。
- メモ: `#` は `.properties` 上で `\#` にエスケープ保存される＝**正常**（store↔load対称）。Settingsのフォント一覧は OS の"全ユーザー登録"フォントのみ（ユーザー単位インストールは出ないことがある＝「すべてのユーザーにインストール」で出る）。
- 罠: `SpinnerNumberModel` は `min<=value<=max` を外れると即例外。config.properties に範囲外を手書きすると Settings で落ちうる（将来 clamp で保険可）。リポジトリ圏外なのでコミットされない。

## 作業ルール（重要）
- **分業**: 考える作業（コード生成・調査）＝Claude、単純な手作業（git/cd/copy）＝自分が外側PowerShellで手打ち。
- **auto-accept は OFF維持**。編集/実行は毎回 diff を IntelliJ 差分ビューアでレビューしてから承認。
- **Claude Code のモデルは `/model sonnet`**（設計はチャット側Opusで詰め、実装はClaude Code Sonnet。auto-accept OFF＋diffレビューで品質担保）。opusplanは「Claude Code内でPlan modeを使う人」向けで本構成では出番薄い。
- **v0.2以降はブランチ→PR練習**: `git switch -c feature/xxx` → 実装 → commit → `git push -u origin feature/xxx` → PR作成 → 自分でレビュー → mainマージ → `git switch main && git pull` → `git branch -d feature/xxx`。
- Swing更新は EDT 安全に: `javax.swing.Timer` + `SwingUtilities.invokeLater`。別スレッド＋sleepでUI直叩きはしない。

## IntelliJ お作法
- **初回の実行は必ず main メソッド横の gutter緑▶ →「Run 'クラス名.main()'」から**。これで名前付きRun構成が自動生成され、設定（multiple instances 等）が効く。
- ドロップダウンが **`Current File`** のままRunしないこと。Current Fileは一時実行モードで、構成が保存されず設定も持てない（→ Run連打で旧プロセスが閉じない等の謎挙動の元）。名前（`Main` 等）が出てる構成でRunするのが正。
- 「Run押したら前のプロセスを止めて再起動」は名前付き構成の **「Allow multiple instances」OFF**（デフォルトOFF）で効く。Current Fileだと効かない。

## 環境のハマりどころ（触ると死ぬ系）
- **HOME = `C:\tools2\etc\home`**（大昔のEmacs/Mule用設定。`.emacs.d` 等が現役）。**絶対に触らない**。
- インストーラ系は Unix流に `$HOME\.local\bin`（= `C:\tools2\etc\home\.local\bin`）に入れに行く。Claude Code本体もここ（現 2.1.186）。
- PATH順は `C:\tools2\etc\home\.local\bin` が `C:\Users\ken\.local\bin` より先。逆だと「updateは成功と言うのに古い版が起動」する二重戸籍事故になる。
- Claude Code 内側シェルは PowerShell化済み（`CLAUDE_CODE_USE_POWERSHELL_TOOL=1`）。
- git push の認証は gh credential helper 経由 → `gh` がPATHに見えるターミナルでないとpushできない。
- ※設定ファイルは `%APPDATA%`（=`C:\Users\ken\AppData\Roaming`）配下なので **HOME二重戸籍とは無関係**（APPDATA/USERPROFILE系を見ている）。
