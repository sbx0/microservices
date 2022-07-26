package cn.sbx0.microservices.bot.service;


import cn.sbx0.microservices.bot.http.entity.GetFundNetDiagramResponse;
import cn.sbx0.microservices.bot.http.entity.RealTimeEastMoneyResponse;

import java.util.List;

/**
 * @author sbx0
 * @since 2022/7/25
 */
public interface IGoldenService {
    void readData();

    String handleData(List<GetFundNetDiagramResponse> data);

    String handleData(RealTimeEastMoneyResponse data);
}
