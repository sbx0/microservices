package cn.sbx0.microservices.home.controller;


import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.entity.TableStructure;
import cn.sbx0.microservices.home.entity.CommunityEditDTO;
import cn.sbx0.microservices.home.entity.CommunityEntity;
import cn.sbx0.microservices.home.mapper.CommunityMapper;
import cn.sbx0.microservices.home.service.impl.CommunityServiceImpl;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangh
 * @since 2022-06-09
 */
@RestController
@RequestMapping("/community")
public class CommunityController extends BaseController<CommunityServiceImpl, CommunityMapper, CommunityEntity> {
    @GetMapping("/table/structure")
    public ResponseVO<TableStructure> tableStructure() {
        return service.tableStructure();
    }

    @PostMapping("/updateOneById")
    public ResponseVO<Boolean> updateOneById(@RequestBody CommunityEditDTO dto) {
        return service.updateOneById(dto);
    }
}

