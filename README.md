# クレクレデンシャル

資格取得に向けた学習計画、学習タスク、学習ログ、模擬試験結果を管理するSpring Boot REST APIです。

## 技術構成

- Java 21 target
- Spring Boot 3系
- Spring Web
- Spring Security
- Spring Data JPA
- Flyway
- PostgreSQL
- OpenAPI / Swagger UI
- Gradle
- Docker

## ドキュメント

- [要件定義書](docs/requirements.md)
- [ER図](docs/er-diagram.md)
- [API一覧](docs/api-list.md)
- [DBテーブル定義](docs/db-table-definition.md)

## ローカルDB起動

```powershell
docker compose up -d
```

PostgreSQLの接続情報は以下です。

```txt
DB: kurekure_credential
User: kurekure
Password: kurekure_password
Port: 5432
```

## ビルド

```powershell
.\gradlew.bat clean build -x test
```

## アプリ起動

```powershell
.\gradlew.bat bootRun
```

起動後、以下を利用できます。

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

Swagger UIから認証が必要なAPIを試す場合は、ユーザー登録・ログイン後に取得した `accessToken` を画面上部の `Authorize` へ入力します。`Bearer ` は自動付与されるため、トークン文字列だけを入力します。

## テスト

```powershell
.\gradlew.bat test --no-daemon
```

## Dockerイメージ作成

```powershell
docker build -t kurekure-credential:local .
```

Dockerイメージはマルチステージビルドで作成するため、事前にローカルでJarを生成する必要はありません。

## メモ

このワークスペースでは、Gradle Wrapperの初回取得時にJavaの証明書検証で失敗したため、`gradle.properties` でWindowsの証明書ストアを使う設定を追加しています。

```properties
org.gradle.jvmargs=-Djavax.net.ssl.trustStoreType=Windows-ROOT
```
