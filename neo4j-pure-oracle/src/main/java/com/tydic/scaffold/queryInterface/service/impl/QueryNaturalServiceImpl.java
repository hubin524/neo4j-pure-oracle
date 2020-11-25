package com.tydic.scaffold.queryInterface.service.impl;

import com.tydic.core.rational.Return;
import com.tydic.neo4j.Neo4jSessionManager;
import com.tydic.scaffold.queryInterface.QueryCommon;
import com.tydic.scaffold.queryInterface.model.UserNaturalInfo;
import com.tydic.scaffold.queryInterface.service.QueryNaturalService;
import org.neo4j.driver.v1.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: queryNaturalServiceImpl
 * @Description
 * @Author: hubin
 * @Date: 2020-08-19
 * @Version:v1.0
 */
@Service
public class QueryNaturalServiceImpl implements QueryNaturalService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Neo4jSessionManager neo;

    @Override
    public Return queryNaturalService(Map<String, Object> param) {
        Session session= neo.getSession();
        QueryCommon queryCommon=new QueryCommon(param,session);
        UserNaturalInfo userNaturalInfo=queryCommon.queryNaturalInfo();

        if(userNaturalInfo != null){
            neo.closeSession(session);
            return Return.success(userNaturalInfo);
        }else{
            neo.closeSession(session);
            return Return.failure(param.get("message")+"");
        }

    }
}
