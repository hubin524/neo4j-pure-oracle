package com.tydic.scaffold.queryInterface.service.impl;

import com.tydic.core.rational.Return;
import com.tydic.framework.util.ObjectUtil;
import com.tydic.neo4j.Neo4jSessionManager;
import com.tydic.scaffold.queryInterface.QueryCommon;
import com.tydic.scaffold.queryInterface.model.FamilyNaturalInfo;
import com.tydic.scaffold.queryInterface.model.UserNaturalInfo;
import com.tydic.scaffold.queryInterface.service.QueryFamilyService;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: queryFamilyServiceImpl
 * @Description
 * @Author: hubin
 * @Date: 2020-08-19
 * @Version:v1.0
 */
@Service
public class QueryFamilyServiceImpl implements QueryFamilyService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    Neo4jSessionManager neo;

    @Override
    public Return queryFamilyService(Map<String, Object> param) {
        Session session= neo.getSession();
        QueryCommon queryCommon=new QueryCommon(param,session);
        UserNaturalInfo userNaturalInfo=queryCommon.queryNaturalInfo();

        FamilyNaturalInfo familyNaturalInfo=new FamilyNaturalInfo();
        if(userNaturalInfo != null){

            familyNaturalInfo.setUserNaturalInfo(userNaturalInfo);
            queryFamilyInfo(familyNaturalInfo,userNaturalInfo,session);
            neo.closeSession(session);
            return Return.success(familyNaturalInfo);
        }else{
            neo.closeSession(session);
            return Return.failure(param.get("message")+"");
        }
    }

    private void queryFamilyInfo(FamilyNaturalInfo familyNaturalInfo,UserNaturalInfo userNaturalInfo,Session session) {
        String NaturalId=userNaturalInfo.getInternetAccountEntity().getOneidPrdInstId();
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("oneidPrdInstId",NaturalId);

        StringJoiner relationJoiner = new StringJoiner(" ");
        relationJoiner.add("MATCH (user:LittleFamily)-[t]-(person:NaturalPerson)");
        relationJoiner.add("WHERE person.oneidPrdInstId = {oneidPrdInstId} ");
        relationJoiner.add("RETURN  user.familyId as familyId,user.householderLatnId as householderLatnId,user.householderPrdInstId as householderPrdInstId,user.householderServiceNbr as householderServiceNbr,user.householderCustId as householderCustId,user.householderAcctId as householderAcctId,user.householderBasicProdOfferInstId as ProdOfferInstId,user.keyMan as keyMan");
        Statement statement = new Statement(relationJoiner.toString(), param);
        StatementResult result = session.run(statement, TransactionConfig.empty());

        List<Record> userList = result.list();
        if (!ObjectUtil.isEmpty(userList) && userList.size() > 0) {
            logger.info("result.list().size=" + userList.size());
            List<Map<String, Object>> littleFamilyList = new ArrayList<>();
            for (int i = 0; i < userList.size(); i++) {
                Record record = userList.get(i);
                littleFamilyList.add(record.asMap());
                logger.info("person.oneidPrdInstId =" + record.asMap());
            }

            familyNaturalInfo.setLittleFamily(littleFamilyList);
        } else {
            logger.warn("not found Family info by oneidPrdInstId" + param.get("oneidPrdInstId"));
        }
    }


}
