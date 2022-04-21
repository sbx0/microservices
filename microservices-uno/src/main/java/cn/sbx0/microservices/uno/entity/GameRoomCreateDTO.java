package cn.sbx0.microservices.uno.entity;

import lombok.Data;

/**
 * @author sbx0
 * @since 2022/3/10
 */
@Data
public class GameRoomCreateDTO {
    private String roomName;
    private String roomPassword;
    private Integer playersSize;
    private Integer publicFlag;
    private String remark;
}
