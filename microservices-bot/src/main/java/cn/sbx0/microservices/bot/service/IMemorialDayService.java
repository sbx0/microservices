package cn.sbx0.microservices.bot.service;

import cn.sbx0.microservices.bot.entity.MemorialDayEntity;

import java.util.List;

/**
 * @author sbx0
 * @since 2022/8/2
 */
public interface IMemorialDayService {
    void handleData(List<MemorialDayEntity> days);
}
