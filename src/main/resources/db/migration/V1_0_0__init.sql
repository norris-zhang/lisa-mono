create table "user" (
    "id" bigserial primary key,
    "username" varchar(255) unique not null,
    "password" varchar(255) not null,
    "role" varchar(255)
);
insert into "user" ("username", "password", "role") values ('lisa', '{bcrypt}$2a$10$ssreL24b8qCncRyYnucjCOSEmqwLWPLowTL4gSUAiPsMyIMAtQ5Je', 'TEACHER');

create table "institution" (
    "id" bigserial primary key,
    "name" varchar(255) unique not null
);

create table "user_institution" (
    "id" bigserial primary key,
    "user_id" bigint not null,
    "institution_id" bigint not null,
    constraint fk_user_id foreign key ("user_id") references "user"("id"),
    constraint fk_institution_id foreign key ("institution_id") references "institution"("id")
);

create table "lisa_class" (
    "id" bigserial primary key,
    "institution_id" bigint not null,
    "name" varchar(255) not null,
    "weekday" varchar(20) not null,
    "start_time" time not null,
    "end_time" time not null,
    constraint fk_institution_id foreign key ("institution_id") references "institution"("id")
);

create table "student" (
    "id" bigserial primary key,
    "first_name" varchar(255) not null,
    "last_name" varchar(255) null,
    "date_of_birth" date null,
    "credits" integer not null
);

create table "student_class" (
    "id" bigserial primary key,
    "class_id" bigint not null,
    "student_id" bigint not null,
    constraint fk_class_id foreign key ("class_id") references "lisa_class"("id"),
    constraint fk_student_id foreign key ("student_id") references "student"("id")
);

create table "parent" (
    "id" bigserial primary key,
    "first_name" varchar(255) not null,
    "last_name" varchar(255) null,
    "contact_number" varchar(20) null
);

create table "parent_student" (
    "id" bigserial primary key,
    "parent_id" bigint not null,
    "student_id" bigint not null,
    "relationship" char(2) null,
    constraint fk_parent_id foreign key ("parent_id") references "parent"("id"),
    constraint fk_student_id foreign key ("student_id") references "student"("id")
);

create table "picture" (
    "id" bigserial primary key,
    "file_name" varchar(255) not null,
    "extension" varchar(20) not null,
    "mimetype" varchar(255) not null,
    "path" varchar(255) not null
);

create table "work" (
    "id" bigserial primary key,
    "title" varchar(255) not null,
    "description" text null,
    "date" date not null,
    "upload_date" timestamp with time zone not null default now(),
    "picture_id" bigint not null,
    "student_id" bigint not null,
    constraint fk_picture_id foreign key ("picture_id") references "picture"("id"),
    constraint fk_student_id foreign key ("student_id") references "student"("id")
);

create table "roll" (
    "id" bigserial primary key,
    "student_id" bigint not null,
    "class_id" bigint not null,
    "class_date" date not null,
    "input_date" timestamp with time zone not null default now(),
    constraint fk_student_id foreign key ("student_id") references "student"("id"),
    constraint fk_class_id foreign key ("class_id") references "lisa_class"("id")
);

create table "renew" (
    "id" bigserial primary key,
    "student_id" bigint not null,
    "class_id" bigint not null,
    "date" date null,
    "input_date" timestamp with time zone not null default now(),
    "current_credit" integer not null,
    "new_credit" integer not null,
    constraint fk_student_id foreign key ("student_id") references "student"("id")
);