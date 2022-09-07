package cn.sbx0.microservices.todo.service;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.todo.entity.MissionInfoEntity;
import cn.sbx0.microservices.todo.entity.mission.MissionsInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 任务信息 服务类
 * </p>
 *
 * @author sbx0
 * @since 2022-09-07
 */
public interface IMissionInfoService extends IService<MissionInfoEntity> {
    Paging<MissionsInfoVO> voPagingList(Long userId, PageQueryDTO dto);

    Paging<MissionInfoEntity> pagingList(PageQueryDTO dto);
}
