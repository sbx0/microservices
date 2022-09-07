package cn.sbx0.microservices.todo.entity.mission;

import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.todo.entity.MissionInfoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author sbx0
 * @since 2022/9/7
 */
@Mapper
public interface MissionInfoConverter {
    MissionInfoConverter INSTANCE = Mappers.getMapper(MissionInfoConverter.class);

    Paging<MissionsInfoVO> entityPagingToVO(Paging<MissionInfoEntity> source);

    MissionsInfoVO entityToVO(MissionInfoEntity source);
}
