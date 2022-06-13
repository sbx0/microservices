package cn.sbx0.microservices.home.mapper;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.home.entity.CommunityHouseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 小区房子 Mapper 接口
 * </p>
 *
 * @author wangh
 * @since 2022-06-13
 */
public interface CommunityHouseMapper extends BaseMapper<CommunityHouseEntity> {

    @MapKey("COLUMN_NAME")
    List<Map<String, String>> tableStructure();

    List<CommunityHouseEntity> pagingList(PageQueryDTO dto);
}
