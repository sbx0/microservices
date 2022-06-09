package cn.sbx0.microservices.home.controller;


import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.home.entity.CommunityVO;
import cn.sbx0.microservices.home.service.IHomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangh
 * @since 2022-06-09
 */
@RestController
@RequestMapping("/")
public class HomeController {
    @Resource
    private IHomeService service;

    @GetMapping("/communities")
    public Paging<CommunityVO> communityPagingList(PageQueryDTO dto) {
        return service.communityPagingList(dto);
    }
}

