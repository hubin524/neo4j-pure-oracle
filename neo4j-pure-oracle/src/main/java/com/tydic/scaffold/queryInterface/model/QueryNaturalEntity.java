package com.tydic.scaffold.queryInterface.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: queryNaturalEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-21
 * @Version:v1.0
 */
@ApiModel("queryEntity:自然人查询入参")
@Data
public class QueryNaturalEntity implements Serializable {
    @ApiModelProperty(value = "号码类型 1手机号码/2QQ/3微信/4淘宝/5京东/6微博/7优酷/8爱奇艺/9email",required = true,example = "1")
    private String numberType;

    @ApiModelProperty(value = "号码",required = true,example = "19965421573")
    private String serviceNbr;


    @ApiModelProperty(value = "本地网",required = false,example = "551")
    private String latnId;
}
