# 任务信息
# 任务类型 待办 0 、纪念日 1
# 重复类型 不重复 0 、每年重复 1 、每月重复 2 、 每周重复 3 、每日重复 4
CREATE TABLE mission_info
(
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id         BIGINT UNSIGNED                            NOT NULL COMMENT '用户ID',
    mission_name    VARCHAR(128)                               NOT NULL COMMENT '任务名称',
    mission_type    INT UNSIGNED                               NOT NULL COMMENT '任务类型',
    mission_date    DATETIME                                   NULL COMMENT '任务日期',
    repeat_type     INT UNSIGNED                               NOT NULL DEFAULT 0 COMMENT '重复类型',
    mission_details VARCHAR(512)                               NULL COMMENT '任务描述',
    del_flag        TINYINT UNSIGNED DEFAULT 0                 NOT NULL COMMENT '删除标记',
    create_time     DATETIME         DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time     DATETIME ON UPDATE CURRENT_TIMESTAMP       NULL COMMENT '修改时间'
) COMMENT '任务信息'
