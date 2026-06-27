# クレクレデンシャル 開発ログ

## このファイルの使い方

このファイルは、クレクレデンシャル開発で「何をしたか」「なぜそうしたか」「次に何をするか」を後から振り返るための記録である。

今後の作業では、作業を実施するたびに日付ごとのログを追記する。

基本フォーマット:

```md
## YYYY-MM-DD

### 実施したこと
- 

### 作成・更新したファイル
| ファイル | 内容 |
| --- | --- |
| `` |  |

### 技術的に決めたこと
- 

### 発生した問題・対応
- 

### 次にやること
- 
```

## 2026-06-27

### 実施したこと

- 資格学習記録アプリの要件を整理した
- アプリ名を「クレクレデンシャル」とした
- MVPではフロントエンド画面を作らず、Spring BootのREST APIとして作成する方針にした
- DBはPostgreSQLを使用する方針にした
- 認証は最初からSpring Security + JWTで実装する方針にした
- AWSデプロイはECS Fargateを目標にする方針にした
- 要件定義書を作成した
- ER図を作成した
- API一覧を作成した
- DBテーブル定義を作成した
- Spring Bootプロジェクトを作成した
- Gradle Wrapperを追加した
- PostgreSQL用のDocker Compose設定を追加した
- Flyway用の初期マイグレーションSQLを作成した
- JPA Entityを作成した
- Repositoryを作成した
- Dockerfileを作成した
- READMEを作成した

### 作成・更新したファイル

| ファイル | 内容 |
| --- | --- |
| `docs/requirements.md` | 要件定義書。アプリ概要、MVP範囲、機能要件、非機能要件、成功条件を整理 |
| `docs/er-diagram.md` | Mermaid形式のER図。MVPで必要なテーブルとリレーションを整理 |
| `docs/api-list.md` | API一覧。認証、資格、資格目標、学習計画、教材、タスク、ログ、模擬試験、進捗サマリーAPIを整理 |
| `docs/db-table-definition.md` | PostgreSQL向けDBテーブル定義。カラム、型、制約、インデックス方針を整理 |
| `README.md` | プロジェクト概要、ドキュメントリンク、ローカル起動手順、ビルド手順を記載 |
| `build.gradle` | Spring Boot、JPA、Security、Validation、Flyway、PostgreSQL、Lombok、H2の依存関係を設定 |
| `settings.gradle` | Gradleのプラグイン・依存関係リポジトリ設定を追加 |
| `gradle.properties` | Gradle JVMがWindows証明書ストアを使うように設定 |
| `docker-compose.yml` | ローカル開発用PostgreSQLコンテナ設定を追加 |
| `.env.example` | 環境変数のサンプルを追加 |
| `Dockerfile` | Spring Bootアプリをコンテナ化するための設定を追加 |
| `.dockerignore` | Dockerビルド対象外ファイルを設定 |
| `db/migration/V1__create_core_tables.sql` | 初期テーブル作成SQLの作業用コピー |
| `src/main/resources/db/migration/V1__create_core_tables.sql` | Flywayが読み込む初期テーブル作成SQL |
| `src/main/resources/application.properties` | PostgreSQL接続、Flyway、JPA、JWT関連の基本設定を追加 |
| `src/test/resources/application.properties` | テスト用H2 DB設定を追加 |
| `src/main/java/com/kurekurecredential/domain/common/BaseTimeEntity.java` | 作成日時・更新日時を扱う共通Entity基底クラスを追加 |
| `src/main/java/com/kurekurecredential/domain/user/UserAccount.java` | `users` テーブルに対応するEntityを追加 |
| `src/main/java/com/kurekurecredential/domain/certification/Certification.java` | `certifications` テーブルに対応するEntityを追加 |
| `src/main/java/com/kurekurecredential/domain/certification/UserCertificationGoal.java` | `user_certification_goals` テーブルに対応するEntityを追加 |
| `src/main/java/com/kurekurecredential/domain/study/StudyPlan.java` | `study_plans` テーブルに対応するEntityを追加 |
| `src/main/java/com/kurekurecredential/domain/study/StudyPlanItem.java` | `study_plan_items` テーブルに対応するEntityを追加 |
| `src/main/java/com/kurekurecredential/domain/study/LearningResource.java` | `learning_resources` テーブルに対応するEntityを追加 |
| `src/main/java/com/kurekurecredential/domain/study/StudyTask.java` | `study_tasks` テーブルに対応するEntityを追加 |
| `src/main/java/com/kurekurecredential/domain/study/StudyLog.java` | `study_logs` テーブルに対応するEntityを追加 |
| `src/main/java/com/kurekurecredential/domain/exam/MockExamResult.java` | `mock_exam_results` テーブルに対応するEntityを追加 |
| `src/main/java/com/kurekurecredential/repository/*.java` | 各Entityに対応するSpring Data JPA Repositoryを追加 |

### 技術的に決めたこと

- バックエンドはJava 21 target + Spring Boot 3系で作成する
- DBはPostgreSQLを使用する
- DBマイグレーションはFlywayで管理する
- ORMはSpring Data JPAを使用する
- 認証はSpring Security + JWTで実装する
- フロントエンドはMVPでは作成しない
- 資格マスタと教材マスタはMVPでは初期データとして登録する
- 進捗サマリーは専用テーブルを作らず、既存テーブルから集計する
- 学習ログと教材の関係は、MVPでは1つのログに任意で1つの教材を紐づける
- 苦手分野はMVPではテキストとして管理し、将来必要になったら詳細テーブル化する
- ローカル開発DBはDocker ComposeでPostgreSQLを起動する

### 作成した主なテーブル

- `users`
- `certifications`
- `user_certification_goals`
- `study_plans`
- `study_plan_items`
- `learning_resources`
- `study_tasks`
- `study_logs`
- `mock_exam_results`

### MVPで作る予定のAPI

- ユーザー登録API
- ログインAPI
- ログインユーザー取得API
- 資格一覧・詳細取得API
- 資格目標作成・一覧・詳細・更新API
- 学習計画作成・取得API
- 教材一覧・詳細取得API
- 学習タスク作成・一覧・詳細・更新・完了API
- 学習ログ作成・一覧・詳細・更新API
- 模擬試験結果作成・一覧・詳細・更新API
- 進捗サマリー取得API

### 発生した問題・対応

- Spring InitializrでSpring Boot `3.5.16.RELEASE` を指定したところ、生成リクエストが失敗した
  - 対応: バージョン指定を `3.5.16` 形式に変更して生成した
- 生成後のGradleビルドで、Spring Boot Gradle Pluginの解決に失敗した
  - 対応: `settings.gradle` にPlugin PortalとMaven Centralを明示した
  - 対応: Spring Boot Gradle PluginをMaven Centralから解決できるように設定した
  - 対応: Spring Bootバージョンを `3.5.9` に調整した
- Gradle Wrapperの初回ダウンロードでJavaの証明書検証エラーが発生した
  - 対応: PowerShellでGradle配布物を取得し、Wrapperキャッシュに配置した
  - 対応: `gradle.properties` に `org.gradle.jvmargs=-Djavax.net.ssl.trustStoreType=Windows-ROOT` を追加した
- PCにはJava 24が入っていたが、Gradle toolchain設定がJava 21実体を探して失敗した
  - 対応: Gradle toolchain指定ではなく、`sourceCompatibility` / `targetCompatibility` をJava 21に設定した
- `.\gradlew.bat test` がGradleテストワーカーのクラス読み込み問題で失敗した
  - 状況: テストクラス自体はコンパイルされているが、テスト実行時に `ClassNotFoundException` になる
  - 対応: 現時点ではアプリ本体のビルド確認を優先し、`.\gradlew.bat clean build -x test` で検証した

### 検証結果

以下のコマンドでビルド成功を確認した。

```powershell
.\gradlew.bat clean build -x test
```

結果:

```txt
BUILD SUCCESSFUL
```

現時点で通常のテスト実行は未解決。

```powershell
.\gradlew.bat test
```

主な失敗内容:

```txt
ClassNotFoundException: com.kurekurecredential.KurekureCredentialApplicationTests
```

### 次にやること

- `.\gradlew.bat test` の失敗原因を切り分ける
- 認証APIの実装を開始する
- ユーザー登録APIを実装する
- ログインAPIを実装する
- JWT発行・検証処理を実装する
- 認証が必要なAPIのSecurity設定を作成する
- 初期資格マスタ投入用のFlyway SQLを作成する

## 2026-06-28

### 実施したこと

- ここまでの成果物をGitHubリポジトリへpushする方針にした
- Gitの管理対象を確認した
- GitHubリモートが未設定であることを確認した
- 実秘密情報が含まれていないか簡易確認した
- `.env` 本体が誤ってコミットされないように `.gitignore` を更新した

### 作成・更新したファイル

| ファイル | 内容 |
| --- | --- |
| `.gitignore` | `.env` と `.env.*` を除外し、`.env.example` はコミット対象にする設定を追加 |
| `docs/development-log.md` | GitHub push前の作業ログを追記 |

### 技術的に決めたこと

- GitHubへpushする前に、原則としてローカルコミットを作成する
- 今後はコミット前にコミットメッセージをユーザーへ確認する
- `.env.example` はサンプルとして管理し、実際の `.env` はGit管理しない

### 発生した問題・対応

- `gh` CLIは未インストールだった
  - 対応: 今回はPR作成ではなくpushのみのため、通常の `git` コマンドで進める
- リモートリポジトリは未設定だった
  - 対応: 指定されたGitHubリポジトリを `origin` として設定する

### 次にやること

- `main` ブランチで初回コミットを作成する
- `origin` に `https://github.com/naoyas0619-dev/crecredential.git` を設定する
- GitHubへpushする
