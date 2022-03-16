drop table if exists game_room;

create table game_room
(
    id             bigint unsigned auto_increment primary key,
    room_code      varchar(50)                    null,
    room_name      varchar(50)                    null,
    room_password  varchar(100)                   null,
    players_size   int unsigned     default 4     null,
    room_status    int unsigned     default 0     null,
    privacy_flag   tinyint unsigned default 1     null,
    remark         varchar(50)                    null,
    del_flag       tinyint unsigned default 0     null,
    create_user_id bigint unsigned                null,
    create_time    datetime         default now() not null,
    update_user_id bigint unsigned                null,
    update_time    datetime on update now()       null,
    constraint code unique (room_code)
);

drop table if exists game_room_user;

create table game_room_user
(
    id             bigint unsigned auto_increment primary key,
    room_id        bigint unsigned                null,
    user_id        bigint unsigned                null,
    username       varchar(50)                    null,
    remark         varchar(50)                    null,
    del_flag       tinyint unsigned default 0     null,
    create_user_id bigint unsigned                null,
    create_time    datetime         default now() not null,
    update_user_id bigint unsigned                null,
    update_time    datetime on update now()       null
);