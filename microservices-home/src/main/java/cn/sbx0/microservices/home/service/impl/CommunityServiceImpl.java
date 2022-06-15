package cn.sbx0.microservices.home.service.impl;

import cn.sbx0.microservices.entity.*;
import cn.sbx0.microservices.home.entity.*;
import cn.sbx0.microservices.home.mapper.CommunityMapper;
import cn.sbx0.microservices.home.service.ICommunityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangh
 * @since 2022-06-09
 */
@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, CommunityEntity> implements ICommunityService {

    @Override
    public ResponseVO<TableStructure> tableStructure() {
        TableStructure tableStructure = new TableStructure();
        tableStructure.setTableName("小区");
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
    public Paging<CommunityVO> voPagingList(PageQueryDTO dto) {
        Paging<CommunityEntity> communityEntityPaging = pagingList(dto);
        return CommunityConverter.INSTANCE.entityPagingToVO(communityEntityPaging);
    }

    @Override
    public Paging<CommunityEntity> pagingList(PageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getSize());
        List<CommunityEntity> list = getBaseMapper().pagingList(dto);
        PageInfo<CommunityEntity> pageInfo = new PageInfo<>(list);
        Paging<CommunityEntity> paging = new Paging<>();
        paging.setPage((long) pageInfo.getPageNum());
        paging.setSize((long) pageInfo.getPageSize());
        paging.setTotal(pageInfo.getTotal());
        paging.setData(list);
        paging.setCode("0");
        paging.setMessage("success");
        return paging;
    }

    @Override
    public ResponseVO<Boolean> updateOneById(CommunityEditDTO dto) {
        int resultNum = getBaseMapper().updateById(CommunityConverter.INSTANCE.editDTOtoEntity(dto));
        boolean result = resultNum > 0;
        return ResponseVO.judge(result, result);
    }

    @Override
    public ResponseVO<Boolean> addOne(CommunityAddDTO dto) {
        int resultNum = getBaseMapper().insert(CommunityConverter.INSTANCE.saveDTOtoEntity(dto));
        boolean result = resultNum > 0;
        return ResponseVO.judge(result, result);
    }
}
