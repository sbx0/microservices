package cn.sbx0.microservices.home.entity;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;

/**
 * <p>
 *
 * </p>
 *
 * @author wangh
 * @since 2022-06-09
 */
@Getter
@Setter
@TableName("community")
public class CommunityEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 小区名称
     */
    private String communityName;

    /**
     * 地址
     */
    private String communityAddress;

    /**
     * 均价
     */
    private BigDecimal averagePrice;

    /**
     * 建筑年代
     */
    private Year buildingAge;

    /**
     * 建筑类型
     */
    private String buildingType;

    /**
     * 栋数
     */
    private Integer buildingNumber;

    /**
     * 户数
     */
    private String householdNumber;

    /**
     * 容积率
     */
    private BigDecimal volumeRate;

    /**
     * 绿化率
     */
    private BigDecimal greeningRate;

    /**
     * 地上停车位
     */
    private Integer parkingAbove;

    /**
     * 地下停车位
     */
    private Integer parkingUnder;

    /**
     * 开发商
     */
    private String developer;

    /**
     * 物业
     */
    private String property;

    /**
     * 0 二手房 1 新房
     */
    private Integer newFlag;

    private String remark;

    private Integer delFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;


}
