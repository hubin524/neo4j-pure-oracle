package com.tydic.scaffold.batchInsertNeo4j.model;

import java.io.Serializable;

/**
 * @ClassName: UserRelEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-14
 * @Version:v1.0
 */

public class UserRelEntity implements Serializable {
    private String oneidPrdInstId ;
    private String latnId;
    private String prdInstId ;
    private String oneidLabels="UserNatural";

    public String getOneidLabels() {
        return oneidLabels;
    }

    public void setOneidLabels(String oneidLabels) {
        this.oneidLabels = oneidLabels;
    }

    public String getOneidPrdInstId() {
        return oneidPrdInstId;
    }

    public void setOneidPrdInstId(String oneidPrdInstId) {
        this.oneidPrdInstId = oneidPrdInstId;
    }

    public String getLatnId() {
        return latnId;
    }

    public void setLatnId(String latnId) {
        this.latnId = latnId;
    }

    public String getPrdInstId() {
        return prdInstId;
    }

    public void setPrdInstId(String prdInstId) {
        this.prdInstId = prdInstId;
    }
}
