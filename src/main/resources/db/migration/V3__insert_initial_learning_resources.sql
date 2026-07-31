insert into learning_resources (
    certification_id, title, url, resource_type, author, published_year,
    recommendation_score, target_level, estimated_study_hours, paid, official, memo
)
select
    id,
    'AWS Certified Solutions Architect - Associate 試験ガイド',
    'https://docs.aws.amazon.com/aws-certification/latest/solutions-architect-associate-03/solutions-architect-associate-03.html',
    'OFFICIAL_DOCUMENT',
    'Amazon Web Services',
    null,
    5,
    'BEGINNER',
    4,
    false,
    true,
    '出題分野と試験対象サービスを確認するための公式試験ガイド。'
from certifications
where name = 'AWS Certified Solutions Architect - Associate';

insert into learning_resources (
    certification_id, title, url, resource_type, author, published_year,
    recommendation_score, target_level, estimated_study_hours, paid, official, memo
)
select
    id,
    'AWS Skill Builder - Solutions Architect 学習',
    'https://skillbuilder.aws/',
    'WEB_SITE',
    'Amazon Web Services',
    null,
    5,
    'BEGINNER',
    40,
    false,
    true,
    'AWS公式のデジタルトレーニング。無料コンテンツを中心に利用する。'
from certifications
where name = 'AWS Certified Solutions Architect - Associate';

insert into learning_resources (
    certification_id, title, url, resource_type, author, published_year,
    recommendation_score, target_level, estimated_study_hours, paid, official, memo
)
select
    id,
    'AWS Certified Developer - Associate 試験ガイド',
    'https://docs.aws.amazon.com/aws-certification/latest/developer-associate-02/developer-associate-02.html',
    'OFFICIAL_DOCUMENT',
    'Amazon Web Services',
    null,
    5,
    'BASIC',
    4,
    false,
    true,
    '出題分野と試験対象サービスを確認するための公式試験ガイド。'
from certifications
where name = 'AWS Certified Developer - Associate';

insert into learning_resources (
    certification_id, title, url, resource_type, author, published_year,
    recommendation_score, target_level, estimated_study_hours, paid, official, memo
)
select
    id,
    'AWS Skill Builder - Developer 学習',
    'https://skillbuilder.aws/',
    'WEB_SITE',
    'Amazon Web Services',
    null,
    5,
    'BASIC',
    40,
    false,
    true,
    'AWS公式のデジタルトレーニング。開発・デプロイ・トラブルシューティングを学ぶ。'
from certifications
where name = 'AWS Certified Developer - Associate';

insert into learning_resources (
    certification_id, title, url, resource_type, author, published_year,
    recommendation_score, target_level, estimated_study_hours, paid, official, memo
)
select
    id,
    'Java SE 17 Programmer I 試験内容チェックリスト',
    'https://www.oracle.com/jp/education/certification/javase-17-programmer-1/',
    'OFFICIAL_DOCUMENT',
    'Oracle',
    null,
    5,
    'BEGINNER',
    3,
    false,
    true,
    'Silver SE 17の出題範囲を確認するためのOracle公式資料。'
from certifications
where name = 'Oracle Certified Java Programmer, Silver SE 17';

insert into learning_resources (
    certification_id, title, url, resource_type, author, published_year,
    recommendation_score, target_level, estimated_study_hours, paid, official, memo
)
select
    id,
    'dev.java Learn Java',
    'https://dev.java/learn/',
    'WEB_SITE',
    'Oracle',
    null,
    5,
    'BEGINNER',
    30,
    false,
    true,
    'Java言語と主要APIを学べるOracle運営の学習サイト。'
from certifications
where name = 'Oracle Certified Java Programmer, Silver SE 17';

insert into learning_resources (
    certification_id, title, url, resource_type, author, published_year,
    recommendation_score, target_level, estimated_study_hours, paid, official, memo
)
select
    id,
    'Java SE 17 Programmer II 試験内容チェックリスト',
    'https://www.oracle.com/jp/education/certification/javase-17-programmer-2/',
    'OFFICIAL_DOCUMENT',
    'Oracle',
    null,
    5,
    'EXPERIENCED',
    3,
    false,
    true,
    'Gold SE 17の出題範囲を確認するためのOracle公式資料。'
from certifications
where name = 'Oracle Certified Java Programmer, Gold SE 17';

insert into learning_resources (
    certification_id, title, url, resource_type, author, published_year,
    recommendation_score, target_level, estimated_study_hours, paid, official, memo
)
select
    id,
    'dev.java Learn Java - Advanced Topics',
    'https://dev.java/learn/',
    'WEB_SITE',
    'Oracle',
    null,
    5,
    'EXPERIENCED',
    50,
    false,
    true,
    'Stream、I/O、並列処理などを公式資料と実装例で確認する。'
from certifications
where name = 'Oracle Certified Java Programmer, Gold SE 17';

insert into learning_resources (
    certification_id, title, url, resource_type, author, published_year,
    recommendation_score, target_level, estimated_study_hours, paid, official, memo
)
select
    id,
    '基本情報技術者試験 シラバス・試験要綱',
    'https://www.ipa.go.jp/shiken/syllabus/gaiyou.html',
    'OFFICIAL_DOCUMENT',
    'IPA',
    null,
    5,
    'BEGINNER',
    4,
    false,
    true,
    '出題範囲と要求される知識・技能を確認するためのIPA公式資料。'
from certifications
where name = '基本情報技術者試験';

insert into learning_resources (
    certification_id, title, url, resource_type, author, published_year,
    recommendation_score, target_level, estimated_study_hours, paid, official, memo
)
select
    id,
    '基本情報技術者試験 公開問題・サンプル問題',
    'https://www.ipa.go.jp/shiken/mondai-kaiotu/index.html',
    'QUESTION_BANK',
    'IPA',
    null,
    5,
    'BASIC',
    30,
    false,
    true,
    '科目A・科目Bの形式と出題傾向を確認するための公式問題。'
from certifications
where name = '基本情報技術者試験';

insert into learning_resources (
    certification_id, title, url, resource_type, author, published_year,
    recommendation_score, target_level, estimated_study_hours, paid, official, memo
)
select
    id,
    '応用情報技術者試験 シラバス・試験要綱',
    'https://www.ipa.go.jp/shiken/syllabus/gaiyou.html',
    'OFFICIAL_DOCUMENT',
    'IPA',
    null,
    5,
    'BASIC',
    5,
    false,
    true,
    '出題範囲と要求される応用知識を確認するためのIPA公式資料。'
from certifications
where name = '応用情報技術者試験';

insert into learning_resources (
    certification_id, title, url, resource_type, author, published_year,
    recommendation_score, target_level, estimated_study_hours, paid, official, memo
)
select
    id,
    '応用情報技術者試験 過去問題・解答・講評',
    'https://www.ipa.go.jp/shiken/mondai-kaiotu/index.html',
    'QUESTION_BANK',
    'IPA',
    null,
    5,
    'EXPERIENCED',
    60,
    false,
    true,
    '過去問題と採点講評を使って記述問題を含む出題形式に慣れる。'
from certifications
where name = '応用情報技術者試験';
