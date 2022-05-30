package cn.sbx0.microservices.uno.service;

import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.GameInfoVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangh
 * @since 2022-05-27
 */
public interface IGameService {
    ResponseVO<GameInfoVO> getInfoByCodeAndUserId(String code, Long userId);
}
