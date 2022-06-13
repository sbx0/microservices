package cn.sbx0.microservices.home.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 小区房子
 * </p>
 *
 * @author wangh
 * @since 2022-06-13
 */
@Getter
@Setter
public class CommunityHouseAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 小区
     */
    private Long communityId;

    /**
     * 小区名称
     */
    private String communityName;

    /**
     * 名称
     */
    private String houseName;

    /**
     * 装修类型
     */
    private String decorationType;

    /**
     * 价格
     */
    private BigDecimal housePrice;

    /**
     * 面积
     */
    private BigDecimal houseSize;

    /**
     * 首付
     */
    private BigDecimal firstPay;

    /**
     * 主观评分
     */
    private Integer subjectiveRating;

    /**
     * 客观评分
     */
    private Integer objectiveRating;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标记
     */
    private Integer delFlag;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;


}
