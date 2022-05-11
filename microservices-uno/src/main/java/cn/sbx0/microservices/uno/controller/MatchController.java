package cn.sbx0.microservices.uno.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.MatchExpectDTO;
import cn.sbx0.microservices.uno.entity.QueueInfoVO;
import cn.sbx0.microservices.uno.service.IMatchService;
import org.springframework.web.bind.annotation.*;

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
        dto.setUserId(StpUtil.getLoginIdAsLong());
        return matchService.join(dto);
    }

    @PostMapping("/quit")
    public ResponseVO<Boolean> quit() {
        return matchService.quit(StpUtil.getLoginIdAsLong());
    }

    @GetMapping("/info")
    public ResponseVO<QueueInfoVO> info() {
        return matchService.getQueueInfo();
    }
}
