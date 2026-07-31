insert into certifications (
    name,
    provider,
    difficulty,
    description,
    recommended_study_hours,
    exam_format,
    passing_score,
    official_url,
    validity_period
) values
(
    'AWS Certified Solutions Architect - Associate',
    'AWS',
    'ASSOCIATE',
    'AWS上で可用性、コスト、性能、セキュリティを考慮したシステムを設計する知識を認定する資格。',
    120,
    '65問、選択式・複数選択式',
    720,
    'https://aws.amazon.com/certification/certified-solutions-architect-associate/',
    '3年'
),
(
    'AWS Certified Developer - Associate',
    'AWS',
    'ASSOCIATE',
    'AWS上のアプリケーションの開発、デプロイ、デバッグに関する知識を認定する資格。',
    100,
    '65問、選択式・複数選択式',
    720,
    'https://aws.amazon.com/certification/certified-developer-associate/',
    '3年'
),
(
    'Oracle Certified Java Programmer, Silver SE 17',
    'Oracle',
    'INTERMEDIATE',
    'Java SE 17を使用した基本的なプログラミング知識と開発能力を認定する資格。',
    120,
    '60問、選択式',
    65,
    'https://www.oracle.com/jp/education/certification/javase-17-programmer-1/',
    null
),
(
    'Oracle Certified Java Programmer, Gold SE 17',
    'Oracle',
    'ADVANCED',
    'Java SE 17の高度な言語仕様とAPIを活用する開発能力を認定する資格。',
    200,
    '60問、選択式',
    65,
    'https://www.oracle.com/jp/education/certification/certification-exam-list/',
    null
),
(
    '基本情報技術者試験',
    'IPA',
    'LEVEL_2',
    'ITエンジニアに必要な基礎知識と技能、および実践的な活用能力を認定する国家試験。',
    200,
    'CBT方式、科目A・科目B',
    600,
    'https://www.ipa.go.jp/shiken/kubun/fe.html',
    null
),
(
    '応用情報技術者試験',
    'IPA',
    'LEVEL_3',
    'ITを活用した戦略立案、設計、開発、運用に必要な応用的知識と技能を認定する国家試験。',
    300,
    'CBT方式、科目A・科目B',
    60,
    'https://www.ipa.go.jp/shiken/kubun/ap.html',
    null
);
