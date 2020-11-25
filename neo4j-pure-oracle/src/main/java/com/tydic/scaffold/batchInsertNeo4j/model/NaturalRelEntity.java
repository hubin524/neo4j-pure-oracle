package com.tydic.scaffold.batchInsertNeo4j.model;

import java.io.Serializable;

/**
 * @ClassName: NaturalRelEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-14
 * @Version:v1.0
 */

public class NaturalRelEntity implements Serializable {
    private String aOneidPrdInstId;
    private String zOneidPrdInstId;
    private String personRel;
    private String oneidLabels="NaturalPerson";

    public String getOneidLabels() {
        return oneidLabels;
    }

    public void setOneidLabels(String oneidLabels) {
        this.oneidLabels = oneidLabels;
    }

    public String getaOneidPrdInstId() {
        return aOneidPrdInstId;
    }

    public void setaOneidPrdInstId(String aOneidPrdInstId) {
        this.aOneidPrdInstId = aOneidPrdInstId;
    }

    public String getzOneidPrdInstId() {
        return zOneidPrdInstId;
    }

    public void setzOneidPrdInstId(String zOneidPrdInstId) {
        this.zOneidPrdInstId = zOneidPrdInstId;
    }

    public String getPersonRel() {
        return personRel;
    }

    public void setPersonRel(String personRel) {
        this.personRel = personRel;
    }
}
