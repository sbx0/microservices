package cn.sbx0.microservices.uno.service;

import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.MatchExpectDTO;

/**
 * @author sbx0
 * @since 2022/5/10
 */
public interface IMatchService {
    // join match queue
    ResponseVO<Boolean> join(MatchExpectDTO dto);

    // quit match queue
    ResponseVO<Boolean> quit(Long userId);

    // get match queue info
    ResponseVO<Integer> getQueueInfo();
}
