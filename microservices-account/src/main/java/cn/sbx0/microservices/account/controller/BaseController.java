package cn.sbx0.microservices.account.controller;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.Serializable;
import java.util.List;

/**
 * @author sbx0
 * @since 2022/3/4
 */
public class BaseController<S extends ServiceImpl<M, T>, M extends BaseMapper<T>, T> {
    @Autowired
    protected S service;

    @ApiOperation(value = "根据 ID 查询详情")
    @GetMapping("/getById/{id}")
    public T getById(@PathVariable("id") Serializable id) {
        return service.getById(id);
    }

    @ApiOperation(value = "列表")
    @GetMapping("/list")
    public Paging<T> list(PageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getSize());
        List<T> list = service.list();
        PageInfo<T> pageInfo = new PageInfo<>(list);
        Paging<T> paging = new Paging<>();
        paging.setPage((long) pageInfo.getPageNum());
        paging.setSize((long) pageInfo.getPageSize());
        paging.setTotal(pageInfo.getTotal());
        paging.setData(list);
        return paging;
    }
}
