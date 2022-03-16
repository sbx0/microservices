package cn.sbx0.microservices.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author sbx0
 * @since 2022/3/8
 */
@Data
public class Paging<T> implements Serializable {
    private static final long serialVersionUID = -1;

    private String code;

    private String message;

    private long total = 0;

    private List<T> data = Collections.emptyList();

    private Long page;

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

