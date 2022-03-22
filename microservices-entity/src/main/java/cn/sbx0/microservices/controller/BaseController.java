package cn.sbx0.microservices.controller;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sbx0
 * @since 2022/3/4
 */
public class BaseController<S extends ServiceImpl<M, T>, M extends BaseMapper<T>, T> {
    @Autowired
    protected S service;

    public Boolean saveOne(@RequestBody T t) {
        return service.save(t);
    }

    public T getById(@PathVariable("id") Serializable id) {
        return desensitization(service.getById(id));
    }

    public Paging<T> list(PageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getSize());
        List<T> list = desensitization(service.list());
        PageInfo<T> pageInfo = new PageInfo<>(list);
        Paging<T> paging = new Paging<>();
        paging.setPage((long) pageInfo.getPageNum());
        paging.setSize((long) pageInfo.getPageSize());
        paging.setTotal(pageInfo.getTotal());
        paging.setData(list);
        paging.setCode("0");
        paging.setMessage("success");
        return paging;
    }

    public T desensitization(T t) {
        return t;
    }

    public Object desensitization(T t, Class clazz) {
        try {
            Object target = clazz.newInstance();
            BeanUtils.copyProperties(t, target);
            return target;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    public List<T> desensitization(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, desensitization(list.get(i)));
        }
        return list;
    }

    public List<Object> desensitization(List<T> source, Class clazz) {
        List<Object> target = new ArrayList<>();
        for (T t : source) {
            target.add(desensitization(t, clazz));
        }
        return target;
    }
}
