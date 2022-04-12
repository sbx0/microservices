package cn.sbx0.microservices.uno.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2022-04-12
 */
@Getter
@Setter
@TableName("game_room")
public class GameRoomEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 房间编号
     */
    private String roomCode;

    /**
     * 房间名称
     */
    private String roomName;

    /**
     * 房间密码
     */
    private String roomPassword;

    /**
     * 房间容量
     */
    private Integer playersSize;

    /**
     * 房间状态
     */
    private Integer roomStatus;

    /**
     * 是否公开
     */
    private Integer publicFlag;

    /**
     * 实例ID
     */
    private String instanceId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除
     */
    private Integer delFlag;

    /**
     * 创建人
     */
    private Long createUserId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    private Long updateUserId;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;


}
