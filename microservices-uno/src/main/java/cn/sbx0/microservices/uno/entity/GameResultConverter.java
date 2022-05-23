package cn.sbx0.microservices.uno.entity;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author sbx0
 * @since 2022/4/19
 */
@Mapper
public interface GameResultConverter {
    GameResultConverter INSTANCE = Mappers.getMapper(GameResultConverter.class);

    GameResultVO entityToVO(GameResultEntity source);

    List<GameResultVO> entitiesToVOs(List<GameResultEntity> source);
}
