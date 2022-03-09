package cn.sbx0.microservices.controller;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;

/**
 * @author sbx0
 * @since 2022/3/4
 */
public class BaseController<S extends ServiceImpl<M, T>, M extends BaseMapper<T>, T> {
    @Autowired
    protected S service;

    @PostMapping("/save")
    public Boolean saveOne(@RequestBody T t) {
        return service.save(t);
    }

    @GetMapping("/getById/{id}")
    public T getById(@PathVariable("id") Serializable id) {
        return service.getById(id);
    }

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
