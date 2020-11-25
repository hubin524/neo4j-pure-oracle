package com.tydic.scaffold.queryInterface.service.impl;

import com.tydic.core.rational.Return;
import com.tydic.framework.util.ObjectUtil;
import com.tydic.neo4j.Neo4jSessionManager;
import com.tydic.scaffold.queryInterface.service.QueryEnterpriseService;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: queryEnterpriseServiceImpl
 * @Description
 * @Author: hubin
 * @Date: 2020-08-19
 * @Version:v1.0
 */
@Service
public class QueryEnterpriseServiceImpl implements QueryEnterpriseService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    Neo4jSessionManager neo;

    @Override
    public Return queryEnterpriseService(Map<String, Object> param) {

        String clientName=param.get("clientName")+"";
        if(clientName==null||clientName.equals("")||clientName.equalsIgnoreCase("null") || (clientName.replace(" ","")).length() < 4){
            Return.failure("企业名称至少输入4个字");
        }

        Session session= neo.getSession();
        List<Map<String, Object>> resultList=new ArrayList<Map<String, Object>>();
        StringJoiner relationJoiner = new StringJoiner(" ");
        relationJoiner.add("MATCH (person:Enterprise)-[t]-(naturalPerson:NaturalPerson)");

        relationJoiner.add("WHERE person.clientName =~'.*[clientName].*' ");
        relationJoiner.add("RETURN distinct person.clientId as clientId,person.clientName as clientName,person.orgUscc as orgUscc,person.regno as regno,person.statusCd as statusCd,person.foundDate as foundDate,person.legalRepr as legalRepr,person.registerAddr as registerAddr,person.orgPhone as orgPhone,person.officeAddr as officeAddr,person.keyMan as keyMan");

        String sql = relationJoiner.toString();
        for(Map.Entry<String, Object> entry : param.entrySet()){
            String mapKey = entry.getKey();
            String mapValue = entry.getValue()+"";
            sql=sql.replace("["+mapKey+"]",mapValue);
        }
        Statement statement = new Statement(sql, param);
        StatementResult result = session.run(statement, TransactionConfig.empty());
        List<Record> naturalList = result.list();
        if (!ObjectUtil.isEmpty(naturalList) && naturalList.size() > 0) {
            logger.info("find data size=" + naturalList.size() + ",by nosql=" + relationJoiner + " and param=" + param);

            for (int i = 0; i < naturalList.size(); i++) {
                Record record = naturalList.get(i);
                Map<String, Object> tempMap=record.asMap();
                logger.info("one data  =" + tempMap);
                resultList.add(tempMap);
            }

        } else {
            logger.error("not find data size=" + naturalList.size() + ",by nosql=" + relationJoiner + " and param=" + param);
            return Return.failure("未查询到["+clientName+"]相关企业信息");
        }

        neo.closeSession(session);
        return Return.success(resultList);
    }
}
