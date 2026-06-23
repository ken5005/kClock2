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
- パッケージ: `ken5005.kclock`（`src/main/java/ken5005/kclock/Main.java`）

## マイルストーン
- [x] v0.1: `HH:mm:ss` を1秒ごと更新表示（commit 6268b00, push済み）
- [ ] v0.2: 最前面固定＋枠なし＋ドラッグ移動  ← 今ここ
- [ ] v0.3: 日付＋曜日＋秒つき時刻を横一列に整形
- [ ] v0.4: フォントサイズ・色を設定可能に

## 作業ルール（重要）
- **分業**: 考える作業（コード生成・調査）＝Claude、単純な手作業（git/cd/copy）＝自分が外側PowerShellで手打ち。
- **auto-accept は OFF維持**。編集/実行は毎回 diff を IntelliJ 差分ビューアでレビューしてから承認。
- **v0.2以降はブランチ→PR練習**: `git switch -c feature/xxx` → 実装 → commit → `git push -u origin feature/xxx` → PR作成 → 自分でレビュー → mainマージ → `git switch main && git pull`。
- Swing更新は EDT 安全に: `javax.swing.Timer` + `SwingUtilities.invokeLater`。別スレッド＋sleepでUI直叩きはしない。

## 環境のハマりどころ（触ると死ぬ系）
- **HOME = `C:\tools2\etc\home`**（大昔のEmacs/Mule用設定。`.emacs.d` 等が現役）。**絶対に触らない**。
- インストーラ系は Unix流に `$HOME\.local\bin`（= `C:\tools2\etc\home\.local\bin`）に入れに行く。Claude Code本体もここ（現 2.1.186）。
- PATH順は `C:\tools2\etc\home\.local\bin` が `C:\Users\ken\.local\bin` より先。逆だと「updateは成功と言うのに古い版が起動」する二重戸籍事故になる。
- Claude Code 内側シェルは PowerShell化済み（`CLAUDE_CODE_USE_POWERSHELL_TOOL=1`）。
- git push の認証は gh credential helper 経由 → `gh` がPATHに見えるターミナルでないとpushできない。
