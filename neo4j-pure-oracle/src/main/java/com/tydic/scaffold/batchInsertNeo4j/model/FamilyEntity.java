package com.tydic.scaffold.batchInsertNeo4j.model;

import java.io.Serializable;

/**
 * @ClassName: FamilyEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-18
 * @Version:v1.0
 */

public class FamilyEntity  implements Serializable {
    private String encryptVirtualFamilyId;
    private String virtualFamilyId;
    private String virtualFamilyType;
    private String keyMan;

    public String getKeyMan() {
        return keyMan;
    }

    public void setKeyMan(String keyMan) {
        this.keyMan = keyMan;
    }

    public String getEncryptVirtualFamilyId() {
        return encryptVirtualFamilyId;
    }

    public void setEncryptVirtualFamilyId(String encryptVirtualFamilyId) {
        this.encryptVirtualFamilyId = encryptVirtualFamilyId;
    }

    public String getVirtualFamilyId() {
        return virtualFamilyId;
    }

    public void setVirtualFamilyId(String virtualFamilyId) {
        this.virtualFamilyId = virtualFamilyId;
    }

    public String getVirtualFamilyType() {
        return virtualFamilyType;
    }

    public void setVirtualFamilyType(String virtualFamilyType) {
        this.virtualFamilyType = virtualFamilyType;
    }
}
