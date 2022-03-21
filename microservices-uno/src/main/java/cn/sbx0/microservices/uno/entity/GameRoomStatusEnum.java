package cn.sbx0.microservices.uno.entity;

import lombok.Getter;

/**
 * @author sbx0
 * @since 2022/3/21
 */
@Getter
public enum GameRoomStatusEnum {
    ENDING("结束状态", 2),
    BEGINNING("开始状态", 1),
    INITIAL("初始状态", 0);
    private String name;
    private Integer value;

    GameRoomStatusEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }


}
