package com.tydic.scaffold.queryInterface.service.impl;

import com.tydic.core.rational.Return;
import com.tydic.framework.util.ObjectUtil;
import com.tydic.neo4j.Neo4jSessionManager;
import com.tydic.scaffold.queryInterface.QueryCommon;
import com.tydic.scaffold.queryInterface.model.NaturalEnterpriseinfo;
import com.tydic.scaffold.queryInterface.model.UserNaturalInfo;
import com.tydic.scaffold.queryInterface.service.QueryNaturalEnterpriseService;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @ClassName: queryNaturalEnterpriseServiceImpl
 * @Description
 * @Author: hubin
 * @Date: 2020-08-19
 * @Version:v1.0
 */
@Service
public class QueryNaturalEnterpriseServiceImpl implements QueryNaturalEnterpriseService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Neo4jSessionManager neo;

    @Override
    public Return queryNaturalEnterpriseService(Map<String, Object> param) {
        Session session= neo.getSession();
        QueryCommon queryCommon=new QueryCommon(param,session);
        UserNaturalInfo userNaturalInfo=queryCommon.queryNaturalInfo();

        NaturalEnterpriseinfo naturalEnterpriseinfo=new NaturalEnterpriseinfo();
        if(userNaturalInfo != null){

            naturalEnterpriseinfo.setUserNaturalInfo(userNaturalInfo);
            param.put("oneidPrdInstId",userNaturalInfo.getInternetAccountEntity().getOneidPrdInstId());
            queryEnterpriseInfo(naturalEnterpriseinfo,session,param);
            neo.closeSession(session);
            return Return.success(naturalEnterpriseinfo);
        }else{
            neo.closeSession(session);
            return Return.failure(param.get("message")+"");
        }
    }

    private void queryEnterpriseInfo(NaturalEnterpriseinfo naturalEnterpriseinfo, Session session, Map<String, Object> param) {
        List<Map<String, Object>> resultList=new ArrayList<Map<String, Object>>();
        StringJoiner relationJoiner = new StringJoiner(" ");
        relationJoiner.add("MATCH (enterprise:Enterprise)-[t]-(person:NaturalPerson)");
        relationJoiner.add("WHERE person.oneidPrdInstId = {oneidPrdInstId} ");
        relationJoiner.add("RETURN  enterprise.clientId as clientId,enterprise.clientName as clientName,enterprise.orgUscc as orgUscc,enterprise.regno as regno,enterprise.statusCd as statusCd,enterprise.foundDate as foundDate,enterprise.legalRepr as legalRepr,enterprise.registerAddr as registerAddr,enterprise.orgPhone as orgPhone,enterprise.officeAddr as officeAddr,enterprise.keyMan as keyMan,enterprise.serviceNbr as serviceNbr");
        Statement statement = new Statement(relationJoiner.toString(), param);
        StatementResult result = session.run(statement, TransactionConfig.empty());
        List<Record> naturalList = result.list();
        if (!ObjectUtil.isEmpty(naturalList) && naturalList.size() > 0) {
            logger.info("find EnterpriseInfo size=" + naturalList.size() + ",by nosql=" + relationJoiner + " and param=" + param);

            for (int i = 0; i < naturalList.size(); i++) {
                Record record = naturalList.get(i);
                Map<String, Object> tempMap=record.asMap();
                logger.info("one data  =" + tempMap);
                resultList.add(tempMap);
            }

        } else {
            logger.error("not find EnterpriseInfo size=" + naturalList.size() + ",by nosql=" + relationJoiner + " and param=" + param);
         }

        naturalEnterpriseinfo.setEnterpriseinfo(resultList);
    }
}
