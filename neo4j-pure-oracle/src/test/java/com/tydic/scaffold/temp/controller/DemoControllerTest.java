package com.tydic.scaffold.temp.controller;

import com.tydic.core.rational.Return;
import com.tydic.core.rational.ReturnCode;
import com.tydic.core.util.json.JSON;
import com.tydic.framework.oauth2.helper.AbstractOauth2HelperBean;
import com.tydic.framework.spring.security.SecurityConstants;
import com.tydic.scaffold.extension.JwtTokenSetterBaseTest;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Rollback
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles({"dev"})
public class DemoControllerTest  extends JwtTokenSetterBaseTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void qryDemo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/temp/demo")
                .header(SecurityConstants.JWT_HEADER_AUTHORIZATION_CODE, jwtToken)
                .param("demoName", "测试")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(handler);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "9010",
            "9011",
            "9012"})
    public void getDemo(String demoId) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/temp/demo/" + demoId)
                .header(SecurityConstants.JWT_HEADER_AUTHORIZATION_CODE, jwtToken)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(handler);
    }

    @Test
    public void save() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/temp/demo")
                .header(SecurityConstants.JWT_HEADER_AUTHORIZATION_CODE, jwtToken)
                .param("demoName", "测试"+new Random().nextInt(100))
                .param("age", "18")
                .param("birthdate", "2018-08-08")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(handler);
    }

    @Test
    public void update() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/temp/demo")
                .header(SecurityConstants.JWT_HEADER_AUTHORIZATION_CODE, jwtToken)
                .param("demoId", "9010")
                .param("age", "99")
                .param("birthdate", "2011-11-1")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(handler);
    }

    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/temp/demo/state/9011")
                .header(SecurityConstants.JWT_HEADER_AUTHORIZATION_CODE, jwtToken)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(handler);
    }


    ResultHandler handler = result -> {
        String json = result.getResponse().getContentAsString();

        Return actual = JSON.parse(json, Return.class);
        Assert.assertEquals(actual.getCode(), ReturnCode.SUCCESS);

        Object data = actual.getData();
        System.out.println("获取响应信息data为：\n" + data);
    };
}