package cn.sbx0.microservices.account;

import cn.dev33.satoken.stp.StpUtil;
import org.mockito.MockedStatic;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * @author sbx0
 * @since 2022/5/16
 */
public class TestDataProvider {
    public final static Long USER_ID = 1L;
    public static MockedStatic<StpUtil> stpUtil;
    public static MockedStatic<BCrypt> bcrypt;
}
