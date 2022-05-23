package cn.sbx0.microservices.account.controller;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.account.mapper.AccountMapper;
import cn.sbx0.microservices.account.service.impl.AccountServiceImpl;
import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.entity.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author wangh
 * @since 2022-03-04
 */
@RestController
@RequestMapping("/")
public class AccountController extends BaseController<AccountServiceImpl, AccountMapper, AccountEntity> {

    @PostMapping("login")
    public ResponseVO<SaTokenInfo> login(@RequestBody LoginDTO dto) {
        return service.login(dto);
    }

    @PostMapping("/logout")
    public void logout() {
        service.logout();
    }

    @PostMapping("/register")
    public ResponseVO<Boolean> register(@RequestBody LoginDTO dto) {
        return service.register(dto);
    }

    @GetMapping("/loginInfo")
    public ResponseVO<AccountVO> loginInfo() {
        return new ResponseVO<>(ResponseVO.SUCCESS, service.loginInfo(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/findByUserName")
    public AccountVO findByUserName(@RequestParam(value = "name") String name) {
        return service.findByUserName(name);
    }

    @GetMapping(value = "/findById")
    public AccountVO findById(@RequestParam(value = "id") Long id) {
        return service.loginInfo(id);
    }

    @PostMapping("/mapNameByIds")
    public Map<Long, String> mapNameByIds(@RequestBody IDsDTO dto) {
        return service.mapNameByIds(dto);
    }
}
