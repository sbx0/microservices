package cn.sbx0.microservices.home.service;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.home.entity.CommunityVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangh
 * @since 2022-06-09
 */
public interface IHomeService {
    Paging<CommunityVO> communityPagingList(PageQueryDTO dto);
}
