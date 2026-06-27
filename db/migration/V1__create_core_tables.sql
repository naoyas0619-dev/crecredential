create table users (
    id bigserial primary key,
    name varchar(100) not null,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now()
);

create table certifications (
    id bigserial primary key,
    name varchar(255) not null,
    provider varchar(100) not null,
    difficulty varchar(50) not null,
    description text,
    recommended_study_hours integer,
    exam_format varchar(255),
    passing_score integer,
    official_url varchar(1000),
    validity_period varchar(100),
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint chk_certifications_recommended_study_hours
        check (recommended_study_hours is null or recommended_study_hours >= 0),
    constraint chk_certifications_passing_score
        check (passing_score is null or passing_score >= 0)
);

create table user_certification_goals (
    id bigserial primary key,
    user_id bigint not null,
    certification_id bigint not null,
    target_exam_date date not null,
    weekly_study_hours integer not null,
    current_level varchar(50) not null,
    study_start_date date not null,
    status varchar(50) not null,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint fk_user_certification_goals_user
        foreign key (user_id) references users (id) on delete cascade,
    constraint fk_user_certification_goals_certification
        foreign key (certification_id) references certifications (id),
    constraint chk_user_certification_goals_weekly_study_hours
        check (weekly_study_hours >= 1),
    constraint chk_user_certification_goals_current_level
        check (current_level in ('BEGINNER', 'BASIC', 'EXPERIENCED')),
    constraint chk_user_certification_goals_status
        check (status in ('IN_PROGRESS', 'PAUSED', 'PASSED', 'FAILED', 'CANCELED'))
);

create table study_plans (
    id bigserial primary key,
    goal_id bigint not null,
    title varchar(255) not null,
    start_date date not null,
    end_date date not null,
    total_planned_hours integer,
    memo text,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint fk_study_plans_goal
        foreign key (goal_id) references user_certification_goals (id) on delete cascade,
    constraint chk_study_plans_total_planned_hours
        check (total_planned_hours is null or total_planned_hours >= 0),
    constraint chk_study_plans_date_range
        check (end_date >= start_date)
);

create table study_plan_items (
    id bigserial primary key,
    study_plan_id bigint not null,
    week_number integer not null,
    phase varchar(50) not null,
    title varchar(255) not null,
    description text,
    planned_hours integer not null,
    mock_exam_recommended boolean not null default false,
    recommended_start_date date,
    recommended_end_date date,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint fk_study_plan_items_study_plan
        foreign key (study_plan_id) references study_plans (id) on delete cascade,
    constraint chk_study_plan_items_week_number
        check (week_number >= 1),
    constraint chk_study_plan_items_phase
        check (phase in (
            'BASIC_UNDERSTANDING',
            'PRACTICAL_EXERCISE',
            'QUESTION_PRACTICE',
            'MOCK_EXAM',
            'WEAKNESS_REVIEW'
        )),
    constraint chk_study_plan_items_planned_hours
        check (planned_hours >= 0),
    constraint chk_study_plan_items_recommended_date_range
        check (
            recommended_start_date is null
            or recommended_end_date is null
            or recommended_end_date >= recommended_start_date
        )
);

create table learning_resources (
    id bigserial primary key,
    certification_id bigint not null,
    title varchar(255) not null,
    url varchar(1000),
    resource_type varchar(50) not null,
    author varchar(255),
    published_year integer,
    recommendation_score integer,
    target_level varchar(50),
    estimated_study_hours integer,
    paid boolean not null default false,
    official boolean not null default false,
    memo text,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint fk_learning_resources_certification
        foreign key (certification_id) references certifications (id) on delete cascade,
    constraint chk_learning_resources_resource_type
        check (resource_type in (
            'WEB_SITE',
            'BOOK',
            'VIDEO',
            'MOCK_EXAM',
            'OFFICIAL_DOCUMENT',
            'QUESTION_BANK'
        )),
    constraint chk_learning_resources_recommendation_score
        check (recommendation_score is null or recommendation_score between 1 and 5),
    constraint chk_learning_resources_estimated_study_hours
        check (estimated_study_hours is null or estimated_study_hours >= 0)
);

create table study_tasks (
    id bigserial primary key,
    goal_id bigint not null,
    study_plan_item_id bigint,
    title varchar(255) not null,
    description text,
    due_date date,
    estimated_minutes integer,
    priority varchar(50) not null,
    status varchar(50) not null,
    completed_at timestamp with time zone,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint fk_study_tasks_goal
        foreign key (goal_id) references user_certification_goals (id) on delete cascade,
    constraint fk_study_tasks_study_plan_item
        foreign key (study_plan_item_id) references study_plan_items (id) on delete set null,
    constraint chk_study_tasks_estimated_minutes
        check (estimated_minutes is null or estimated_minutes >= 0),
    constraint chk_study_tasks_priority
        check (priority in ('LOW', 'MEDIUM', 'HIGH')),
    constraint chk_study_tasks_status
        check (status in ('TODO', 'DONE'))
);

create table study_logs (
    id bigserial primary key,
    goal_id bigint not null,
    task_id bigint,
    resource_id bigint,
    studied_date date not null,
    study_minutes integer not null,
    title varchar(255) not null,
    content text,
    reflection text,
    understanding_level varchar(50),
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint fk_study_logs_goal
        foreign key (goal_id) references user_certification_goals (id) on delete cascade,
    constraint fk_study_logs_task
        foreign key (task_id) references study_tasks (id) on delete set null,
    constraint fk_study_logs_resource
        foreign key (resource_id) references learning_resources (id) on delete set null,
    constraint chk_study_logs_study_minutes
        check (study_minutes >= 1),
    constraint chk_study_logs_understanding_level
        check (understanding_level is null or understanding_level in ('LOW', 'MEDIUM', 'HIGH'))
);

create table mock_exam_results (
    id bigserial primary key,
    goal_id bigint not null,
    exam_date date not null,
    exam_name varchar(255) not null,
    score integer not null,
    max_score integer not null,
    passing_score integer not null,
    correct_answer_rate numeric(5, 2),
    weak_areas text,
    memo text,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint fk_mock_exam_results_goal
        foreign key (goal_id) references user_certification_goals (id) on delete cascade,
    constraint chk_mock_exam_results_score
        check (score >= 0),
    constraint chk_mock_exam_results_max_score
        check (max_score >= 1),
    constraint chk_mock_exam_results_passing_score
        check (passing_score >= 0),
    constraint chk_mock_exam_results_correct_answer_rate
        check (correct_answer_rate is null or correct_answer_rate between 0 and 100)
);

create index idx_certifications_name on certifications (name);
create index idx_user_certification_goals_user_id on user_certification_goals (user_id);
create index idx_user_certification_goals_user_id_status on user_certification_goals (user_id, status);
create index idx_learning_resources_certification_id on learning_resources (certification_id);
create index idx_study_tasks_goal_id on study_tasks (goal_id);
create index idx_study_tasks_goal_id_status on study_tasks (goal_id, status);
create index idx_study_logs_goal_id_studied_date on study_logs (goal_id, studied_date);
create index idx_mock_exam_results_goal_id_exam_date on mock_exam_results (goal_id, exam_date);
