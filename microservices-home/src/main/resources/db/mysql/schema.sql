create table community
(
    id                bigint unsigned auto_increment
        primary key,
    community_name    varchar(50)                                null comment '小区名称',
    community_address varchar(250)                               null comment '地址',
    average_price     decimal(10, 2)                             null comment '均价',
    building_age      year                                       null comment '建筑年代',
    building_type     varchar(50)                                null comment '建筑类型',
    building_number   int                                        null comment '栋数',
    household_number  varchar(100)                               null comment '户数',
    volume_rate       decimal(5, 1)                              null comment '容积率',
    greening_rate     decimal(5, 1)                              null comment '绿化率',
    parking_above     int                                        null comment '地上停车位',
    parking_under     int                                        null comment '地下停车位',
    developer         varchar(150)                               null comment '开发商',
    property          varchar(150)                               null comment '物业',
    new_flag          tinyint unsigned default '0'               null comment '0 二手房 1 新房',
    remark            varchar(50)                                null,
    del_flag          tinyint unsigned default '0'               null,
    create_time       datetime         default CURRENT_TIMESTAMP not null,
    update_time       datetime                                   null on update CURRENT_TIMESTAMP,
    constraint community_name
        unique (community_name) comment '小区'
)
    comment '小区';
