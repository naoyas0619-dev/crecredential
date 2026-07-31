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

### 学習計画API実装

- `POST /api/certification-goals/{goalId}/study-plans` を実装した
- `GET /api/certification-goals/{goalId}/study-plans` を実装した
- `GET /api/study-plans/{studyPlanId}` を実装した
- 学習計画一覧は開始日の昇順で返すようにした
- 学習計画詳細の週次項目は週番号の昇順で返すようにした
- 学習計画は資格目標の学習開始日から目標試験日までの範囲内に制限した
- 学習計画項目を1件以上必須にした
- 同一計画内の週番号重複を禁止した
- 学習計画項目の推奨期間を学習計画全体の期間内に制限した
- 資格目標と学習計画の所有者チェックを実装した
- 他ユーザーによる作成・一覧・詳細取得を `403 Forbidden` にした
- 存在しない資格目標・学習計画を `404 Not Found` にした
- 学習計画用テーブルはV1で作成済みのため、Flywayマイグレーションの追加は行わなかった

### 作成・更新した主なファイル

| ファイル | 内容 |
| --- | --- |
| `src/main/java/com/kurekurecredential/repository/StudyPlanRepository.java` | 開始日順の一覧取得と所有者情報の同時取得を追加 |
| `src/main/java/com/kurekurecredential/service/studyplan/StudyPlanService.java` | 学習計画の作成・一覧・詳細と入力・所有者チェックを追加 |
| `src/main/java/com/kurekurecredential/web/studyplan/*.java` | Controllerとリクエスト・レスポンスDTOを追加 |
| `src/test/java/com/kurekurecredential/web/studyplan/StudyPlanControllerIntegrationTest.java` | 学習計画APIの結合テストを追加 |
| `docs/api-list.md` | 学習計画の入力規則と一覧・詳細仕様を追記 |

### テスト対象

- 未認証では学習計画APIを利用できない
- 所有ユーザーが学習計画と週次項目を作成できる
- 作成結果の週次項目が週番号順になる
- 終了日が開始日より前の計画は拒否される
- 資格目標の学習期間外の計画は拒否される
- 計画期間外の推奨日を持つ項目は拒否される
- 項目が空の計画は拒否される
- 週番号が重複した計画は拒否される
- 所有ユーザーが計画を開始日順で一覧取得できる
- 所有ユーザーが計画詳細と週次項目を取得できる
- 他ユーザーは計画の作成・一覧・詳細取得ができない
- 存在しない資格目標・学習計画は `404 Not Found` になる

### 検証結果

以下のコマンドで全26件のテストが成功した。

```powershell
.\gradlew.bat clean test --no-daemon
```

結果:

```txt
BUILD SUCCESSFUL
Tests: 26, Failures: 0, Errors: 0, Skipped: 0
```

- PostgreSQL 16.13に接続したアプリを8081番ポートで起動した
- 実APIで資格目標と学習計画、週次項目2件を作成した
- 作成レスポンスの週次項目が週番号順になることを確認した
- 学習計画一覧1件と詳細の週次項目2件を取得できることを確認した
- 実DBの `study_plans` と `study_plan_items` に計画1件・項目2件が保存されることを確認した
- 別ユーザーによる学習計画詳細取得が `403 Forbidden` になることを確認した
- API確認後、確認用ユーザー2件を資格目標・学習計画・週次項目ごと削除した
- 検証用Spring Bootプロセスを停止し、PostgreSQLコンテナは起動状態を維持した

### 次にやること

- 初期教材マスタをFlywayで投入する
- 教材一覧・詳細取得APIを実装する

### 初期教材マスタ投入と教材API実装

- Flywayの `V3` マイグレーションで初期教材マスタ12件を追加した
- 初期資格6件に対して公式教材・試験資料を各2件登録した
- AWSは公式試験ガイドとAWS Skill Builderを登録した
- Java Silver・GoldはOracleの試験内容チェックリストとdev.javaを登録した
- 基本情報・応用情報はIPAのシラバスと公開問題・過去問題を登録した
- 初期段階では正確性と更新性を優先し、公式教材だけを登録した
- 書籍は出版年と評価を改めて確認し、別の初期データとして追加する方針にした
- `GET /api/learning-resources` を実装した
- `certificationId`、`resourceType`、`targetLevel` の組み合わせ検索を実装した
- `GET /api/learning-resources/{resourceId}` を実装した
- 一覧はおすすめ度の降順、同点の場合は教材タイトルの昇順で返すようにした
- 存在しない資格IDによる絞り込みと存在しない教材IDは `404 Not Found` にした
- 不正な教材種別・対象レベルは `400 Bad Request` にした

### 作成・更新した主なファイル

| ファイル | 内容 |
| --- | --- |
| `src/main/resources/db/migration/V3__insert_initial_learning_resources.sql` | 初期公式教材12件を追加 |
| `db/migration/V3__insert_initial_learning_resources.sql` | 管理用のFlyway SQLコピーを追加 |
| `src/main/java/com/kurekurecredential/repository/LearningResourceRepository.java` | 3条件を組み合わせた教材検索を追加 |
| `src/main/java/com/kurekurecredential/service/resource/LearningResourceService.java` | 教材一覧・詳細取得処理を追加 |
| `src/main/java/com/kurekurecredential/web/resource/*.java` | ControllerとレスポンスDTOを追加 |
| `src/test/java/com/kurekurecredential/web/resource/LearningResourceControllerIntegrationTest.java` | 教材APIの結合テストを追加 |
| `docs/api-list.md` | 初期教材方針と教材詳細レスポンスを追記 |

### テスト対象

- 未認証では教材APIを利用できない
- 認証済みユーザーが初期教材12件を取得できる
- 一覧レスポンスに詳細メモを含めない
- 資格IDで教材を2件に絞り込める
- 資格ID、教材種別、対象レベルを組み合わせて絞り込める
- 教材詳細で資格名、提供元、公式フラグ、メモを取得できる
- 存在しない資格IDによる絞り込みは `404 Not Found` になる
- 不正な教材種別は `400 Bad Request` になる
- 存在しない教材IDは `404 Not Found` になる

### 検証結果

以下のコマンドで全31件のテストが成功した。

```powershell
.\gradlew.bat clean test --no-daemon
```

結果:

```txt
BUILD SUCCESSFUL
Tests: 31, Failures: 0, Errors: 0, Skipped: 0
```

- PostgreSQL 16.13に `V3__insert_initial_learning_resources.sql` を適用した
- 実DBの `flyway_schema_history` でV3が成功していることを確認した
- 実DBの `learning_resources` に教材12件が登録されていることを確認した
- 実APIで教材一覧12件を取得できることを確認した
- 資格ID、`OFFICIAL_DOCUMENT`、`BEGINNER` の複合条件で1件に絞り込めることを確認した
- 教材詳細の公式フラグと提供元を確認した
- API確認後、確認用ユーザーを削除した
- 検証用Spring Bootプロセスを停止し、PostgreSQLコンテナは起動状態を維持した

### 次にやること

- 学習タスクの作成・一覧・詳細・更新・完了APIを実装する
- 学習タスクでも資格目標と学習計画項目の所有者整合性を確認する

### 学習タスクAPI実装

- `POST /api/certification-goals/{goalId}/study-tasks` を実装した
- `GET /api/study-tasks` を実装した
- `goalId`、`status`、`dueFrom`、`dueTo` の組み合わせ検索を実装した
- `GET /api/study-tasks/{taskId}` を実装した
- `PUT /api/study-tasks/{taskId}` を実装した
- `PATCH /api/study-tasks/{taskId}/complete` を実装した
- タスク作成時のステータスを `TODO` にした
- タスク完了時にステータスを `DONE` にして完了日時を設定するようにした
- 更新でステータスを `TODO` に戻した場合は完了日時をクリアするようにした
- 学習計画項目は任意指定とし、同じ資格目標に属する項目だけを関連付けられるようにした
- タスク期限を資格目標の学習期間内に制限した
- 一覧は期限の昇順で返すようにした
- タスク、資格目標、学習計画項目の所有者チェックを実装した
- 他ユーザーのタスク・計画項目へのアクセスを `403 Forbidden` にした
- 学習履歴との関連を維持するため、削除APIはMVP対象外にした
- 学習タスク用テーブルはV1で作成済みのため、Flywayマイグレーションの追加は行わなかった

### 作成・更新した主なファイル

| ファイル | 内容 |
| --- | --- |
| `src/main/java/com/kurekurecredential/repository/StudyTaskRepository.java` | 所有ユーザーと検索条件を組み合わせたタスク検索を追加 |
| `src/main/java/com/kurekurecredential/repository/StudyPlanItemRepository.java` | 計画・資格目標・所有者情報の同時取得を追加 |
| `src/main/java/com/kurekurecredential/service/task/StudyTaskService.java` | タスクの作成・一覧・詳細・更新・完了と整合性チェックを追加 |
| `src/main/java/com/kurekurecredential/web/task/*.java` | Controllerとリクエスト・レスポンスDTOを追加 |
| `src/test/java/com/kurekurecredential/web/task/StudyTaskControllerIntegrationTest.java` | 学習タスクAPIの結合テストを追加 |
| `docs/api-list.md` | 更新・完了・削除方針を追記 |

### テスト対象

- 未認証では学習タスクAPIを利用できない
- 所有ユーザーが学習計画項目付きのタスクを作成できる
- 作成時のステータスが `TODO` になる
- 資格目標の期間外の期限は拒否される
- 別の資格目標に属する計画項目は関連付けできない
- 自分のタスクだけを期限順で一覧取得できる
- 資格目標、ステータス、期限範囲で絞り込める
- 所有ユーザーがタスクを詳細取得・更新できる
- `DONE` 更新時に完了日時が設定される
- `TODO` に戻した場合に完了日時がクリアされる
- 完了APIでステータスと完了日時が設定される
- 他ユーザーはタスクや計画項目を操作できない
- 不正な期限範囲・ステータスを拒否する
- 存在しないタスクIDは `404 Not Found` になる

### 検証結果

以下のコマンドで全38件のテストが成功した。

```powershell
.\gradlew.bat clean test --no-daemon
```

結果:

```txt
BUILD SUCCESSFUL
Tests: 38, Failures: 0, Errors: 0, Skipped: 0
```

- PostgreSQL 16.13に接続したアプリを8081番ポートで起動した
- 実APIで資格目標、学習計画、週次項目、学習タスクを作成した
- タスクと学習計画項目の関連が保存されることを確認した
- 完了APIで `DONE` と完了日時が設定されることを確認した
- 日付条件なし一覧と、資格目標・状態・期限範囲による複合検索を確認した
- 実DBの `study_tasks` で状態、完了日時、学習計画項目IDを確認した
- 別ユーザーによるタスク詳細取得が `403 Forbidden` になることを確認した
- API確認後、確認用ユーザー2件を関連データごと削除した
- 検証用Spring Bootプロセスを停止し、PostgreSQLコンテナは起動状態を維持した

### 発生した問題・対応

- H2では成功したタスク一覧検索が、最初のPostgreSQL確認では例外になった
  - 原因: JPQLのNULLになり得る日付パラメータについて、PostgreSQLがSQLパラメータの型を判定できなかった
  - 発生したSQLState: `42P18`
  - 対応: `dueFrom` と `dueTo` をJPQL内で明示的に `date` 型へキャストした
  - 結果: 日付条件なしと日付条件ありの両方がPostgreSQLで成功した

### 次にやること

- 学習ログの作成・一覧・詳細・更新APIを実装する
- 任意指定するタスク・教材と資格目標の整合性、所有者を確認する

### 学習ログAPI実装

- `POST /api/certification-goals/{goalId}/study-logs` を実装した
- `GET /api/study-logs` を実装した
- `goalId`、`studiedFrom`、`studiedTo` の組み合わせ検索を実装した
- `GET /api/study-logs/{logId}` を実装した
- `PUT /api/study-logs/{logId}` を実装した
- 一覧は学習日の降順、同日の場合はIDの降順で返すようにした
- 学習時間を1分以上に制限した
- 学習日を資格目標の学習期間内に制限した
- タスクは任意指定とし、同じ資格目標に属するタスクだけを関連付けられるようにした
- 教材は任意指定とし、資格目標と同じ資格向けの教材だけを関連付けられるようにした
- 学習ログ、資格目標、タスクの所有者チェックを実装した
- 他ユーザーの学習ログ・タスクへのアクセスを `403 Forbidden` にした
- 学習実績を維持するため、削除APIはMVP対象外にした
- PostgreSQLのNULL日付パラメータ型問題を避けるため、検索日付をJPQLで明示的に `date` 型へキャストした
- 学習ログ用テーブルはV1で作成済みのため、Flywayマイグレーションの追加は行わなかった

### 作成・更新した主なファイル

| ファイル | 内容 |
| --- | --- |
| `src/main/java/com/kurekurecredential/repository/StudyLogRepository.java` | 所有ユーザーと日付条件を組み合わせた学習ログ検索を追加 |
| `src/main/java/com/kurekurecredential/service/studylog/StudyLogService.java` | 学習ログの作成・一覧・詳細・更新と関連整合性チェックを追加 |
| `src/main/java/com/kurekurecredential/web/studylog/*.java` | Controllerとリクエスト・レスポンスDTOを追加 |
| `src/test/java/com/kurekurecredential/web/studylog/StudyLogControllerIntegrationTest.java` | 学習ログAPIの結合テストを追加 |
| `docs/api-list.md` | 一覧順序、関連整合性、削除方針を追記 |

### テスト対象

- 未認証では学習ログAPIを利用できない
- タスク・教材付きの学習ログを作成できる
- 資格目標の期間外の学習日は拒否される
- 学習時間が0分の場合は拒否される
- 別の資格目標に属するタスクは関連付けできない
- 別の資格向け教材は関連付けできない
- 自分の学習ログだけを学習日の新しい順で取得できる
- 資格目標と学習日範囲で絞り込める
- 所有ユーザーが学習ログを詳細取得・更新できる
- 更新時にタスク・教材の関連を解除できる
- 他ユーザーは学習ログやタスクを利用できない
- 不正な学習日範囲を拒否する
- 存在しない学習ログIDは `404 Not Found` になる

### 検証結果

以下のコマンドで全45件のテストが成功した。

```powershell
.\gradlew.bat clean test --no-daemon
```

結果:

```txt
BUILD SUCCESSFUL
Tests: 45, Failures: 0, Errors: 0, Skipped: 0
```

- PostgreSQL 16.13に接続したアプリを8081番ポートで起動した
- 実APIで資格目標、学習タスク、教材付き学習ログを作成した
- 学習ログを120分、理解度 `HIGH` に更新できることを確認した
- 日付条件なし一覧と資格目標・日付範囲による絞り込みを確認した
- 実DBの `study_logs` で学習時間、理解度、タスクID、教材IDを確認した
- 別ユーザーによる学習ログ詳細取得が `403 Forbidden` になることを確認した
- API確認後、確認用ユーザー2件を関連データごと削除した
- 検証用Spring Bootプロセスを停止し、PostgreSQLコンテナは起動状態を維持した

### 次にやること

- 模擬試験結果の作成・一覧・詳細・更新APIを実装する
- 得点、満点、合格ライン、正答率の整合性を検証する
