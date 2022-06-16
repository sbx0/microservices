package cn.sbx0.microservices.home.service;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.entity.TableStructure;
import cn.sbx0.microservices.home.entity.CommunityHouseAddDTO;
import cn.sbx0.microservices.home.entity.CommunityHouseEditDTO;
import cn.sbx0.microservices.home.entity.CommunityHouseEntity;
import cn.sbx0.microservices.home.entity.CommunityHouseVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 小区房子 服务类
 * </p>
 *
 * @author wangh
 * @since 2022-06-13
 */
public interface ICommunityHouseService extends IService<CommunityHouseEntity> {

    ResponseVO<TableStructure> tableStructure();

    ResponseVO<Boolean> updateOneById(CommunityHouseEditDTO dto);

    ResponseVO<Boolean> addOne(CommunityHouseAddDTO dto);

    Paging<CommunityHouseEntity> pagingList(PageQueryDTO dto);

    Paging<CommunityHouseVO> voPagingList(PageQueryDTO dto);
}
