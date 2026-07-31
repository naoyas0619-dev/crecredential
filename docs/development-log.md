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

## 2026-06-29

### 実施したこと

- `.\gradlew.bat test` の失敗原因を調査した
- 疑わしい原因を以下の順で切り分けた
  - Java 24の影響
  - Gradleテストクラスパスの問題
  - OneDrive + 日本語パスの影響
  - Gradle Wrapper / キャッシュ不整合

### 調査結果

- 直接原因は、プロジェクトパスに日本語文字が含まれていることだった
- 元のパス `C:\Users\naoya\OneDrive\ドキュメント\AWS_Springboot` では `gradlew test` が失敗した
- ASCIIのみのパス `C:\tmp\AWS_Springboot_ascii` では `gradlew test` が成功した
- 日本語を含むパス `C:\tmp\日本語パス\AWS_Springboot` では同じ `ClassNotFoundException` で失敗した
- OneDrive配下でもASCIIのみのパス `C:\Users\naoya\OneDrive\AWS_Springboot_ascii` では成功した
- そのため、OneDrive自体ではなく、日本語パスが原因と判断した

### 原因ではないと判断したもの

- Java 24
  - JDK 21で実行しても同じ `ClassNotFoundException` が発生したため
- Gradleテストクラスパス不足
  - `build/classes/java/test` にテストクラスが生成されていたため
  - Gradleテストワーカーのクラスパスにも `build/classes/java/test` が含まれていたため
  - 同じクラスパスを使って手動でテストクラスをロードできたため
- Gradle Wrapper / キャッシュ不整合
  - 同じWrapperと同じGradleキャッシュを使っても、ASCIIパスでは成功し、日本語パスでは失敗したため
  - 別のGradleホームを使っても、ASCIIパスでは成功し、日本語パスでは失敗したため

### 発生しているエラー

```txt
ClassNotFoundException: com.kurekurecredential.KurekureCredentialApplicationTests
```

### 次にやること

- 開発作業用のディレクトリをASCIIのみのパスへ移すか検討する
- 例: `C:\Users\naoya\OneDrive\AWS_Springboot` または `C:\dev\AWS_Springboot`
- 移動後に `.\gradlew.bat clean test` が成功することを確認する

### 追加対応

- 日本語パスによる `gradlew test` 失敗を避けるため、作業ディレクトリを `C:\dev\AWS_Springboot` に移す方針にした
- 移動先では `.\gradlew.bat clean test` を実行して、テストが成功することを確認する

### 認証API実装の作業開始

- `C:\dev\AWS_Springboot` を今後の作業ディレクトリとして使用する
- 認証APIの実装に着手する
- 実装対象はユーザー登録、ログイン、ログインユーザー取得とする
- JWTライブラリは `jjwt` を使用する
- Spring Securityはステートレス構成にする

### 認証API実装で実施したこと

- `jjwt` の依存関係を追加した
- Spring Securityをステートレス構成にした
- `/api/auth/register` と `/api/auth/login` を未認証で利用できるようにした
- その他のAPIは認証必須にした
- `BCryptPasswordEncoder` によるパスワードハッシュ化を追加した
- JWT発行・検証用の `JwtService` を追加した
- JWTを `Authorization: Bearer ...` から読み取るフィルタを追加した
- ユーザー登録APIを実装した
- ログインAPIを実装した
- ログインユーザー取得APIを実装した
- バリデーションエラー、メールアドレス重複、ログイン失敗時のエラーレスポンスを追加した
- 認証APIの結合テストを追加した

### 作成・更新したファイル

| ファイル | 内容 |
| --- | --- |
| `build.gradle` | JWTライブラリ `jjwt` を追加 |
| `src/main/java/com/kurekurecredential/config/SecurityConfig.java` | Spring Security設定を追加 |
| `src/main/java/com/kurekurecredential/security/AuthUserDetails.java` | 認証ユーザー情報を表す `UserDetails` 実装を追加 |
| `src/main/java/com/kurekurecredential/security/AuthUserDetailsService.java` | メールアドレスでユーザーを取得する `UserDetailsService` を追加 |
| `src/main/java/com/kurekurecredential/security/JwtService.java` | JWT発行・検証処理を追加 |
| `src/main/java/com/kurekurecredential/security/JwtAuthenticationFilter.java` | Bearerトークン認証フィルタを追加 |
| `src/main/java/com/kurekurecredential/service/auth/AuthService.java` | ユーザー登録、ログイン、ログインユーザー取得の業務処理を追加 |
| `src/main/java/com/kurekurecredential/web/auth/*.java` | 認証APIのControllerとリクエスト/レスポンスDTOを追加 |
| `src/main/java/com/kurekurecredential/web/common/*.java` | 共通エラーレスポンスと例外ハンドラを追加 |
| `src/test/java/com/kurekurecredential/web/auth/AuthControllerIntegrationTest.java` | 認証APIの結合テストを追加 |
| `src/test/java/com/kurekurecredential/web/auth/JsonTestHelper.java` | テスト用JSONヘルパーを追加 |
| `src/test/resources/application.properties` | テスト用JWT設定を追加 |

### 検証結果

以下のコマンドでテスト成功を確認した。

```powershell
.\gradlew.bat clean test --no-daemon
```

結果:

```txt
BUILD SUCCESSFUL
```

確認した主な内容:

- ユーザー登録が `201 Created` で成功する
- レスポンスにパスワードが含まれない
- 重複メールアドレスは `409 Conflict` になる
- 正しいメールアドレス・パスワードでログインできる
- ログイン成功時にJWTが返る
- JWT付きで `/api/auth/me` を取得できる
- パスワード誤りは `401 Unauthorized` になる
- 未認証の `/api/auth/me` は拒否される

### 発生した問題・対応

- 認証実装後、テスト起動時に `app.jwt.secret` が解決できず失敗した
  - 原因: `src/test/resources/application.properties` にJWT設定がなかった
  - 対応: テスト用の `app.jwt.secret` と `app.jwt.expiration-seconds` を追加した
  - 結果: `.\gradlew.bat clean test --no-daemon` が成功した

### 次にやること

- 初期資格マスタ投入用のFlyway SQLを作成する
- 資格一覧・詳細取得APIを実装する
- 認証済みAPIの所有者チェック方針をサービス層に反映していく

## 2026-07-31

### 初期資格マスタ投入と資格API実装

- Flywayの `V2` マイグレーションで初期資格マスタ6件を追加した
  - AWS Certified Solutions Architect - Associate
  - AWS Certified Developer - Associate
  - Oracle Certified Java Programmer, Silver SE 17
  - Oracle Certified Java Programmer, Gold SE 17
  - 基本情報技術者試験
  - 応用情報技術者試験
- 試験名、試験形式、合格基準、公式URLは2026年7月時点のAWS、Oracle、IPA公式情報を基準にした
- 推奨学習時間は学習計画を作成するためのアプリ独自の目安として設定した
- `GET /api/certifications` を実装した
- `keyword` による資格名・提供元の部分一致検索を実装した
- `GET /api/certifications/{certificationId}` を実装した
- 存在しない資格IDに対する共通の `404 Not Found` レスポンスを追加した
- 一覧は資格名の昇順で返すようにした

### 作成・更新した主なファイル

| ファイル | 内容 |
| --- | --- |
| `src/main/resources/db/migration/V2__insert_initial_certifications.sql` | 初期資格マスタ6件を追加 |
| `db/migration/V2__insert_initial_certifications.sql` | 管理用のFlyway SQLコピーを追加 |
| `src/main/java/com/kurekurecredential/repository/CertificationRepository.java` | 一覧・検索用クエリを追加 |
| `src/main/java/com/kurekurecredential/service/certification/CertificationService.java` | 資格一覧・詳細取得処理を追加 |
| `src/main/java/com/kurekurecredential/web/certification/*.java` | 資格APIのControllerとレスポンスDTOを追加 |
| `src/main/java/com/kurekurecredential/web/common/*.java` | 共通の404例外処理を追加 |
| `src/test/java/com/kurekurecredential/web/certification/CertificationControllerIntegrationTest.java` | 資格APIの結合テストを追加 |

### テスト対象

- 未認証では資格一覧を取得できない
- 認証済みユーザーは初期資格6件を取得できる
- 資格一覧の項目に詳細説明を含めない
- キーワードの前後空白を除去し、資格名・提供元で部分一致検索できる
- 認証済みユーザーは資格詳細を取得できる
- 存在しない資格IDは `404 Not Found` になる

### 検証結果

以下のコマンドで全11件のテストが成功した。

```powershell
.\gradlew.bat clean test --no-daemon
```

結果:

```txt
BUILD SUCCESSFUL
Tests: 11, Failures: 0, Errors: 0, Skipped: 0
```

- テストではH2のPostgreSQL互換モードを使用し、Flywayの `V1` と `V2` が適用されることを確認した
- Docker Desktop起動後、PostgreSQL 16.13のコンテナを起動して実DBでも確認した
- 実PostgreSQLの `flyway_schema_history` で `V1` と `V2` が成功していることを確認した
- 実PostgreSQLの `certifications` テーブルに初期資格6件が登録されていることを確認した
- 実PostgreSQLへ接続したアプリで、ユーザー登録・ログイン・JWT認証を行った
- 資格一覧6件、`AWS` キーワード検索2件、資格詳細1件をAPIから取得できることを確認した
- API確認後、確認用ユーザーを削除し、Spring Bootプロセスを停止した
- PostgreSQLコンテナは今後のローカル開発で利用できるように起動状態を維持した

### 次にやること

- 資格目標の作成・一覧・詳細・更新APIを実装する
- 認証ユーザー本人の資格目標だけを操作できる所有者チェックを実装する

### 資格目標API実装

- `POST /api/certification-goals` を実装した
- `GET /api/certification-goals` を実装した
- `status` による資格目標一覧の絞り込みを実装した
- `GET /api/certification-goals/{goalId}` を実装した
- `PUT /api/certification-goals/{goalId}` を実装した
- 資格目標一覧は目標試験日の昇順で返すようにした
- 資格は作成時に選択し、更新時には変更できない仕様にした
- 資格目標の終了には物理削除ではなく `CANCELED` ステータスを使用する方針にした
- JWTから取得したユーザーIDを使って所有者チェックを行うようにした
- 他ユーザーの資格目標の詳細取得・更新は `403 Forbidden` にした
- 存在しない資格・資格目標は `404 Not Found` にした
- 目標試験日が学習開始日より前の場合は `400 Bad Request` にした
- 週の学習時間は1時間以上に制限した
- JSONやEnum値が不正な場合の共通 `400 Bad Request` レスポンスを追加した

### 作成・更新した主なファイル

| ファイル | 内容 |
| --- | --- |
| `src/main/java/com/kurekurecredential/repository/UserCertificationGoalRepository.java` | 所有ユーザー・ステータス別の検索と資格の同時取得を追加 |
| `src/main/java/com/kurekurecredential/service/goal/CertificationGoalService.java` | 資格目標の作成・一覧・詳細・更新と所有者チェックを追加 |
| `src/main/java/com/kurekurecredential/web/goal/*.java` | Controllerとリクエスト・レスポンスDTOを追加 |
| `src/main/java/com/kurekurecredential/web/common/*.java` | 400・403の共通例外処理を追加 |
| `src/test/java/com/kurekurecredential/web/goal/CertificationGoalControllerIntegrationTest.java` | 資格目標APIの結合テストを追加 |
| `docs/api-list.md` | 更新APIと `CANCELED` 運用を追記し、削除APIを対象外に変更 |

### テスト対象

- 未認証では資格目標APIを利用できない
- 認証済みユーザーが資格目標を作成できる
- 週の学習時間が0の場合は拒否される
- 目標試験日が学習開始日より前の場合は拒否される
- 存在しない資格IDは `404 Not Found` になる
- 不正なEnum値は `400 Bad Request` になる
- 自分の資格目標だけを一覧取得できる
- ステータスで一覧を絞り込める
- 自分の資格目標を詳細取得・更新できる
- 他ユーザーの資格目標は詳細取得・更新できない
- 存在しない資格目標IDは `404 Not Found` になる

### 検証結果

以下のコマンドで全19件のテストが成功した。

```powershell
.\gradlew.bat clean test --no-daemon
```

結果:

```txt
BUILD SUCCESSFUL
Tests: 19, Failures: 0, Errors: 0, Skipped: 0
```

- PostgreSQL 16.13に接続したアプリを8081番ポートで起動した
- 実APIで資格目標の作成、状態絞り込み一覧、更新を確認した
- 更新後のステータス `PAUSED` と週の学習時間12時間が返ることを確認した
- 実DBの `user_certification_goals` に更新内容が保存されていることを確認した
- 別ユーザーによる詳細取得が `403 Forbidden` になることを確認した
- API確認後、確認用ユーザー2件を関連する資格目標ごと削除した
- 検証用Spring Bootプロセスを停止し、PostgreSQLコンテナは起動状態を維持した

### 発生した問題・対応

- 共通エラー処理に指定したSpringの例外クラスが現在の依存バージョンに存在せず、初回コンパイルが失敗した
  - 対応: 存在しない例外クラスを除外し、JSON不正とクエリパラメータ不正を処理する実在クラスだけに変更した
  - 結果: コンパイルと全19件のテストが成功した

### 次にやること

- 資格目標に紐づく学習計画の作成・一覧・詳細APIを実装する
- 学習計画でも資格目標の所有者チェックを適用する
