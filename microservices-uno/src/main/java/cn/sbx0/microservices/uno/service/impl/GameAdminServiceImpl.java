
package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.uno.entity.GameRoomConverter;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.entity.GameRoomVO;
import cn.sbx0.microservices.uno.feign.AccountService;
import cn.sbx0.microservices.uno.service.IGameAdminService;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sbx0
 * @since 2022/5/27
 */
@Slf4j
@Service
public class GameAdminServiceImpl implements IGameAdminService {
    @Resource
    private IGameRoomService roomService;
    @Resource
    private IGameRoomUserService gamerService;
    @Resource
    private IGameCardService cardService;
    @Resource
    private AccountService accountService;


    @Override
    public Paging<GameRoomVO> roomPagingList(PageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getSize());
        List<GameRoomEntity> list = roomService.pagingList(dto.getKeyword());
        PageInfo<GameRoomEntity> pageInfo = new PageInfo<>(list);
        Paging<GameRoomVO> paging = new Paging<>();
        paging.setPage((long) pageInfo.getPageNum());
        paging.setSize((long) pageInfo.getPageSize());
        paging.setTotal(pageInfo.getTotal());
        paging.setData(GameRoomConverter.INSTANCE.listEntityToVO(list));
        paging.setCode("0");
        paging.setMessage("success");
        return paging;
    }
}
