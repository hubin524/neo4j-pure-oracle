package com.tydic.scaffold;

import com.tydic.core.rational.Return;
import com.tydic.core.rational.ReturnCode;
import com.tydic.core.util.json.JSON;
import com.tydic.framework.spring.security.SecurityConstants;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019-08-05 15:00
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Disabled
public class Oauth2ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 测试token兑换jwtToken接口
     *
     * @throws Exception
     */
    @Test
    public void token() throws Exception {
        //todo  code有效期为 30min;
        String code = "261798b580da9cb374fc6e78764f26e9";

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/authorization/token").param("code", code).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("获取响应信息为：\n" + mvcResult.getResponse().getContentAsString());
    }

    /**
     * 模拟获取用户信息接口
     *
     * @throws Exception
     */
    @Test
    public void user() throws Exception {
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2JpbGUiLCJsb2dpbkFjY291bnQiOiJ0ZXN0MDAyIiwiaXNzIjoidHlkaWMtY2hpbmEtYWgtdGVsIiwiZXhwIjoxNTcyMjQ5MjA4fQ.Huv-oEDjuPCoUHC45ItxnTzlWr9Yaa5C4S6ELe-cSoU";

        mockMvc.perform(MockMvcRequestBuilders.get("/authorization/user").header(SecurityConstants.JWT_HEADER_AUTHORIZATION_CODE, jwtToken).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();

                    System.out.println("获取响应信息为：\n" + json);
                    Return actual = JSON.parse(json, Return.class);
                    Assert.assertEquals(actual.getCode(), ReturnCode.SUCCESS);
                });
    }
}
