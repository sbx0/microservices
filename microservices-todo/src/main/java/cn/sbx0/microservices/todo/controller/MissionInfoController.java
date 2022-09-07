package cn.sbx0.microservices.todo.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.todo.entity.MissionInfoEntity;
import cn.sbx0.microservices.todo.entity.mission.MissionsInfoVO;
import cn.sbx0.microservices.todo.mapper.MissionInfoMapper;
import cn.sbx0.microservices.todo.service.impl.MissionInfoServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 任务信息 前端控制器
 * </p>
 *
 * @author sbx0
 * @since 2022-09-07
 */
@RestController
@RequestMapping("/missions")
public class MissionInfoController extends BaseController<MissionInfoServiceImpl, MissionInfoMapper, MissionInfoEntity> {
    @PostMapping("/paging")
    public Paging<MissionsInfoVO> pagingList(@RequestBody PageQueryDTO dto) {
        return service.voPagingList(StpUtil.getLoginIdAsLong(), dto);
    }
}

