package cn.sbx0.microservices.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sbx0
 * @since 2022/3/8
 */
@ApiModel("分页查询参数")
@Getter
@Setter
public class PageQueryDTO {
    @ApiModelProperty(value = "页码，默认为 1", example = "1")
    private Integer page;
    @ApiModelProperty(value = "页大小,默认为 10", example = "10")
    private Integer size;
    @ApiModelProperty(value = "搜索字符串")
    private String keywords;

    public PageQueryDTO() {
        this.page = 1;
        this.size = 10;
    }
}
