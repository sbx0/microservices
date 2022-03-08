package cn.sbx0.microservices.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author sbx0
 * @since 2022/3/8
 */
@Data
@ApiModel("分页结果对象")
public class Paging<T> implements Serializable {
    private static final long serialVersionUID = -1;

    @ApiModelProperty("总行数")
    private long total = 0;

    @ApiModelProperty("数据列表")
    private List<T> data = Collections.emptyList();

    @ApiModelProperty(value = "页码")
    private Long page;

    @ApiModelProperty(value = "页大小")
    private Long size;

    public Paging() {

    }

    public Paging(IPage<T> page) {
        this.total = page.getTotal();
        this.data = page.getRecords();
        this.page = page.getCurrent();
        this.size = page.getSize();
    }

}

