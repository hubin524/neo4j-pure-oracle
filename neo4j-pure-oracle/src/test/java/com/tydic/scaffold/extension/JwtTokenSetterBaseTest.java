package com.tydic.scaffold.extension;

import com.tydic.framework.oauth2.helper.AbstractOauth2HelperBean;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author jianghs
 * @version 1.0.0
 * @Description 为单元测试，设置jwtToken
 * @createTime 2020-01-06 15:56
 */
public class JwtTokenSetterBaseTest {
    protected String jwtToken;

    @Autowired
    protected AbstractOauth2HelperBean oauth2HelperBean;

    @BeforeAll
    void prepareEnvData() {
        String loginAccount = "test002";
        String accessTokenValue = "";
        jwtToken = oauth2HelperBean.genJwtToken(loginAccount,accessTokenValue);
    }
}
