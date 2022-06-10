package cn.sbx0.microservices.home.mapper;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.home.entity.CommunityEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wangh
 * @since 2022-06-09
 */
public interface CommunityMapper extends BaseMapper<CommunityEntity> {
    List<CommunityEntity> pagingList(PageQueryDTO dto);

    @MapKey("COLUMN_NAME")
    List<Map<String, String>> tableStructure();
}
