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

## Dockerイメージ作成

```powershell
.\gradlew.bat clean bootJar -x test
docker build -t kurekure-credential:local .
```

## メモ

このワークスペースでは、Gradle Wrapperの初回取得時にJavaの証明書検証で失敗したため、`gradle.properties` でWindowsの証明書ストアを使う設定を追加しています。

```properties
org.gradle.jvmargs=-Djavax.net.ssl.trustStoreType=Windows-ROOT
```

テスト実行は現時点でGradleテストワーカーのクラス読み込み問題により失敗しています。アプリ本体のコンパイルとJar作成は `.\gradlew.bat clean build -x test` で確認済みです。
