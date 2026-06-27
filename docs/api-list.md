# クレクレデンシャル API一覧

## 1. 概要

本書は、要件定義書とER図をもとに、MVPで実装するREST APIを整理した一覧である。

初期開発ではフロントエンド画面を作成せず、本APIをSwagger / OpenAPIやPostmanから確認する。

## 2. 共通仕様

### 2.1 ベースURL

ローカル開発環境では以下を想定する。

```txt
http://localhost:8080/api
```

### 2.2 認証方式

ログイン後に取得したJWTを、認証が必要なAPIのリクエストヘッダーに付与する。

```http
Authorization: Bearer {accessToken}
```

### 2.3 共通レスポンス方針

MVPでは、レスポンスはシンプルなJSONオブジェクトで返す。

一覧取得APIは配列を直接返すか、将来的なページングを見据えて以下の形式にする。

```json
{
  "items": [],
  "total": 0
}
```

初期実装ではページングなしでもよいが、タスクや学習ログは件数が増えやすいため、後から `page` と `size` を追加しやすいURL設計にする。

### 2.4 共通ステータスコード

| ステータス | 用途 |
| --- | --- |
| `200 OK` | 取得、更新、完了処理が成功した |
| `201 Created` | 作成が成功した |
| `204 No Content` | 削除が成功した |
| `400 Bad Request` | リクエスト値が不正 |
| `401 Unauthorized` | 未認証、またはJWTが不正 |
| `403 Forbidden` | 他ユーザーのデータなど、権限がない |
| `404 Not Found` | 対象データが存在しない |
| `409 Conflict` | メールアドレス重複などの競合 |
| `500 Internal Server Error` | 想定外エラー |

### 2.5 エラーレスポンス例

```json
{
  "code": "VALIDATION_ERROR",
  "message": "入力内容に誤りがあります。",
  "details": [
    {
      "field": "email",
      "message": "メールアドレス形式で入力してください。"
    }
  ]
}
```

## 3. API一覧

### 3.1 認証API

| メソッド | パス | 説明 | 認証 | 関連要件 |
| --- | --- | --- | --- | --- |
| `POST` | `/auth/register` | ユーザー登録 | 不要 | FR-001, FR-005 |
| `POST` | `/auth/login` | ログイン | 不要 | FR-002, FR-003 |
| `GET` | `/auth/me` | ログインユーザー取得 | 必要 | FR-004 |

#### POST /auth/register

リクエスト例:

```json
{
  "name": "山田太郎",
  "email": "taro@example.com",
  "password": "password123"
}
```

レスポンス例:

```json
{
  "id": 1,
  "name": "山田太郎",
  "email": "taro@example.com"
}
```

#### POST /auth/login

リクエスト例:

```json
{
  "email": "taro@example.com",
  "password": "password123"
}
```

レスポンス例:

```json
{
  "accessToken": "jwt-token",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

#### GET /auth/me

レスポンス例:

```json
{
  "id": 1,
  "name": "山田太郎",
  "email": "taro@example.com"
}
```

### 3.2 資格API

| メソッド | パス | 説明 | 認証 | 関連要件 |
| --- | --- | --- | --- | --- |
| `GET` | `/certifications` | 資格一覧取得 | 必要 | FR-101 |
| `GET` | `/certifications/{certificationId}` | 資格詳細取得 | 必要 | FR-102 |

#### GET /certifications

クエリパラメータ:

| 名前 | 必須 | 説明 |
| --- | --- | --- |
| `keyword` | 任意 | 資格名、提供元の部分一致検索 |

レスポンス例:

```json
{
  "items": [
    {
      "id": 1,
      "name": "AWS Certified Solutions Architect - Associate",
      "provider": "AWS",
      "difficulty": "ASSOCIATE",
      "recommendedStudyHours": 120
    }
  ],
  "total": 1
}
```

#### GET /certifications/{certificationId}

レスポンス例:

```json
{
  "id": 1,
  "name": "AWS Certified Solutions Architect - Associate",
  "provider": "AWS",
  "difficulty": "ASSOCIATE",
  "description": "AWSの設計能力を確認する資格。",
  "recommendedStudyHours": 120,
  "examFormat": "選択式、複数選択",
  "passingScore": 720,
  "officialUrl": "https://aws.amazon.com/certification/",
  "validityPeriod": "3年"
}
```

### 3.3 資格目標API

| メソッド | パス | 説明 | 認証 | 関連要件 |
| --- | --- | --- | --- | --- |
| `POST` | `/certification-goals` | 資格目標作成 | 必要 | FR-201, FR-202 |
| `GET` | `/certification-goals` | 自分の資格目標一覧取得 | 必要 | FR-205 |
| `GET` | `/certification-goals/{goalId}` | 自分の資格目標詳細取得 | 必要 | FR-206 |
| `PUT` | `/certification-goals/{goalId}` | 資格目標更新 | 必要 | FR-202, FR-204 |
| `DELETE` | `/certification-goals/{goalId}` | 資格目標削除 | 必要 | FR-006 |

#### POST /certification-goals

リクエスト例:

```json
{
  "certificationId": 1,
  "targetExamDate": "2026-12-20",
  "weeklyStudyHours": 8,
  "currentLevel": "BEGINNER",
  "studyStartDate": "2026-07-01",
  "status": "IN_PROGRESS"
}
```

レスポンス例:

```json
{
  "id": 1,
  "certification": {
    "id": 1,
    "name": "AWS Certified Solutions Architect - Associate"
  },
  "targetExamDate": "2026-12-20",
  "weeklyStudyHours": 8,
  "currentLevel": "BEGINNER",
  "studyStartDate": "2026-07-01",
  "status": "IN_PROGRESS"
}
```

#### GET /certification-goals

クエリパラメータ:

| 名前 | 必須 | 説明 |
| --- | --- | --- |
| `status` | 任意 | `IN_PROGRESS` などのステータスで絞り込み |

レスポンス例:

```json
{
  "items": [
    {
      "id": 1,
      "certificationName": "AWS Certified Solutions Architect - Associate",
      "targetExamDate": "2026-12-20",
      "weeklyStudyHours": 8,
      "status": "IN_PROGRESS"
    }
  ],
  "total": 1
}
```

### 3.4 学習計画API

| メソッド | パス | 説明 | 認証 | 関連要件 |
| --- | --- | --- | --- | --- |
| `POST` | `/certification-goals/{goalId}/study-plans` | 学習計画作成 | 必要 | FR-301 |
| `GET` | `/certification-goals/{goalId}/study-plans` | 資格目標に紐づく学習計画一覧取得 | 必要 | FR-307 |
| `GET` | `/study-plans/{studyPlanId}` | 学習計画詳細取得 | 必要 | FR-307 |

#### POST /certification-goals/{goalId}/study-plans

リクエスト例:

```json
{
  "title": "AWS SAA 12週間学習計画",
  "startDate": "2026-07-01",
  "endDate": "2026-09-23",
  "totalPlannedHours": 96,
  "memo": "基礎理解、問題演習、模擬試験の順で進める。",
  "items": [
    {
      "weekNumber": 1,
      "phase": "BASIC_UNDERSTANDING",
      "title": "IAMとアカウント管理を理解する",
      "description": "IAMユーザー、ロール、ポリシーを学習する。",
      "plannedHours": 8,
      "mockExamRecommended": false,
      "recommendedStartDate": "2026-07-01",
      "recommendedEndDate": "2026-07-07"
    }
  ]
}
```

レスポンス例:

```json
{
  "id": 1,
  "goalId": 1,
  "title": "AWS SAA 12週間学習計画",
  "startDate": "2026-07-01",
  "endDate": "2026-09-23",
  "totalPlannedHours": 96,
  "items": [
    {
      "id": 1,
      "weekNumber": 1,
      "phase": "BASIC_UNDERSTANDING",
      "title": "IAMとアカウント管理を理解する",
      "plannedHours": 8
    }
  ]
}
```

### 3.5 教材API

| メソッド | パス | 説明 | 認証 | 関連要件 |
| --- | --- | --- | --- | --- |
| `GET` | `/learning-resources` | 教材一覧取得 | 必要 | FR-401 |
| `GET` | `/learning-resources/{resourceId}` | 教材詳細取得 | 必要 | FR-401 |

#### GET /learning-resources

クエリパラメータ:

| 名前 | 必須 | 説明 |
| --- | --- | --- |
| `certificationId` | 任意 | 資格IDで絞り込み |
| `resourceType` | 任意 | 教材種別で絞り込み |
| `targetLevel` | 任意 | 対象レベルで絞り込み |

レスポンス例:

```json
{
  "items": [
    {
      "id": 1,
      "certificationId": 1,
      "title": "AWS Skill Builder",
      "url": "https://skillbuilder.aws/",
      "resourceType": "WEB_SITE",
      "recommendationScore": 5,
      "targetLevel": "BEGINNER",
      "estimatedStudyHours": 20,
      "paid": false,
      "official": true
    }
  ],
  "total": 1
}
```

### 3.6 学習タスクAPI

| メソッド | パス | 説明 | 認証 | 関連要件 |
| --- | --- | --- | --- | --- |
| `POST` | `/certification-goals/{goalId}/study-tasks` | タスク作成 | 必要 | FR-501, FR-502 |
| `GET` | `/study-tasks` | 自分のタスク一覧取得 | 必要 | FR-505 |
| `GET` | `/study-tasks/{taskId}` | タスク詳細取得 | 必要 | FR-505 |
| `PUT` | `/study-tasks/{taskId}` | タスク更新 | 必要 | FR-506 |
| `PATCH` | `/study-tasks/{taskId}/complete` | タスク完了 | 必要 | FR-507 |
| `DELETE` | `/study-tasks/{taskId}` | タスク削除 | 必要 | FR-006 |

#### POST /certification-goals/{goalId}/study-tasks

リクエスト例:

```json
{
  "studyPlanItemId": 1,
  "title": "IAMの基本を学習する",
  "description": "IAMユーザー、グループ、ロール、ポリシーの違いを整理する。",
  "dueDate": "2026-07-07",
  "estimatedMinutes": 120,
  "priority": "HIGH"
}
```

レスポンス例:

```json
{
  "id": 1,
  "goalId": 1,
  "studyPlanItemId": 1,
  "title": "IAMの基本を学習する",
  "dueDate": "2026-07-07",
  "estimatedMinutes": 120,
  "priority": "HIGH",
  "status": "TODO"
}
```

#### GET /study-tasks

クエリパラメータ:

| 名前 | 必須 | 説明 |
| --- | --- | --- |
| `goalId` | 任意 | 資格目標IDで絞り込み |
| `status` | 任意 | `TODO` / `DONE` で絞り込み |
| `dueFrom` | 任意 | 期限開始日 |
| `dueTo` | 任意 | 期限終了日 |

### 3.7 学習ログAPI

| メソッド | パス | 説明 | 認証 | 関連要件 |
| --- | --- | --- | --- | --- |
| `POST` | `/certification-goals/{goalId}/study-logs` | 学習ログ作成 | 必要 | FR-601, FR-602 |
| `GET` | `/study-logs` | 自分の学習ログ一覧取得 | 必要 | FR-606 |
| `GET` | `/study-logs/{logId}` | 学習ログ詳細取得 | 必要 | FR-606 |
| `PUT` | `/study-logs/{logId}` | 学習ログ更新 | 必要 | FR-602 |
| `DELETE` | `/study-logs/{logId}` | 学習ログ削除 | 必要 | FR-006 |

#### POST /certification-goals/{goalId}/study-logs

リクエスト例:

```json
{
  "taskId": 1,
  "resourceId": 1,
  "studiedDate": "2026-07-01",
  "studyMinutes": 90,
  "title": "IAMの基本学習",
  "content": "IAMユーザー、ロール、ポリシーの違いを確認した。",
  "reflection": "ロールとポリシーの関係をもう少し復習したい。",
  "understandingLevel": "MEDIUM"
}
```

レスポンス例:

```json
{
  "id": 1,
  "goalId": 1,
  "taskId": 1,
  "resourceId": 1,
  "studiedDate": "2026-07-01",
  "studyMinutes": 90,
  "title": "IAMの基本学習",
  "understandingLevel": "MEDIUM"
}
```

#### GET /study-logs

クエリパラメータ:

| 名前 | 必須 | 説明 |
| --- | --- | --- |
| `goalId` | 任意 | 資格目標IDで絞り込み |
| `studiedFrom` | 任意 | 学習日開始 |
| `studiedTo` | 任意 | 学習日終了 |

### 3.8 模擬試験結果API

| メソッド | パス | 説明 | 認証 | 関連要件 |
| --- | --- | --- | --- | --- |
| `POST` | `/certification-goals/{goalId}/mock-exam-results` | 模擬試験結果作成 | 必要 | FR-701, FR-702 |
| `GET` | `/mock-exam-results` | 自分の模擬試験結果一覧取得 | 必要 | FR-704 |
| `GET` | `/mock-exam-results/{resultId}` | 模擬試験結果詳細取得 | 必要 | FR-704 |
| `PUT` | `/mock-exam-results/{resultId}` | 模擬試験結果更新 | 必要 | FR-702, FR-703 |
| `DELETE` | `/mock-exam-results/{resultId}` | 模擬試験結果削除 | 必要 | FR-006 |

#### POST /certification-goals/{goalId}/mock-exam-results

リクエスト例:

```json
{
  "examDate": "2026-08-15",
  "examName": "AWS SAA 模擬試験 1回目",
  "score": 650,
  "maxScore": 1000,
  "passingScore": 720,
  "correctAnswerRate": 65.0,
  "weakAreas": "VPC, IAM, Route 53",
  "memo": "ネットワーク系の設問で失点が多い。"
}
```

レスポンス例:

```json
{
  "id": 1,
  "goalId": 1,
  "examDate": "2026-08-15",
  "examName": "AWS SAA 模擬試験 1回目",
  "score": 650,
  "maxScore": 1000,
  "passingScore": 720,
  "scoreGap": -70,
  "correctAnswerRate": 65.0,
  "weakAreas": "VPC, IAM, Route 53"
}
```

### 3.9 進捗サマリーAPI

| メソッド | パス | 説明 | 認証 | 関連要件 |
| --- | --- | --- | --- | --- |
| `GET` | `/certification-goals/{goalId}/summary` | 資格目標ごとの進捗サマリー取得 | 必要 | FR-801, FR-802, FR-803, FR-804, FR-805 |

#### GET /certification-goals/{goalId}/summary

レスポンス例:

```json
{
  "goalId": 1,
  "certificationName": "AWS Certified Solutions Architect - Associate",
  "targetExamDate": "2026-12-20",
  "daysUntilExam": 172,
  "plannedStudyMinutes": 5760,
  "actualStudyMinutes": 420,
  "studyProgressRate": 7.29,
  "taskSummary": {
    "total": 10,
    "done": 3,
    "todo": 7,
    "completionRate": 30.0
  },
  "latestMockExamResult": {
    "id": 1,
    "examDate": "2026-08-15",
    "examName": "AWS SAA 模擬試験 1回目",
    "score": 650,
    "passingScore": 720,
    "scoreGap": -70
  }
}
```

## 4. 列挙値

### 4.1 現在レベル

| 値 | 説明 |
| --- | --- |
| `BEGINNER` | 初学者 |
| `BASIC` | 基礎あり |
| `EXPERIENCED` | 実務経験あり |

### 4.2 資格目標ステータス

| 値 | 説明 |
| --- | --- |
| `IN_PROGRESS` | 学習中 |
| `PAUSED` | 一時停止 |
| `PASSED` | 合格 |
| `FAILED` | 不合格 |
| `CANCELED` | 中止 |

### 4.3 学習フェーズ

| 値 | 説明 |
| --- | --- |
| `BASIC_UNDERSTANDING` | 基礎理解 |
| `PRACTICAL_EXERCISE` | 実践演習 |
| `QUESTION_PRACTICE` | 問題演習 |
| `MOCK_EXAM` | 模擬試験 |
| `WEAKNESS_REVIEW` | 弱点補強 |

### 4.4 教材種別

| 値 | 説明 |
| --- | --- |
| `WEB_SITE` | Webサイト |
| `BOOK` | 書籍 |
| `VIDEO` | 動画 |
| `MOCK_EXAM` | 模擬試験 |
| `OFFICIAL_DOCUMENT` | 公式ドキュメント |
| `QUESTION_BANK` | 問題集 |

### 4.5 タスク優先度

| 値 | 説明 |
| --- | --- |
| `LOW` | 低 |
| `MEDIUM` | 中 |
| `HIGH` | 高 |

### 4.6 タスクステータス

| 値 | 説明 |
| --- | --- |
| `TODO` | 未完了 |
| `DONE` | 完了 |

### 4.7 理解度

| 値 | 説明 |
| --- | --- |
| `LOW` | 低 |
| `MEDIUM` | 中 |
| `HIGH` | 高 |

## 5. 実装時の注意点

### 5.1 認可チェック

以下のユーザー固有リソースでは、必ずログインユーザーの所有データか確認する。

- 資格目標
- 学習計画
- 学習タスク
- 学習ログ
- 模擬試験結果
- 進捗サマリー

### 5.2 削除方針

MVPでは物理削除を基本とする。

ただし、資格目標を削除すると関連する学習計画、タスク、学習ログ、模擬試験結果に影響するため、実装時には以下のどちらかを選ぶ。

- 関連データをまとめて削除する
- 削除APIをMVPでは実装せず、ステータスを `CANCELED` にする

初期方針としては、学習履歴を失いにくい `CANCELED` 運用を推奨する。

### 5.3 進捗サマリーの集計

進捗サマリーはDBテーブルを作らず、既存テーブルから集計して返す。

- 予定学習時間: `study_plan_items.planned_hours`
- 実績学習時間: `study_logs.study_minutes`
- タスク完了率: `study_tasks.status`
- 最新模擬試験: `mock_exam_results.exam_date`

