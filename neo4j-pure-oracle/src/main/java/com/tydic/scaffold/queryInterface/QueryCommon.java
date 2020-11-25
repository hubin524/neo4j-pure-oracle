package com.tydic.scaffold.queryInterface;

import com.tydic.core.rational.Return;
import com.tydic.framework.util.BusinessConstants;
import com.tydic.framework.util.ObjectUtil;
import com.tydic.scaffold.batchInsertNeo4j.model.InternetAccountEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.UserEntity;
import com.tydic.scaffold.queryInterface.model.UserNaturalInfo;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @ClassName: QueryCommon
 * @Description
 * @Author: hubin
 * @Date: 2020-08-20
 * @Version:v1.0
 */

public class QueryCommon {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, Object> param = new HashMap<String, Object>();
    private Session session;

    public QueryCommon(Map<String, Object> param, Session session) {
        this.param = param;
        this.session = session;
    }

    public UserNaturalInfo queryNaturalInfo() {

        UserNaturalInfo userNaturalInfo = new UserNaturalInfo();

        String nbrType = param.get("numberType") + "";
        StringJoiner relationJoiner = new StringJoiner(" ");
        if (BusinessConstants.NumberConstants.SERVICE_NBR_TYPE.equals(nbrType)) {

            relationJoiner.add("MATCH (user:User)-[t]-(person:NaturalPerson)");
            relationJoiner.add("WHERE user.serviceNbr = {serviceNbr} ");
            relationJoiner.add("RETURN person.oneidPrdInstId as oneidPrdInstId, person.oneidServiceNbr as oneidServiceNbr,user.prdInstId as prdInstId,user.prdInstName as prdInstName,user.serviceNbr as serviceNbr,user.userAccount as userAccount,user.prdId as prdId,user.prdInstStasId as prdInstStasId,user.installDate as installDate,user.completeDate as completeDate,user.acctId as acctId,user.custId as custId,user.netNum as netNum,user.cImsi as cImsi,user.lImsi as lImsi,user.gImsi as gImsi,user.simCard as simCard, user.meid as meid,user.imei as imei");

        } else if (BusinessConstants.NumberConstants.AQIYI_TYPE.equals(nbrType)) {

            relationJoiner.add("MATCH (cust:Aqiyi)-[r]-(person:NaturalPerson)");
            relationJoiner.add("WHERE cust.aqiyi = {serviceNbr}");
            relationJoiner.add("RETURN person.oneidPrdInstId as oneidPrdInstId, person.oneidServiceNbr as oneidServiceNbr");

        } else if (BusinessConstants.NumberConstants.EMAIL_TYPE.equals(nbrType)) {

            relationJoiner.add("MATCH (cust:Email)-[r]-(person:NaturalPerson)");
            relationJoiner.add("WHERE cust.emailId = {serviceNbr}");
            relationJoiner.add("RETURN person.oneidPrdInstId as oneidPrdInstId, person.oneidServiceNbr as oneidServiceNbr");

        } else if (BusinessConstants.NumberConstants.JD_TYPE.equals(nbrType)) {

            relationJoiner.add("MATCH (cust:Jd)-[r]-(person:NaturalPerson)");
            relationJoiner.add("WHERE cust.jdId = {serviceNbr}");
            relationJoiner.add("RETURN person.oneidPrdInstId as oneidPrdInstId, person.oneidServiceNbr as oneidServiceNbr");

        } else if (BusinessConstants.NumberConstants.QQ_TYPE.equals(nbrType)) {

            relationJoiner.add("MATCH (cust:QQ)-[r]-(person:NaturalPerson)");
            relationJoiner.add("WHERE cust.qqId = {serviceNbr}");
            relationJoiner.add("RETURN person.oneidPrdInstId as oneidPrdInstId, person.oneidServiceNbr as oneidServiceNbr");

        } else if (BusinessConstants.NumberConstants.TAOBAO_TYPE.equals(nbrType)) {

            relationJoiner.add("MATCH (cust:Taobao)-[r]-(person:NaturalPerson)");
            relationJoiner.add("WHERE cust.taobaoId = {serviceNbr}");
            relationJoiner.add("RETURN person.oneidPrdInstId as oneidPrdInstId, person.oneidServiceNbr as oneidServiceNbr");

        } else if (BusinessConstants.NumberConstants.WECHAT_TYPE.equals(nbrType)) {

            relationJoiner.add("MATCH (cust:Wechat)-[r]-(person:NaturalPerson)");
            relationJoiner.add("WHERE cust.wechatId = {serviceNbr}");
            relationJoiner.add("RETURN person.oneidPrdInstId as oneidPrdInstId, person.oneidServiceNbr as oneidServiceNbr");

        } else if (BusinessConstants.NumberConstants.WEIBO_TYPE.equals(nbrType)) {

            relationJoiner.add("MATCH (cust:Weibo)-[r]-(person:NaturalPerson)");
            relationJoiner.add("WHERE cust.weiboId = {serviceNbr}");
            relationJoiner.add("RETURN person.oneidPrdInstId as oneidPrdInstId, person.oneidServiceNbr as oneidServiceNbr");

        } else if (BusinessConstants.NumberConstants.YOUKU_TYPE.equals(nbrType)) {

            relationJoiner.add("MATCH (cust:Youku)-[r]-(person:NaturalPerson)");
            relationJoiner.add("WHERE cust.youkuId = {serviceNbr}");
            relationJoiner.add("RETURN person.oneidPrdInstId as oneidPrdInstId, person.oneidServiceNbr as oneidServiceNbr");

        } else {
            logger.error("不支持的查询类型[" + nbrType + "]");
            param.put("message", "不支持的查询类型[" + nbrType + "]");
            return null;
        }


        Map<String, Object> naturalMap = new HashMap<String, Object>();
        List<Record> naturalList = queryCommList(relationJoiner);

        if (!ObjectUtil.isEmpty(naturalList) && naturalList.size() > 0) {
            naturalMap = naturalList.get(0).asMap();
            logger.info("查询到自然人ID信息 , oneidPrdInstId=" + naturalMap.get("oneidPrdInstId"));

            if (BusinessConstants.NumberConstants.SERVICE_NBR_TYPE.equals(nbrType)) {//号码可直接查出用户信息
                List<Map<String, Object>> userList = new ArrayList<>();
                for (int i = 0; i < naturalList.size(); i++) {
                    Record record = naturalList.get(i);
                    userList.add(record.asMap());
                    logger.debug("one use info =" + record.asMap());
                }
                logger.info("total use count =" + userList.size());
                userNaturalInfo.setUserEntity(userList);
            }

        } else {
            param.put("message", "未查询到自然人ID信息");
            logger.error("未查询到自然人ID信息");
            return null;
        }


        String naturalId = naturalMap.get("oneidPrdInstId") + "";
        String serviceNbr = naturalMap.get("oneidServiceNbr") + "";
        param.put("oneidPrdInstId", naturalId);
        InternetAccountEntity internetAccountEntity = new InternetAccountEntity();
        userNaturalInfo.setInternetAccountEntity(internetAccountEntity);

        userNaturalInfo.getInternetAccountEntity().setOneidPrdInstId(naturalId);
        userNaturalInfo.getInternetAccountEntity().setServiceNbr(serviceNbr);
        //查询自然人下用户信息以及互联网信息
        if (!param.containsKey("query_use")) {
            queryUserAndInternetInfo(userNaturalInfo, nbrType, naturalId,internetAccountEntity);
        }
        return userNaturalInfo;
    }

    private List<Record> queryCommList(StringJoiner relationJoiner) {

        Statement statement = new Statement(relationJoiner.toString(), param);
        StatementResult result = session.run(statement, TransactionConfig.empty());
        List<Record> naturalList = result.list();
        if (!ObjectUtil.isEmpty(naturalList) && naturalList.size() > 0) {
            logger.info("find data size=" + naturalList.size() + ",by nosql=" + relationJoiner + " and param=" + param);

        } else {
            logger.error("not find data size=" + naturalList.size() + ",by nosql=" + relationJoiner + " and param=" + param);

        }

        return naturalList;
    }

    private Map<String, Object> queryCommMap(StringJoiner relationJoiner) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Statement statement = new Statement(relationJoiner.toString(), param);
        StatementResult result = session.run(statement, TransactionConfig.empty());
        List<Record> naturalList = result.list();
        if (!ObjectUtil.isEmpty(naturalList) && naturalList.size() > 0) {
            logger.info("find data size=" + naturalList.size() + ",by nosql=" + relationJoiner + " and param=" + param);

            for (int i = 0; i < naturalList.size(); i++) {
                Record record = naturalList.get(i);
                Map<String, Object> tempMap = record.asMap();
                logger.info("one data  =" + tempMap);
                for (String key : tempMap.keySet()) {
                    if (resultMap.containsKey(key)) {
                        resultMap.put(key, resultMap.get(key) + "," + tempMap.get(key));
                    } else {
                        resultMap.put(key, tempMap.get(key));
                    }
                }

                logger.info("person.oneidPrdInstId =" + record.asMap());
            }

        } else {
            logger.error("not find data size=" + naturalList.size() + ",by nosql=" + relationJoiner + " and param=" + param);

        }
        logger.info("resultMap data  =" + resultMap);
        return resultMap;
    }


    private UserNaturalInfo queryUserAndInternetInfo(UserNaturalInfo userNaturalInfo, String nbrType, String naturalId, InternetAccountEntity internetAccountEntity) {
        StringJoiner relationJoiner = new StringJoiner(" ");
        if (!BusinessConstants.NumberConstants.SERVICE_NBR_TYPE.equals(nbrType)) {//手机号码类型  已经查过用户信息

            relationJoiner.add("MATCH (user:User)-[t]-(person:NaturalPerson)");
            relationJoiner.add("WHERE person.oneidPrdInstId = {oneidPrdInstId} ");
            relationJoiner.add("RETURN person.oneidPrdInstId as oneidPrdInstId,user.prdInstId as prdInstId,user.prdInstName as prdInstName,user.serviceNbr as serviceNbr,user.userAccount as userAccount,user.prdId as prdId,user.prdInstStasId as prdInstStasId,user.installDate as installDate,user.completeDate as completeDate,user.acctId as acctId,user.custId as custId,user.netNum as netNum,user.cImsi as cImsi,user.lImsi as lImsi,user.gImsi as gImsi,user.simCard as simCard, user.meid as meid,user.imei as imei");
            Statement statement = new Statement(relationJoiner.toString(), param);


            StatementResult result = session.run(statement, TransactionConfig.empty());
            List<Record> userList = result.list();
            if (!ObjectUtil.isEmpty(userList) && userList.size() > 0) {
                logger.info("result.list().size=" + userList.size());
                List<Map<String, Object>> rr = new ArrayList<>();
                for (int i = 0; i < userList.size(); i++) {
                    Record record = userList.get(i);
                    rr.add(record.asMap());
                    logger.info("person.oneidPrdInstId =" + record.asMap());
                }

                userNaturalInfo.setUserEntity(rr);
            } else {
                logger.warn("not found user info by oneidPrdInstId" + param.get("oneidPrdInstId"));
            }
        }




        relationJoiner = new StringJoiner(" ");
        relationJoiner.add("MATCH (user:QQ)-[r]-(person:NaturalPerson)");
        relationJoiner.add("WHERE person.oneidPrdInstId = {oneidPrdInstId} ");
        relationJoiner.add("RETURN user.qqId as qqId");
        Map<String, Object> resultMap = queryCommMap(relationJoiner);
        internetAccountEntity.setQqId(resultMap.get("qqId") + "");

        relationJoiner = new StringJoiner(" ");
        relationJoiner.add("MATCH (user:Taobao)-[r]-(person:NaturalPerson)");
        relationJoiner.add("WHERE person.oneidPrdInstId = {oneidPrdInstId} ");
        relationJoiner.add("RETURN user.taobaoId as taobaoId");
        resultMap = queryCommMap(relationJoiner);
        internetAccountEntity.setTaobaoId(resultMap.get("taobaoId") + "");

        relationJoiner = new StringJoiner(" ");
        relationJoiner.add("MATCH (user:Aqiyi)-[r]-(person:NaturalPerson)");
        relationJoiner.add("WHERE person.oneidPrdInstId = {oneidPrdInstId} ");
        relationJoiner.add("RETURN user.aqiyi as aqiyi");
        resultMap = queryCommMap(relationJoiner);
        internetAccountEntity.setAqiyiId(resultMap.get("aqiyi") + "");

        relationJoiner = new StringJoiner(" ");
        relationJoiner.add("MATCH (user:Email)-[r]-(person:NaturalPerson)");
        relationJoiner.add("WHERE person.oneidPrdInstId = {oneidPrdInstId} ");
        relationJoiner.add("RETURN user.emailId as emailId");
        resultMap = queryCommMap(relationJoiner);
        internetAccountEntity.setEmailId(resultMap.get("emailId") + "");

        relationJoiner = new StringJoiner(" ");
        relationJoiner.add("MATCH (user:Jd)-[r]-(person:NaturalPerson)");
        relationJoiner.add("WHERE person.oneidPrdInstId = {oneidPrdInstId} ");
        relationJoiner.add("RETURN user.jdId as jdId");
        resultMap = queryCommMap(relationJoiner);
        internetAccountEntity.setJdId(resultMap.get("jdId") + "");

        relationJoiner = new StringJoiner(" ");
        relationJoiner.add("MATCH (user:Wechat)-[r]-(person:NaturalPerson)");
        relationJoiner.add("WHERE person.oneidPrdInstId = {oneidPrdInstId} ");
        relationJoiner.add("RETURN user.wechatId as wechatId");
        resultMap = queryCommMap(relationJoiner);
        internetAccountEntity.setWechatId(resultMap.get("wechatId") + "");

        relationJoiner = new StringJoiner(" ");
        relationJoiner.add("MATCH (user:Weibo)-[r]-(person:NaturalPerson)");
        relationJoiner.add("WHERE person.oneidPrdInstId = {oneidPrdInstId} ");
        relationJoiner.add("RETURN user.weiboId as weiboId");
        resultMap = queryCommMap(relationJoiner);
        internetAccountEntity.setWeiboId(resultMap.get("weiboId") + "");

        relationJoiner = new StringJoiner(" ");
        relationJoiner.add("MATCH (user:Youku)-[r]-(person:NaturalPerson)");
        relationJoiner.add("WHERE person.oneidPrdInstId = {oneidPrdInstId} ");
        relationJoiner.add("RETURN user.youkuId as youkuId");
        resultMap = queryCommMap(relationJoiner);
        internetAccountEntity.setYoukuId(resultMap.get("youkuId") + "");

        internetAccountEntity.setOneidPrdInstId(naturalId);
        userNaturalInfo.setInternetAccountEntity(internetAccountEntity);


        return userNaturalInfo;
    }


}
