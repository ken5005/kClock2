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
- [x] v0.6: clamp化／ホバーで年込みフル表示(tooltip)／実行可能jar化＋javaw起動／最前面強化B案
- [ ] 次フェーズ: README作成 / 配色・サイズの最終微調整  ← 今ここ

## 設定ファイル（v0.5〜）
- 保存先 **`%APPDATA%\kClock\config.properties`**（`.properties`形式）。**`Config` クラスが load/save/デフォルトを集約**。旧 static final 定数は「デフォルト値」扱いに格下げ。
- 項目: `window.width/height/x/y` `font.size` `font.family` `text.color(#RRGGBB)` `bg.color`。各値は欠損/不正ならデフォルトにフォールバック（1個壊れても落とさない）。
- 保存タイミング: 終了時 `addShutdownHook`（System.exit経由でも走る・I/Oのみ＝EDT非依存）＋ 設定ダイアログの OK/Apply。窓位置はドラッグ完了(mouseReleased)で記録。
- メモ: `#` は `.properties` 上で `\#` にエスケープ保存される＝**正常**（store↔load対称）。Settingsのフォント一覧は OS の"全ユーザー登録"フォントのみ（ユーザー単位インストールは出ないことがある＝「すべてのユーザーにインストール」で出る）。
- 罠: `SpinnerNumberModel` は `min<=value<=max` を外れると即例外。**v0.6 で Spinner 生成時に `clamp(config値,min,max)` を噛ませて保険済み**（範囲外を手書きしても Settings が落ちない）。リポジトリ圏外なのでコミットされない。

## 表示まわり（v0.6で追加）
- **clamp**: `clamp(int v,int min,int max)` ヘルパ。fontSize/width/height の各 Spinner 生成で噛ませ、手書きconfigが範囲外でもダイアログが落ちない保険。
- **tooltip**: ラベルにホバーで `yyyy年M月d日(E) HH:mm:ss`（年込みフル）を表示。毎秒Timerで `setToolTipText`。ToolTipフォントは UI構築前に `UIManager.put("ToolTip.font", new Font("Monospaced",PLAIN,18))` を1回。
- **最前面強化B案**: `setFocusableWindowState(false)` ＋ `setAutoRequestFocus(false)` ＋ 別 `new Timer(2000, e->frame.toFront())`。**※非フォーカス化が必須**——入れないと2秒ごとに他アプリのフォーカスを奪う。Windowsのtopmost再ソートは完全制御不可なのでTimerは保険。

## ビルド・起動（v0.6〜）
- kClockは**外部依存ゼロ＝素のjarでOK**（shadowJar不要）。`build.gradle` に `jar { manifest { attributes 'Main-Class': 'ken5005.kclock.Main' } }`。
- `gradlew jar` → `build/libs/kClock2-1.0-SNAPSHOT.jar`。
- GUI起動は **`javaw`**（黒窓なし）。**javaw.exe への .lnk ショートカット**が定番（`.vbs`は非推奨）。
- ログオン自動起動: `.lnk` を `shell:startup`（`%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup`）に置く。

## 作業ルール（重要）
- **分業**: 考える作業（コード生成・調査）＝Claude、単純な手作業（git/cd/copy）＝自分が外側PowerShellで手打ち。
- **auto-accept は OFF維持**。編集/実行は毎回 diff を IntelliJ 差分ビューアでレビューしてから承認。
- **Claude Code のモデルは `/model sonnet`**（設計はチャット側Opusで詰め、実装はClaude Code Sonnet。auto-accept OFF＋diffレビューで品質担保）。opusplanは「Claude Code内でPlan modeを使う人」向けで本構成では出番薄い。
- **v0.2以降はブランチ→PR練習**: `git switch -c feature/xxx` → 実装 → commit → `git push -u origin feature/xxx` → PR作成 → 自分でレビュー → mainマージ → `git switch main && git pull` → `git branch -d feature/xxx`。
- Swing更新は EDT 安全に: `javax.swing.Timer` + `SwingUtilities.invokeLater`。別スレッド＋sleepでUI直叩きはしない。
- **git alias（全マシン導入済み）**: `st`=status -sb / `sw`=switch / `aa`=add -A / `cm`=commit -m / `br`=branch / `lg`=log --oneline --graph --decorate -20。軽微変更（CLAUDE.md更新等）は main 直 push でOK。

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
