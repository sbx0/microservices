package cn.sbx0.microservices.home.service;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.entity.TableStructure;
import cn.sbx0.microservices.home.entity.CommunityAddDTO;
import cn.sbx0.microservices.home.entity.CommunityEditDTO;
import cn.sbx0.microservices.home.entity.CommunityEntity;
import cn.sbx0.microservices.home.entity.CommunityVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangh
 * @since 2022-06-09
 */
public interface ICommunityService extends IService<CommunityEntity> {
    ResponseVO<TableStructure> tableStructure();

    Paging<CommunityVO> voPagingList(PageQueryDTO dto);

    Paging<CommunityEntity> pagingList(PageQueryDTO dto);

    ResponseVO<Boolean> updateOneById(CommunityEditDTO dto);

    ResponseVO<Boolean> addOne(CommunityAddDTO dto);
}
