package cn.sbx0.microservices.home.service.impl;

import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.entity.QueryOrderDTO;
import cn.sbx0.microservices.home.entity.CommunityVO;
import cn.sbx0.microservices.home.service.ICommunityService;
import cn.sbx0.microservices.home.service.IHomeService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangh
 * @since 2022-06-09
 */
@Service
public class HomeServiceImpl implements IHomeService {
    @Resource
    private ICommunityService communityService;

    private static Pattern linePattern = Pattern.compile("_(\\w)");
    private static Pattern humpPattern = Pattern.compile("[A-Z]");


    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString().toUpperCase();
    }

    @Override
    public Paging<CommunityVO> communityPagingList(PageQueryDTO dto) {
        List<QueryOrderDTO> orders = dto.getOrders();
        if (!CollectionUtils.isEmpty(orders)) {
            for (QueryOrderDTO order : orders) {
                order.setField(humpToLine(order.getField()));
            }
        }
        return communityService.voPagingList(dto);
    }
}
