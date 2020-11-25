package com.tydic.scaffold.batchInsertNeo4j.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: LittleFamilyEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-19
 * @Version:v1.0
 */
@Data
public class LittleFamilyEntity implements Serializable {
    private String familyId;
    private String householderLatnId;
    private String householderPrdInstId;
    private String householderServiceNbr;
    private String householderCustId;
    private String householderAcctId;
    private String prodOfferInstId;
    private String keyMan;

}
