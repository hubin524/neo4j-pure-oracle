package com.tydic.scaffold.batchInsertNeo4j.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: NaturalPersonEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-13
 * @Version:v1.0
 */

public class NaturalPersonEntity implements Serializable {
    private String encryptOneidPrdInstId;
    private String oneidServiceNbr;
    private String oneidPrdInstId;
    private String oneidLabels="NaturalPerson";
    private String oneidPrdInstName;

    public String getEncryptOneidPrdInstId() {
        return encryptOneidPrdInstId;
    }

    public void setEncryptOneidPrdInstId(String encryptOneidPrdInstId) {
        this.encryptOneidPrdInstId = encryptOneidPrdInstId;
    }

    public String getOneidServiceNbr() {
        return oneidServiceNbr;
    }

    public void setOneidServiceNbr(String oneidServiceNbr) {
        this.oneidServiceNbr = oneidServiceNbr;
    }

    public String getOneidPrdInstId() {
        return oneidPrdInstId;
    }

    public void setOneidPrdInstId(String oneidPrdInstId) {
        this.oneidPrdInstId = oneidPrdInstId;
    }

    public String getOneidLabels() {
        return oneidLabels;
    }

    public void setOneidLabels(String oneidLabels) {
        this.oneidLabels = oneidLabels;
    }

    public String getOneidPrdInstName() {
        return oneidPrdInstName;
    }

    public void setOneidPrdInstName(String oneidPrdInstName) {
        this.oneidPrdInstName = oneidPrdInstName;
    }
}
