package com.tydic.scaffold.batchInsertNeo4j.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: LittleFamilyRelEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-19
 * @Version:v1.0
 */
@Data
public class LittleFamilyRelEntity implements Serializable {
    private String encryptVirtualFamilyId;
    private String virtualFamilyId;
    private String familyId;
    private String encryptOneidPrdInstId;
    private String oneidPrdInstId;
    private String relation;
}
