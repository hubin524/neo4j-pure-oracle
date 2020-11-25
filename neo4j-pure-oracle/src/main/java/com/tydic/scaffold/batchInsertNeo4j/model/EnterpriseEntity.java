package com.tydic.scaffold.batchInsertNeo4j.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: EnterpriseEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-18
 * @Version:v1.0
 */
@Data
public class EnterpriseEntity implements Serializable {
    private String latnId;
    private String clientId ;
    private String clientName  ;
    private String orgUscc  ;
    private String regno  ;
    private String licid  ;
    private String statusCd ;
    private String foundDate;
    private String legalRepr;
    private String registerAddr;
    private String orgPhone ;
    private String bdLocation  ;
    private String cityCode ;
    private String cityName ;
    private String countyCode  ;
    private String countyName  ;
    private String officeAddr  ;
    private String keyMan  ;
    private String serviceNbr  ;
}
