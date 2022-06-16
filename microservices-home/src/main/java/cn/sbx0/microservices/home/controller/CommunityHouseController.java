package cn.sbx0.microservices.home.controller;


import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.entity.TableStructure;
import cn.sbx0.microservices.home.entity.CommunityHouseAddDTO;
import cn.sbx0.microservices.home.entity.CommunityHouseEditDTO;
import cn.sbx0.microservices.home.entity.CommunityHouseEntity;
import cn.sbx0.microservices.home.mapper.CommunityHouseMapper;
import cn.sbx0.microservices.home.service.impl.CommunityHouseServiceImpl;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 小区房子 前端控制器
 * </p>
 *
 * @author wangh
 * @since 2022-06-13
 */
@RestController
@RequestMapping("/house")
public class CommunityHouseController extends BaseController<CommunityHouseServiceImpl, CommunityHouseMapper, CommunityHouseEntity> {
    @GetMapping("/table/structure")
    public ResponseVO<TableStructure> tableStructure() {
        return service.tableStructure();
    }

    @PostMapping("/updateOneById")
    public ResponseVO<Boolean> updateOneById(@RequestBody CommunityHouseEditDTO dto) {
        return service.updateOneById(dto);
    }

    @PostMapping("/addOne")
    public ResponseVO<Boolean> addOne(@RequestBody CommunityHouseAddDTO dto) {
        return service.addOne(dto);
    }
}

