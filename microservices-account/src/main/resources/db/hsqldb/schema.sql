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
    create_time datetime         default now() not null,
    update_time datetime on update now()       null,
    constraint username unique (username)
);
