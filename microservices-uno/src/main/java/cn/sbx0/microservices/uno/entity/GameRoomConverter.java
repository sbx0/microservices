package cn.sbx0.microservices.uno.entity;

import cn.sbx0.microservices.entity.Paging;
import org.mapstruct.Mapper;

/**
 * @author sbx0
 * @since 2022/4/19
 */
@Mapper(componentModel = "spring")
public interface GameRoomConverter {
    GameRoomVO map(GameRoomEntity source);

    Paging<GameRoomVO> pagingEntityToVO(Paging<GameRoomEntity> source);

    GameRoomEntity dtoToEntity(GameRoomCreateDTO source);

    GameRoomInfoVO entityToVO(GameRoomEntity source);
}
