package com.tydic.scaffold.batchInsertNeo4j.model;

import java.io.Serializable;

/**
 * @ClassName: UserEntity
 * @Description
 * @Author: hubin
 * @Date: 2020-08-14
 * @Version:v1.0
 */

public class UserEntity implements Serializable {
    private String oneidLabels="User";
    private String serviceNbr;
    private String prdInstId;
    private String prdInstName ;
    private String userAccount ;
    private String prdId  ;
    private String prdInstStasId ;
    private String installDate ;
    private String completeDate  ;
    private String acctId  ;
    private String custId ;
    private String netNum   ;
    private String cImsi   ;
    private String lImsi   ;
    private String gImsi   ;
    private String simCard  ;
    private String meid   ;
    private String imei    ;

    public String getOneidLabels() {
        return oneidLabels;
    }

    public void setOneidLabels(String oneidLabels) {
        this.oneidLabels = oneidLabels;
    }

    public String getServiceNbr() {
        return serviceNbr;
    }

    public void setServiceNbr(String serviceNbr) {
        this.serviceNbr = serviceNbr;
    }

    public String getPrdInstId() {
        return prdInstId;
    }

    public void setPrdInstId(String prdInstId) {
        this.prdInstId = prdInstId;
    }

    public String getPrdInstName() {
        return prdInstName;
    }

    public void setPrdInstName(String prdInstName) {
        this.prdInstName = prdInstName;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getPrdId() {
        return prdId;
    }

    public void setPrdId(String prdId) {
        this.prdId = prdId;
    }

    public String getPrdInstStasId() {
        return prdInstStasId;
    }

    public void setPrdInstStasId(String prdInstStasId) {
        this.prdInstStasId = prdInstStasId;
    }

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }

    public String getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

    public String getAcctId() {
        return acctId;
    }

    public void setAcctId(String acctId) {
        this.acctId = acctId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getNetNum() {
        return netNum;
    }

    public void setNetNum(String netNum) {
        this.netNum = netNum;
    }

    public String getcImsi() {
        return cImsi;
    }

    public void setcImsi(String cImsi) {
        this.cImsi = cImsi;
    }

    public String getlImsi() {
        return lImsi;
    }

    public void setlImsi(String lImsi) {
        this.lImsi = lImsi;
    }

    public String getgImsi() {
        return gImsi;
    }

    public void setgImsi(String gImsi) {
        this.gImsi = gImsi;
    }

    public String getSimCard() {
        return simCard;
    }

    public void setSimCard(String simCard) {
        this.simCard = simCard;
    }

    public String getMeid() {
        return meid;
    }

    public void setMeid(String meid) {
        this.meid = meid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
