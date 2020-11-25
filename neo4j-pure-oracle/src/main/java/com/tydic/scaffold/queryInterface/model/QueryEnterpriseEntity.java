package com.tydic.scaffold.queryInterface.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: QueryEnterpriseEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-21
 * @Version:v1.0
 */
@ApiModel("queryEntity:two自然人关系查询入参")
@Data
public class QueryEnterpriseEntity implements Serializable {

    @ApiModelProperty(value = "企业名称（至少4字）",required = true,example = "天源迪科")
   private String clientName;
}
