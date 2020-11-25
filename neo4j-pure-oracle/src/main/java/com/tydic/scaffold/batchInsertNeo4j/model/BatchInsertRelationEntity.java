package com.tydic.scaffold.batchInsertNeo4j.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: BatchInsertRelationEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-26
 * @Version:v1.0
 */
@ApiModel("queryEntity:批量导入关系入参")
@Data
public class BatchInsertRelationEntity implements Serializable {
    @ApiModelProperty(value = "线程数量",required = false,example = "6")
    private int threadNum=0;

    @ApiModelProperty(value = "本地网",required = false,example = "551")
    private int latnId=0;


    @ApiModelProperty(value = "关系名称(EnterpriseRel/FamilyRel/UserNaturalRel/aqiyi_natural/email_natural/jd_natural/person_rel/qq_natural/taobao_natural/wechat_natural/weibo_natural/youku_natural)",required = true,example = "qq_natural")
    private String relationName;
}
