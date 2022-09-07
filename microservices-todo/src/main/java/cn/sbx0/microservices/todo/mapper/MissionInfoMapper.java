package cn.sbx0.microservices.todo.mapper;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.todo.entity.MissionInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 任务信息 Mapper 接口
 * </p>
 *
 * @author sbx0
 * @since 2022-09-07
 */
public interface MissionInfoMapper extends BaseMapper<MissionInfoEntity> {
    List<MissionInfoEntity> pagingList(PageQueryDTO dto);
}
