package cn.sbx0.microservices.home.service.impl;

import cn.sbx0.microservices.entity.*;
import cn.sbx0.microservices.home.entity.*;
import cn.sbx0.microservices.home.mapper.CommunityHouseMapper;
import cn.sbx0.microservices.home.service.ICommunityHouseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 小区房子 服务实现类
 * </p>
 *
 * @author wangh
 * @since 2022-06-13
 */
@Service
public class CommunityHouseServiceImpl extends ServiceImpl<CommunityHouseMapper, CommunityHouseEntity> implements ICommunityHouseService {

    @Override
    public Paging<CommunityHouseVO> voPagingList(PageQueryDTO dto) {
        Paging<CommunityHouseEntity> entityPaging = pagingList(dto);
        return CommunityHouseConverter.INSTANCE.entityPagingToVO(entityPaging);
    }

    @Override
    public Paging<CommunityHouseEntity> pagingList(PageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getSize());
        List<CommunityHouseEntity> list = getBaseMapper().pagingList(dto);
        PageInfo<CommunityHouseEntity> pageInfo = new PageInfo<>(list);
        Paging<CommunityHouseEntity> paging = new Paging<>();
        paging.setPage((long) pageInfo.getPageNum());
        paging.setSize((long) pageInfo.getPageSize());
        paging.setTotal(pageInfo.getTotal());
        paging.setData(list);
        paging.setCode("0");
        paging.setMessage("success");
        return paging;
    }

    @Override
    public ResponseVO<TableStructure> tableStructure() {
        TableStructure tableStructure = new TableStructure();
        tableStructure.setTableName("小区房子");
        ArrayList<ColumnStructure> columns = new ArrayList<>();
        List<Map<String, String>> tableStructures = getBaseMapper().tableStructure();
        for (Map<String, String> structure : tableStructures) {
            ColumnStructure columnStructure = new ColumnStructure();
            columnStructure.setColumnName(structure.get("COLUMN_NAME"));
            columnStructure.setColumnType(structure.get("COLUMN_TYPE"));
            columnStructure.setColumnComment(structure.get("COLUMN_COMMENT"));
            columns.add(columnStructure);
        }
        tableStructure.setColumns(columns);
        return ResponseVO.success(tableStructure);
    }

    @Override
    public ResponseVO<Boolean> updateOneById(CommunityHouseEditDTO dto) {
        int resultNum = getBaseMapper().updateById(CommunityHouseConverter.INSTANCE.editDTOtoEntity(dto));
        boolean result = resultNum > 0;
        return ResponseVO.judge(result, result);
    }

    @Override
    public ResponseVO<Boolean> addOne(CommunityHouseAddDTO dto) {
        int resultNum = getBaseMapper().insert(CommunityHouseConverter.INSTANCE.saveDTOtoEntity(dto));
        boolean result = resultNum > 0;
        return ResponseVO.judge(result, result);
    }
}
