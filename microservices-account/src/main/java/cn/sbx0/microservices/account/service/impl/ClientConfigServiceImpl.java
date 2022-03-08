package cn.sbx0.microservices.account.service.impl;

import cn.sbx0.microservices.account.entity.ClientConfigEntity;
import cn.sbx0.microservices.account.mapper.ClientConfigMapper;
import cn.sbx0.microservices.account.service.IClientConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangh
 * @since 2022-03-08
 */
@Service
public class ClientConfigServiceImpl extends ServiceImpl<ClientConfigMapper, ClientConfigEntity> implements IClientConfigService {

}
