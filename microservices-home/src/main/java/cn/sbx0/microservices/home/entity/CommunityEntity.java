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
 * 小区
 * </p>
 *
 * @author wangh
 * @since 2022-06-10
 */
@Getter
@Setter
@TableName("community")
public class CommunityEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
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
     * 新旧
     */
    private Integer newFlag;

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

    /**
     * 地图配置
     */
    private String mapConfig;

    /**
     * 区域
     */
    private String areaName;

    /**
     * 主观评分
     */
    private Integer subjectiveRating;

    /**
     * 客观评分
     */
    private Integer objectiveRating;


}
