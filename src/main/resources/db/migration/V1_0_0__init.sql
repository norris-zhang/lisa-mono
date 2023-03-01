create table "institution" (
    "id" bigserial primary key,
    "name" varchar(255) unique not null
);

create table "user" (
    "id" bigserial primary key,
    "username" varchar(255) not null,
    "password" varchar(255) not null,
    "institution_id" bigint not null,
    "role" varchar(255),
    constraint fk_usr_institution_id foreign key ("institution_id") references "institution"("id"),
    constraint unique_username_institution_id unique ("username", "institution_id")
);

insert into "institution" ("name") values ('LisaArt');
insert into "institution" ("name") values ('QianCheng');
insert into "institution" ("name") values ('BanMuYuan');
insert into "user" ("username", "password", "institution_id", "role") values ('lisa', '{bcrypt}$2a$10$ssreL24b8qCncRyYnucjCOSEmqwLWPLowTL4gSUAiPsMyIMAtQ5Je', (select "id" from "institution" where "name"='LisaArt'), 'TEACHER');
insert into "user" ("username", "password", "institution_id", "role") values ('dongchenz', '{bcrypt}$2a$10$ssreL24b8qCncRyYnucjCOSEmqwLWPLowTL4gSUAiPsMyIMAtQ5Je', (select "id" from "institution" where "name"='LisaArt'), 'STUDENT');
insert into "user" ("username", "password", "institution_id", "role") values ('qiancheng', '{bcrypt}$2a$10$ssreL24b8qCncRyYnucjCOSEmqwLWPLowTL4gSUAiPsMyIMAtQ5Je', (select "id" from "institution" where "name"='QianCheng'), 'TEACHER');
insert into "user" ("username", "password", "institution_id", "role") values ('banmuyuan', '{bcrypt}$2a$10$ssreL24b8qCncRyYnucjCOSEmqwLWPLowTL4gSUAiPsMyIMAtQ5Je', (select "id" from "institution" where "name"='BanMuYuan'), 'TEACHER');
insert into "user" ("username", "password", "institution_id", "role") values ('admin', '{bcrypt}$2a$10$ssreL24b8qCncRyYnucjCOSEmqwLWPLowTL4gSUAiPsMyIMAtQ5Je', (select "id" from "institution" where "name"='LisaArt'), 'ADMIN');

create table "lisa_class" (
    "id" bigserial primary key,
    "institution_id" bigint not null,
    "name" varchar(255) not null,
    "weekday" varchar(20) not null,
    "start_time" time not null,
    "end_time" time not null,
    "status" integer not null default 0,
    "session_credits" integer not null default 1,
    constraint fk_cla_institution_id foreign key ("institution_id") references "institution"("id")
);

insert into "lisa_class" (institution_id, name, weekday, start_time, end_time) values ((select "id" from "institution" where "name"='LisaArt'), '周六1.30-2.30', 'SATURDAY', '13:30', '14:30');
insert into "lisa_class" (institution_id, name, weekday, start_time, end_time) values ((select "id" from "institution" where "name"='LisaArt'), '周六3.00-4.00', 'SATURDAY', '15:00', '16:00');

create table "student" (
    "id" bigserial primary key,
    "institution_id" bigint not null,
    "first_name" varchar(255) not null,
    "last_name" varchar(255) null,
    "date_of_birth" date null,
    "enrolled_on" date null,
    "credits" integer not null,
    "user_id" bigint null,
    constraint fk_stu_institution_id foreign key ("institution_id") references "institution"("id"),
    constraint fk_stu_user_id foreign key ("user_id") references "user"("id")
);

insert into "student" (institution_id, "first_name", "last_name", "date_of_birth", "enrolled_on", "credits", "user_id") values (1, 'Dongchen', 'Zhang', '2014-03-28', '2018-07-01', 1, 2);
insert into "student" (institution_id, "first_name", "last_name", "date_of_birth", "credits") values (1, 'Dongyu', 'Zhang', '2014-03-28', 1);

create table "student_class" (
    "id" bigserial primary key,
    "class_id" bigint not null,
    "student_id" bigint not null,
    constraint fk_class_id foreign key ("class_id") references "lisa_class"("id"),
    constraint fk_student_id foreign key ("student_id") references "student"("id")
);

insert into "student_class" ("class_id", "student_id") values (1, 1);
insert into "student_class" ("class_id", "student_id") values (1, 2);

create table "parent" (
    "id" bigserial primary key,
    "institution_id" bigint not null,
    "first_name" varchar(255) not null,
    "last_name" varchar(255) null,
    "contact_number" varchar(20) null,
    constraint fk_par_institution_id foreign key ("institution_id") references "institution"("id")
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
    "path" varchar(255) not null,
    "institution_id" bigint not null,
    constraint fk_pic_institution_id foreign key ("institution_id") references "institution"("id")
);

create table "work" (
    "id" bigserial primary key,
    "title" varchar(255) not null,
    "description" text null,
    "date" date not null,
    "upload_date" timestamp with time zone not null default now(),
    "picture_id" bigint not null,
    "student_id" bigint not null,
    "class_id" bigint not null,
    constraint fk_picture_id foreign key ("picture_id") references "picture"("id"),
    constraint fk_student_id foreign key ("student_id") references "student"("id"),
    constraint fk_wor_class_id foreign key ("class_id") references "lisa_class"("id")
);

create table "roll" (
    "id" bigserial primary key,
    "student_id" bigint not null,
    "class_id" bigint not null,
    "class_date" date not null,
    "input_date" timestamp with time zone not null default now(),
    "is_present" char(1) not null,
    "credit_redeemed" integer not null default 1,
    constraint fk_student_id foreign key ("student_id") references "student"("id"),
    constraint fk_class_id foreign key ("class_id") references "lisa_class"("id"),
    constraint unique_class_student_date unique ("student_id", "class_id", "class_date")
);

create table "renew" (
    "id" bigserial primary key,
    "student_id" bigint not null,
    "date" date null,
    "input_date" timestamp with time zone not null default now(),
    "opening_balance" integer not null,
    "topup_amount" integer not null,
    "new_balance" integer not null,
    constraint fk_ren_student_id foreign key ("student_id") references "student"("id")
);
