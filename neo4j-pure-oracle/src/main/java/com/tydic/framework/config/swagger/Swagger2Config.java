package com.tydic.framework.config.swagger;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author qxjiao
 * @date 2019/2/26
 */
@Profile({"dev","test"})
@Configuration
@EnableSwagger2
public class Swagger2Config {

  @Bean
  public Docket createRestApi() {
    ParameterBuilder ticketPar = new ParameterBuilder();
    List<Parameter> pars = new ArrayList<Parameter>();
    ticketPar.name("Authorization").description("用户Authorization token")
            .modelRef(new ModelRef("string")).parameterType("header")
            .required(true).build(); //header中的ticket参数非必填，传空也可以
    pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数

    return new Docket(DocumentationType.SWAGGER_2)
            .select()
            //扫描所有有注解的api
            .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
            .paths(PathSelectors.any())
            .build().globalOperationParameters(pars).apiInfo(apiInfo());
  }
  @Bean
  public ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("政商校 后端服务 RESTful APIs")
        .description("接口统一返回对象定义说明:code:返回码 （200-成功 "
            + "500-错误），message:返回状态数据，data:返回业务数据对象")
        .termsOfServiceUrl("http://www.tydic.com")
        .version("1.0.0")
        .license("LICENSE")
        .licenseUrl("http://www.tydic.com")
        .build();
  }

}
