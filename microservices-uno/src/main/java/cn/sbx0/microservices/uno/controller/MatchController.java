package cn.sbx0.microservices.uno.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.MatchExpectDTO;
import cn.sbx0.microservices.uno.service.IMatchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sbx0
 * @since 2022/5/11
 */
@RestController
@RequestMapping("/match")
public class MatchController {
    @Resource
    private IMatchService matchService;

    @PostMapping("/join")
    public ResponseVO<Boolean> join(@RequestBody MatchExpectDTO dto) {
        return matchService.join(dto);
    }

    @PostMapping("/quit")
    public ResponseVO<Boolean> quit() {
        return matchService.quit(StpUtil.getLoginIdAsLong());
    }

    @PostMapping("/info")
    public ResponseVO<Integer> info() {
        return matchService.getQueueInfo();
    }
}
