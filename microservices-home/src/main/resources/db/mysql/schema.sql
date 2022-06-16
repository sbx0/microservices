create table community
(
    id                bigint unsigned auto_increment comment 'ID'
        primary key,
    community_name    varchar(50)                                null comment '小区名称',
    community_address varchar(250)                               null comment '地址',
    average_price     decimal(10, 2)                             null comment '均价',
    building_age      year                                       null comment '建筑年代',
    building_type     varchar(50)                                null comment '建筑类型',
    building_number   int                                        null comment '栋数',
    household_number  varchar(100)                               null comment '户数',
    volume_rate       decimal(5, 1)                              null comment '容积率',
    greening_rate     decimal(5, 2)                              null comment '绿化率',
    parking_above     int                                        null comment '地上停车位',
    parking_under     int                                        null comment '地下停车位',
    developer         varchar(150)                               null comment '开发商',
    property          varchar(150)                               null comment '物业',
    new_flag          tinyint unsigned default '0'               null comment '新旧',
    remark            varchar(50)                                null comment '备注',
    del_flag          tinyint unsigned default '0'               null comment '删除标记',
    create_time       datetime         default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime                                   null on update CURRENT_TIMESTAMP comment '修改时间',
    map_config        varchar(512)                               null comment '地图配置',
    area_name         varchar(50)                                null comment '区域',
    subjective_rating int                                        null comment '主观评分',
    objective_rating  int                                        null comment '客观评分',
    constraint community_name
        unique (community_name) comment '小区'
)
    comment '小区';

create table community_house
(
    id                bigint unsigned auto_increment comment 'ID' primary key,
    community_id      bigint unsigned                            null comment '小区',
    community_name    bigint unsigned                            null comment '小区名称',
    house_name        varchar(50)                                null comment '名称',
    decoration_type   varchar(250)                               null comment '装修类型',
    house_price       decimal(10, 2)                             null comment '价格',
    house_size        decimal(10, 2)                             null comment '面积',
    first_pay         decimal(10, 2)                             null comment '首付',
    subjective_rating int                                        null comment '主观评分',
    objective_rating  int                                        null comment '客观评分',
    remark            varchar(50)                                null comment '备注',
    del_flag          tinyint unsigned default '0'               null comment '删除标记',
    create_time       datetime         default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime                                   null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint house_name
        unique (house_name) comment '名称'
)
    comment '小区房子';

