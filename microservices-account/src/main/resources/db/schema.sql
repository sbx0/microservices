drop table if exists account;

create table account
(
    id          bigint unsigned auto_increment primary key,
    username    varchar(50)                    null,
    password    varchar(100)                   null,
    nickname    varchar(50)                    null,
    email       varchar(100)                   null,
    enable_flag tinyint unsigned default 1     null,
    remark      varchar(50)                    null,
    del_flag    tinyint unsigned default 0     null,
    crate_time  datetime         default now() not null,
    update_time datetime on update now()       null,
    constraint username unique (username)
);
INSERT INTO assembler.account (id, username, password, nickname, email, del_flag, enable_flag, remark)
VALUES (1, 'sbx0', '$2a$10$FgUDlcElk34L8rLFPkSVJeLc0qg4hhZP5/cl4jChA8ezWW28tCQ.i', 'test', 'test', 0, 1, 'test');

drop table if exists client_config;

create table client_config
(
    id            bigint unsigned auto_increment primary key,
    client_id     varchar(50)                    null,
    client_secret varchar(100)                   null,
    grant_types   varchar(100)                   null,
    scopes        varchar(100)                   null,
    remark        varchar(50)                    null,
    del_flag      tinyint unsigned default 0     null,
    crate_time    datetime         default now() not null,
    update_time   datetime on update now()       null,
    constraint client_id unique (client_id)
);
