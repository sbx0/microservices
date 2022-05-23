package cn.sbx0.microservices.uno.entity;

import lombok.Data;

/**
 * @author sbx0
 * @since 2022/5/23
 */
@Data
public class GameResultVO {
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户名称
     */
    private String userName;

    /**
     * 回合数
     */
    private Integer round;

    /**
     * 名次
     */
    private Integer ranking;
}
