drop table if exists game_room;
drop table if exists game_room_user;

create table game_room
(
    id             bigint unsigned auto_increment primary key,
    room_code      varchar(50)                    null comment '房间编号',
    room_name      varchar(50)                    null comment '房间名称',
    room_password  varchar(100)                   null comment '房间密码',
    players_size   int unsigned     default 4     null comment '房间容量',
    room_status    int unsigned     default 0     null comment '房间状态',
    public_flag    tinyint unsigned default 1     null comment '是否公开',
    instance_id    varchar(50)                    null comment '实例ID',
    remark         varchar(50)                    null comment '备注',
    del_flag       tinyint unsigned default 0     null comment '逻辑删除',
    create_user_id bigint unsigned                null comment '创建人',
    create_time    datetime         default now() not null comment '创建时间',
    update_user_id bigint unsigned                null comment '修改人',
    update_time    datetime on update now()       null comment '修改时间',
    constraint code unique (room_code)
);

create table game_room_user
(
    id             bigint unsigned auto_increment primary key,
    room_id        bigint unsigned                null comment '房间ID',
    user_id        bigint unsigned                null comment '用户ID',
    username       varchar(50)                    null comment '用户名称',
    remark         varchar(50)                    null comment '备注',
    del_flag       tinyint unsigned default 0     null comment '逻辑删除',
    create_user_id bigint unsigned                null comment '创建人',
    create_time    datetime         default now() not null comment '创建时间',
    update_user_id bigint unsigned                null comment '修改人',
    update_time    datetime on update now()       null comment '修改时间'
);
