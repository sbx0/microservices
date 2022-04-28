package cn.sbx0.microservices.account.controller;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.account.mapper.AccountMapper;
import cn.sbx0.microservices.account.service.impl.AccountServiceImpl;
import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.entity.AccountEntity;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.entity.LoginDTO;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangh
 * @since 2022-03-04
 */
@RestController
@RequestMapping("/")
public class AccountController extends BaseController<AccountServiceImpl, AccountMapper, AccountEntity> {

    @PostMapping("/login")
    public SaTokenInfo login(@RequestBody LoginDTO dto) {
        return service.login(dto);
    }

    @PostMapping("/logout")
    public void logout() {
        service.logout();
    }

    @PostMapping("/register")
    public Boolean register(@RequestBody LoginDTO dto) {
        return service.register(dto);
    }

    @GetMapping("/user/loginInfo")
    public AccountVO loginInfo() {
        return service.loginInfo(StpUtil.getLoginIdAsLong());
    }

    @GetMapping("/findByUserName")
    public AccountVO findByUserName(@RequestParam(value = "name") String name) {
        return service.findByUserName(name);
    }
}
