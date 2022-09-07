package cn.sbx0.microservices.todo.service.impl;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.entity.QueryFilterDTO;
import cn.sbx0.microservices.entity.QueryOrderDTO;
import cn.sbx0.microservices.todo.entity.MissionInfoEntity;
import cn.sbx0.microservices.todo.entity.mission.MissionInfoConverter;
import cn.sbx0.microservices.todo.entity.mission.MissionsInfoVO;
import cn.sbx0.microservices.todo.mapper.MissionInfoMapper;
import cn.sbx0.microservices.todo.service.IMissionInfoService;
import cn.sbx0.microservices.utils.NameUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 任务信息 服务实现类
 * </p>
 *
 * @author sbx0
 * @since 2022-09-07
 */
@Service
public class MissionInfoServiceImpl extends ServiceImpl<MissionInfoMapper, MissionInfoEntity> implements IMissionInfoService {
    private static final String USER_ID = "user_id";

    @Override
    public Paging<MissionsInfoVO> voPagingList(Long userId, PageQueryDTO dto) {
        List<QueryOrderDTO> orders = dto.getOrders();
        if (dto.getOrders().isEmpty()) {
            orders = new ArrayList<>();
        }
        for (QueryOrderDTO order : orders) {
            order.setField(NameUtils.humpToLine(order.getField()));
        }
        dto.setOrders(orders);

        List<QueryFilterDTO> filters = dto.getFilters();
        if (dto.getFilters().isEmpty()) {
            filters = new ArrayList<>();
        }
        // just this user
        filters.add(new QueryFilterDTO(USER_ID, userId.toString()));
        for (QueryFilterDTO filter : filters) {
            filter.setField(NameUtils.humpToLine(filter.getField()));
        }
        dto.setFilters(filters);

        Paging<MissionInfoEntity> entityPaging = pagingList(dto);
        return MissionInfoConverter.INSTANCE.entityPagingToVO(entityPaging);
    }

    @Override
    public Paging<MissionInfoEntity> pagingList(PageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getSize());
        List<MissionInfoEntity> list = getBaseMapper().pagingList(dto);
        PageInfo<MissionInfoEntity> pageInfo = new PageInfo<>(list);
        Paging<MissionInfoEntity> paging = new Paging<>();
        paging.setPage((long) pageInfo.getPageNum());
        paging.setSize((long) pageInfo.getPageSize());
        paging.setTotal(pageInfo.getTotal());
        paging.setData(list);
        paging.setCode("0");
        paging.setMessage("success");
        return paging;
    }
}
