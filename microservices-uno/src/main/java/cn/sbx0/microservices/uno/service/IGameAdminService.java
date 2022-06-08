package cn.sbx0.microservices.uno.service;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.uno.entity.GameRoomVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangh
 * @since 2022-05-27
 */
public interface IGameAdminService {
    Paging<GameRoomVO> roomPagingList(PageQueryDTO dto);
}
