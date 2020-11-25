package com.tydic.scaffold.batchInsertNeo4j.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: EnterpriseNaturalRelEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-19
 * @Version:v1.0
 */
@Data
public class EnterpriseNaturalRelEntity implements Serializable {
    private String clientLatnId;
    private String clientId;
    private String clientName;
    private String encryptOneidPrdInstId;
    private String oneidPrdInstId;
    private String latnId;
}
