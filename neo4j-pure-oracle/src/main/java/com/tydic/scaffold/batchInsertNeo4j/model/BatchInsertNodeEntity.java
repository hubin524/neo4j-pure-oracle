package com.tydic.scaffold.batchInsertNeo4j.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: BatchInsertNodeEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-26
 * @Version:v1.0
 */
@ApiModel("BatchInsertNodeEntity:批量导入节点入参")
@Data
public class BatchInsertNodeEntity implements Serializable {
    @ApiModelProperty(value = "线程数量",required = false,example = "6")
    private int threadNum=0;

    @ApiModelProperty(value = "本地网",required = false,example = "551")
    private int latnId=0;

    @ApiModelProperty(value = "节点名称(NaturalPerson/User/Family/Enterprise/QQ/Aqiyi/Email/Jd/Taobao/Wechat/Weibo/Youku)",required = true,example = "QQ")
    private String nodeName;
}
