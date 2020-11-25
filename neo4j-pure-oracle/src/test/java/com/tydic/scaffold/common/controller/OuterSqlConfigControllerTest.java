package com.tydic.scaffold.common.controller;

import com.tydic.framework.oauth2.helper.AbstractOauth2HelperBean;
import com.tydic.framework.spring.security.SecurityConstants;
import com.tydic.scaffold.extension.JwtTokenSetterBaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OuterSqlConfigControllerTest extends JwtTokenSetterBaseTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void qryByDynamicSql() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/common/outerSqlConfig/qry/TEST_DEMO")
                .header(SecurityConstants.JWT_HEADER_AUTHORIZATION_CODE, jwtToken)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                });
    }
}