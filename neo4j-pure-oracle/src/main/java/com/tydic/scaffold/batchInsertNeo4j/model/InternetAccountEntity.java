package com.tydic.scaffold.batchInsertNeo4j.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: InternetAccountEntity
 * @Description 互联网账号实体类
 * @Author: hubin
 * @Date: 2020-08-18
 * @Version:v1.0
 */
@Data
public class InternetAccountEntity implements Serializable {
    private String qqId;
    private String emailId;
    private String wechatId;
    private String jdId;
    private String taobaoId;
    private String weiboId;
    private String youkuId;
    private String aqiyiId;
    private String oneidPrdInstId;
    private String serviceNbr;
    private String prdInstId;
    private String latnId;
    private String useraccount;
    private String model;
    private String encryptOneidPrdInstId;


}
