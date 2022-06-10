package cn.sbx0.microservices.home.entity;


import cn.sbx0.microservices.entity.Paging;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author sbx0
 * @since 2022/6/9
 */
@Mapper
public interface CommunityConverter {
    CommunityConverter INSTANCE = Mappers.getMapper(CommunityConverter.class);

    Paging<CommunityVO> entityPagingToVO(Paging<CommunityEntity> source);

    CommunityEntity editDTOtoEntity(CommunityEditDTO source);

    CommunityEntity saveDTOtoEntity(CommunityAddDTO dto);
}
