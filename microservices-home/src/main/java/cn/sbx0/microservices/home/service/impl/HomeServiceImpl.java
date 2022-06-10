package cn.sbx0.microservices.home.service.impl;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.home.entity.CommunityVO;
import cn.sbx0.microservices.home.service.ICommunityService;
import cn.sbx0.microservices.home.service.IHomeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangh
 * @since 2022-06-09
 */
@Service
public class HomeServiceImpl implements IHomeService {
    @Resource
    private ICommunityService communityService;

    @Override
    public Paging<CommunityVO> communityPagingList(PageQueryDTO dto) {
        return communityService.voPagingList(dto);
    }
}
