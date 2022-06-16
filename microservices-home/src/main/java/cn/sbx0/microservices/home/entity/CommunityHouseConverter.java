package cn.sbx0.microservices.home.entity;


import cn.sbx0.microservices.entity.Paging;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author sbx0
 * @since 2022/6/9
 */
@Mapper
public interface CommunityHouseConverter {
    CommunityHouseConverter INSTANCE = Mappers.getMapper(CommunityHouseConverter.class);

    Paging<CommunityHouseVO> entityPagingToVO(Paging<CommunityHouseEntity> source);

    CommunityHouseEntity editDTOtoEntity(CommunityHouseEditDTO source);

    CommunityHouseEntity saveDTOtoEntity(CommunityHouseAddDTO dto);
}
