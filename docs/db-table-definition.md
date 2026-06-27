# クレクレデンシャル DBテーブル定義

## 1. 概要

本書は、ER図をもとにPostgreSQLで使用するDBテーブル定義を整理したものである。

実装時は、本定義をもとにFlywayのマイグレーションSQLを作成する。

## 2. 命名方針

- テーブル名はスネークケースの複数形とする
- カラム名はスネークケースとする
- 主キーは `id` とする
- 外部キーは `{参照先単数名}_id` とする
- 作成日時は `created_at`、更新日時は `updated_at` とする
- 日時はPostgreSQLの `timestamp with time zone` を使用する
- 日付のみの項目は `date` を使用する
- 金額や割合以外の整数値は `integer` を基本とする

## 3. テーブル定義

### 3.1 users

ユーザー情報を管理する。

| カラム | 型 | NULL | 制約 | 説明 |
| --- | --- | --- | --- | --- |
| `id` | `bigserial` | NO | PK | ユーザーID |
| `name` | `varchar(100)` | NO |  | ユーザー名 |
| `email` | `varchar(255)` | NO | UNIQUE | メールアドレス |
| `password_hash` | `varchar(255)` | NO |  | ハッシュ化済みパスワード |
| `created_at` | `timestamp with time zone` | NO |  | 作成日時 |
| `updated_at` | `timestamp with time zone` | NO |  | 更新日時 |

### 3.2 certifications

資格マスタを管理する。

| カラム | 型 | NULL | 制約 | 説明 |
| --- | --- | --- | --- | --- |
| `id` | `bigserial` | NO | PK | 資格ID |
| `name` | `varchar(255)` | NO |  | 資格名 |
| `provider` | `varchar(100)` | NO |  | 提供元 |
| `difficulty` | `varchar(50)` | NO |  | 難易度 |
| `description` | `text` | YES |  | 説明 |
| `recommended_study_hours` | `integer` | YES |  | 推奨学習時間 |
| `exam_format` | `varchar(255)` | YES |  | 試験形式 |
| `passing_score` | `integer` | YES |  | 合格ラインの目安 |
| `official_url` | `varchar(1000)` | YES |  | 公式サイトURL |
| `validity_period` | `varchar(100)` | YES |  | 有効期限 |
| `created_at` | `timestamp with time zone` | NO |  | 作成日時 |
| `updated_at` | `timestamp with time zone` | NO |  | 更新日時 |

### 3.3 user_certification_goals

ユーザーごとの資格取得目標を管理する。

| カラム | 型 | NULL | 制約 | 説明 |
| --- | --- | --- | --- | --- |
| `id` | `bigserial` | NO | PK | 資格目標ID |
| `user_id` | `bigint` | NO | FK | ユーザーID |
| `certification_id` | `bigint` | NO | FK | 資格ID |
| `target_exam_date` | `date` | NO |  | 目標試験日 |
| `weekly_study_hours` | `integer` | NO |  | 週の学習可能時間 |
| `current_level` | `varchar(50)` | NO |  | 現在レベル |
| `study_start_date` | `date` | NO |  | 学習開始日 |
| `status` | `varchar(50)` | NO |  | 目標ステータス |
| `created_at` | `timestamp with time zone` | NO |  | 作成日時 |
| `updated_at` | `timestamp with time zone` | NO |  | 更新日時 |

制約:

- `user_id` は `users.id` を参照する
- `certification_id` は `certifications.id` を参照する
- `weekly_study_hours` は1以上を想定する

### 3.4 study_plans

資格目標に対する学習計画全体を管理する。

| カラム | 型 | NULL | 制約 | 説明 |
| --- | --- | --- | --- | --- |
| `id` | `bigserial` | NO | PK | 学習計画ID |
| `goal_id` | `bigint` | NO | FK | 資格目標ID |
| `title` | `varchar(255)` | NO |  | 学習計画タイトル |
| `start_date` | `date` | NO |  | 開始日 |
| `end_date` | `date` | NO |  | 終了日 |
| `total_planned_hours` | `integer` | YES |  | 総予定学習時間 |
| `memo` | `text` | YES |  | メモ |
| `created_at` | `timestamp with time zone` | NO |  | 作成日時 |
| `updated_at` | `timestamp with time zone` | NO |  | 更新日時 |

制約:

- `goal_id` は `user_certification_goals.id` を参照する

### 3.5 study_plan_items

週ごとの学習テーマや学習フェーズを管理する。

| カラム | 型 | NULL | 制約 | 説明 |
| --- | --- | --- | --- | --- |
| `id` | `bigserial` | NO | PK | 学習計画項目ID |
| `study_plan_id` | `bigint` | NO | FK | 学習計画ID |
| `week_number` | `integer` | NO |  | 週番号 |
| `phase` | `varchar(50)` | NO |  | 学習フェーズ |
| `title` | `varchar(255)` | NO |  | 週ごとの学習テーマ |
| `description` | `text` | YES |  | 詳細 |
| `planned_hours` | `integer` | NO |  | 予定学習時間 |
| `mock_exam_recommended` | `boolean` | NO |  | 模擬試験推奨フラグ |
| `recommended_start_date` | `date` | YES |  | 推奨開始日 |
| `recommended_end_date` | `date` | YES |  | 推奨終了日 |
| `created_at` | `timestamp with time zone` | NO |  | 作成日時 |
| `updated_at` | `timestamp with time zone` | NO |  | 更新日時 |

制約:

- `study_plan_id` は `study_plans.id` を参照する
- `week_number` は1以上を想定する
- `planned_hours` は0以上を想定する

### 3.6 learning_resources

資格ごとの教材マスタを管理する。

| カラム | 型 | NULL | 制約 | 説明 |
| --- | --- | --- | --- | --- |
| `id` | `bigserial` | NO | PK | 教材ID |
| `certification_id` | `bigint` | NO | FK | 資格ID |
| `title` | `varchar(255)` | NO |  | 教材タイトル |
| `url` | `varchar(1000)` | YES |  | URL |
| `resource_type` | `varchar(50)` | NO |  | 教材種別 |
| `author` | `varchar(255)` | YES |  | 著者 |
| `published_year` | `integer` | YES |  | 出版年 |
| `recommendation_score` | `integer` | YES |  | おすすめ度 |
| `target_level` | `varchar(50)` | YES |  | 対象レベル |
| `estimated_study_hours` | `integer` | YES |  | 想定学習時間 |
| `paid` | `boolean` | NO |  | 有料フラグ |
| `official` | `boolean` | NO |  | 公式教材フラグ |
| `memo` | `text` | YES |  | メモ |
| `created_at` | `timestamp with time zone` | NO |  | 作成日時 |
| `updated_at` | `timestamp with time zone` | NO |  | 更新日時 |

制約:

- `certification_id` は `certifications.id` を参照する
- `recommendation_score` は1から5を想定する

### 3.7 study_tasks

資格目標に紐づく学習タスクを管理する。

| カラム | 型 | NULL | 制約 | 説明 |
| --- | --- | --- | --- | --- |
| `id` | `bigserial` | NO | PK | タスクID |
| `goal_id` | `bigint` | NO | FK | 資格目標ID |
| `study_plan_item_id` | `bigint` | YES | FK | 学習計画項目ID |
| `title` | `varchar(255)` | NO |  | タスク名 |
| `description` | `text` | YES |  | 内容 |
| `due_date` | `date` | YES |  | 期限 |
| `estimated_minutes` | `integer` | YES |  | 見積もり時間 |
| `priority` | `varchar(50)` | NO |  | 優先度 |
| `status` | `varchar(50)` | NO |  | タスクステータス |
| `completed_at` | `timestamp with time zone` | YES |  | 完了日時 |
| `created_at` | `timestamp with time zone` | NO |  | 作成日時 |
| `updated_at` | `timestamp with time zone` | NO |  | 更新日時 |

制約:

- `goal_id` は `user_certification_goals.id` を参照する
- `study_plan_item_id` は `study_plan_items.id` を参照する
- `estimated_minutes` は0以上を想定する

### 3.8 study_logs

日々の学習実績を管理する。

| カラム | 型 | NULL | 制約 | 説明 |
| --- | --- | --- | --- | --- |
| `id` | `bigserial` | NO | PK | 学習ログID |
| `goal_id` | `bigint` | NO | FK | 資格目標ID |
| `task_id` | `bigint` | YES | FK | タスクID |
| `resource_id` | `bigint` | YES | FK | 教材ID |
| `studied_date` | `date` | NO |  | 学習日 |
| `study_minutes` | `integer` | NO |  | 学習時間 |
| `title` | `varchar(255)` | NO |  | 学習ログタイトル |
| `content` | `text` | YES |  | 学習内容 |
| `reflection` | `text` | YES |  | 感想・メモ |
| `understanding_level` | `varchar(50)` | YES |  | 理解度 |
| `created_at` | `timestamp with time zone` | NO |  | 作成日時 |
| `updated_at` | `timestamp with time zone` | NO |  | 更新日時 |

制約:

- `goal_id` は `user_certification_goals.id` を参照する
- `task_id` は `study_tasks.id` を参照する
- `resource_id` は `learning_resources.id` を参照する
- `study_minutes` は1以上を想定する

### 3.9 mock_exam_results

模擬試験結果を管理する。

| カラム | 型 | NULL | 制約 | 説明 |
| --- | --- | --- | --- | --- |
| `id` | `bigserial` | NO | PK | 模擬試験結果ID |
| `goal_id` | `bigint` | NO | FK | 資格目標ID |
| `exam_date` | `date` | NO |  | 受験日 |
| `exam_name` | `varchar(255)` | NO |  | 試験名 |
| `score` | `integer` | NO |  | 得点 |
| `max_score` | `integer` | NO |  | 満点 |
| `passing_score` | `integer` | NO |  | 合格ライン |
| `correct_answer_rate` | `numeric(5,2)` | YES |  | 正答率 |
| `weak_areas` | `text` | YES |  | 苦手分野 |
| `memo` | `text` | YES |  | メモ |
| `created_at` | `timestamp with time zone` | NO |  | 作成日時 |
| `updated_at` | `timestamp with time zone` | NO |  | 更新日時 |

制約:

- `goal_id` は `user_certification_goals.id` を参照する
- `score` は0以上を想定する
- `max_score` は1以上を想定する
- `passing_score` は0以上を想定する
- `correct_answer_rate` は0.00から100.00を想定する

## 4. インデックス方針

| テーブル | インデックス | 目的 |
| --- | --- | --- |
| `users` | `email` | ログイン時のユーザー検索 |
| `certifications` | `name` | 資格名検索 |
| `user_certification_goals` | `user_id` | ログインユーザーの資格目標一覧取得 |
| `user_certification_goals` | `user_id, status` | ステータス別の資格目標一覧取得 |
| `learning_resources` | `certification_id` | 資格ごとの教材一覧取得 |
| `study_tasks` | `goal_id` | 資格目標ごとのタスク一覧取得 |
| `study_tasks` | `goal_id, status` | タスク完了状況の集計 |
| `study_logs` | `goal_id, studied_date` | 資格目標ごとの学習ログ集計 |
| `mock_exam_results` | `goal_id, exam_date` | 最新模擬試験結果取得 |

## 5. 初期実装で使用する列挙値

### 5.1 current_level

- `BEGINNER`
- `BASIC`
- `EXPERIENCED`

### 5.2 user_certification_goals.status

- `IN_PROGRESS`
- `PAUSED`
- `PASSED`
- `FAILED`
- `CANCELED`

### 5.3 study_plan_items.phase

- `BASIC_UNDERSTANDING`
- `PRACTICAL_EXERCISE`
- `QUESTION_PRACTICE`
- `MOCK_EXAM`
- `WEAKNESS_REVIEW`

### 5.4 learning_resources.resource_type

- `WEB_SITE`
- `BOOK`
- `VIDEO`
- `MOCK_EXAM`
- `OFFICIAL_DOCUMENT`
- `QUESTION_BANK`

### 5.5 study_tasks.priority

- `LOW`
- `MEDIUM`
- `HIGH`

### 5.6 study_tasks.status

- `TODO`
- `DONE`

### 5.7 study_logs.understanding_level

- `LOW`
- `MEDIUM`
- `HIGH`

## 6. Flyway作成時の分割方針

初期実装では、以下のようにマイグレーションを分割する。

| ファイル | 内容 |
| --- | --- |
| `V1__create_core_tables.sql` | ユーザー、資格、資格目標、教材、計画、タスク、ログ、模試結果テーブル作成 |
| `V2__insert_initial_certifications.sql` | 初期資格マスタ投入 |
| `V3__insert_initial_learning_resources.sql` | 初期教材マスタ投入 |

