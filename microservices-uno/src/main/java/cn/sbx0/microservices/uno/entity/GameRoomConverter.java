package cn.sbx0.microservices.uno.entity;

import cn.sbx0.microservices.entity.Paging;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author sbx0
 * @since 2022/4/19
 */
@Mapper
public interface GameRoomConverter {
    GameRoomConverter INSTANCE = Mappers.getMapper(GameRoomConverter.class);

    GameRoomVO entityToVO(GameRoomEntity source);

    Paging<GameRoomVO> pagingEntityToVO(Paging<GameRoomEntity> source);

    GameRoomEntity dtoToEntity(GameRoomCreateDTO source);

    GameRoomInfoVO entityToInfoVO(GameRoomEntity source);
}
