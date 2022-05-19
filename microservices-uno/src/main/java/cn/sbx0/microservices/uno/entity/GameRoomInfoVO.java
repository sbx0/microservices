package cn.sbx0.microservices.uno.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author wangh
 * @since 2022-03-10
 */
@Getter
@Setter
public class GameRoomInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roomCode;

    private String roomName;

    private Integer playersSize;

    private Integer roomStatus;

    private Integer round;

    private Integer publicFlag;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    private Boolean isIAmIn;

    private Integer currentGamer;

    private Integer penaltyCards;

    private String direction;

}
