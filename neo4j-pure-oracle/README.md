## basic_dev_platform
spring-boot 开发脚手架.

## 功能模块说明

### 认证&权限&安全
采用部分springsecurity无状态安全组件：
> http://192.168.129.159:8078/dic_tsc_pct/uam/uam-client-root/tree/master/uam-client-root
> http://192.168.129.159:8078/dic_tsc_pct/uam/uam-client-root/tree/master/uam-client-stateless
> http://192.168.129.159:8078/dic_tsc_pct/uam/uam-client-root/tree/master/uam-client-stateless-ahtel
> http://192.168.129.159:8078/dic_tsc_pct/uam/uam-client-root/tree/master/uam-client-stateless-sys3

### 数据脱敏
使用数据脱敏组件：
> http://192.168.129.159:8078/dic_tsc_pct/backend_framework/framework-springmvc-data-desensitization
### 参数校验
`JSR303`数据验证的规范。

### 动态sql
使用部门动态sql组件：
> http://192.168.129.159:8078/dic_tsc_pct/backend_framework/framework-mybatis-dynamic-sql-expansion

### 附件
提供通用的附件上传,base64附件上传,下载功能;
代码位置：`com.tydic.scaffold.common.controller.AttachmentController`

> 附件功能：需要添加业务部分代码; 

### 缓存
使用redis作为缓存中间件.
代码示例： `com.tydic.framework.oauth2.helper.AbstractOauth2HelperBean`

### 日志
使用logback作为日志框架.
并结合当前已知的平台日志规范,将日志输出到指定的目录。
代码位置：`com.tydic.framework.config.log`
>提供默认的日志格式,若不满足,则需自行扩展.

### 单元测试
采用mock测试。
测试用例见`src/test/`

> `mvn clean test site` 生成html测试报告 


### CRUD代码示例
`com.tydic.scaffold.temp` 下,提供来一个针对单表的CRUD示例。
> 首先需要执行sql脚本，创建数据库表。
> 脚本所在位置：`resources/ddl.tb_temp_demo.sql`   
>

----
----

## 补充
**swagger2 地址**
http://localhost:8081/scaffold/swagger-ui.html

### 业务接口访问流程
**maven依赖**
```xml
<dependency>
    <groupId>com.tydic.framework.uam</groupId>
    <!--  ahtel ,sys3 2选1-->
    <artifactId>uam-client-stateless-[ahtel | sys3 ]</artifactId>
    <version>x.y.z</version>
</dependency>
```
1.统一认证单点登录，获取code
请求： [web-sso][http://60.173.195.121:9906/web-sso/#/?client_id=48E0E9DFAFD2462E810C4D6144CD01BE&response_type=code&redirect_uri=http://127.0.0.1:8000]         
用户名/密码： xxx/yyy
> 请求中的参数： client_id , redirect_uri 在系统管理中配置


2. code兑换accessToken
请求： [token][http://localhost:8081/scaffold/authorization/token?code=2lTd8nu3]
响应：获取`jwtToken`

3.**获取当前用户权限信息**
`header`中设置`Authorization`，值为 `${上一步获取的jwtToken}` ,
如：请求用户信息：   [user][http://localhost:8080/scaffold/authorization/user]     

4.**业务接口请求**
参照3.

5.**获取“session”信息**
```java
//系统管理通用
OauthUserDetails oauthUserDetails = (OauthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

//针对使用安徽电信eda系统管理
AhtelOauthUserDetails oauthUserDetails = (AhtelOauthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

//接入sys3系统管理
Sys3OauthUserDetails oauthUserDetails = (Sys3OauthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

```

6.**返回数据格式**   
返回的数据为json格式,HTTPSTATUS全部返回为`200`,由返回的json中的`code`字段来标识状态信息：如下
  ```java
  ///////成功  
  {
    "code": "200",
    "message": "成功",
    "data": {
      "accessToken": "6fae10db3c92cebc59a847684e7e09aa671b1700904f1091a1415bdc21637e7edc61e9507808c078fabe421730425e9dd72a4ed507e408f360b7355564807e8187eed56ea8e633ac38e8d5f56e93d04b4ca3ab18556fab0b6b9964cb596f107e94ae80f68242dc704cd5c9b8dbc866e3c3b5dd506de5d6debd2e40729b440c5b68a74529ec53c1ee0b08ea245dede24c4cf64c5539014e7e534a5785386602fcf713a796d43b79b4c216c6244816f34db043005aa8e791843db6a4699b0e8c1cf29206bcc056bf4f61fd7adf92e277581cb47dfd6b39e7069c8dedad80ec42679585d69e12d68acd344718c2d37eafed2cfc5704480bfc07fe8f01c38768e67780bdee7396f3b7f1ff62ec9a602ec25c40e88d258d173950f092fd0dc1b0ed9d5cbbb29f3dfb8d10220781e691c7a387202986e63c6ff379a2a9c6d61b0ffa9ef1ea6e883812ab143fbd44c97a6c0884d11e15f35fd358fd7824f8ae4ea3d6b051c771c3d67a39ec3ecad422d531535816777f9f2cd9ca69eba4d53c209cb3b26cdab5a6cf1c425e78f889fa796a21600276472244a4e00839f8cc7f3b75152d91df279d5483b01293e14b3c19231574d070adcad3326e1e953ff799970124f035738a531c8242458796098cecd1cb21eef9d3a558921c732e02d373ad436366eccdeeca00bd1a72cf4932d347143a16f85198906b62d666457dab09e55f0578",
      "jwtToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2JpbGUiLCJsb2dpbkFjY291bnQiOiJ0ZXN0MDAyIiwiaXNzIjoidHlkaWMtY2hpbmEtYWgtdGVsIiwiZXhwIjoxNTU4NjkxMDg1fQ.LJUhoPFEiDqyKlpDQtxgWoD9-sPQpe54aPwxe3vwSYc"
    }
  }
  
  
  {
    "code": "401",
    "message": "未认证",
    "data": ""
  }
  
 {
   "code": "500",
   "message": "服务器错误",
   "data": null
 }
```

> [1.统一认证.gif](http://192.168.129.159:8078/dic_tsc_pct/uam/uam-server/blob/master/test_1.gif)  
> [2.单点登录.gif](http://192.168.129.159:8078/dic_tsc_pct/uam/uam-server/blob/master/test_2.gif)  
>

### demo样例
- `com.tydic.ifs.scaffold.temp.controller.DemoController`
- `com.tydic.ifs.scaffold.temp.service.impl.DemoServiceImpl`
- `com.tydic.ifs.scaffold.temp.daoDemoMapper`
- `resources/mapper/DemoMapper.xml`


### 附录1 --- 针对集成uam-client-stateless-ahtel, 配置 EDA系统管理

```YML
### EDA系统管理相关配置
eda:
  ws:
    url: http://192.168.128.107:7002/edaws/service/DBPService
  #### 获取用户信息的app_id
  app:
    id: 228
  #### 获取用户数据权限的app_id
  ### 通常 eda.app.id == eda.data_center.app.id ,但是有部分新建系统，如果给用户重新赋权一遍数据权限工作量会比较大， 所以就复用已有系统的数据权限(政商校就是此策略)
  data_center:
    app:
      id: 228
```




### 附录2 --- 针对集成 uam-client-stateless-sys3 ,自研系统管理配置
```YML
### 自研系统管理相关配置
sysmanager:
  request-url: http://192.168.129.42:8803/service
```
