package com.tydic.scaffold.queryInterface.service.impl;

import com.tydic.core.rational.Return;
import com.tydic.neo4j.Neo4jSessionManager;
import com.tydic.scaffold.queryInterface.QueryCommon;
import com.tydic.scaffold.queryInterface.model.QueryTwoNaturalEntity;
import com.tydic.scaffold.queryInterface.model.UserNaturalInfo;
import com.tydic.scaffold.queryInterface.service.QueryNaturalRelService;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: queryNaturalRelServiceImpl
 * @Description
 * @Author: hubin
 * @Date: 2020-08-19
 * @Version:v1.0
 */
@Service
public class QueryNaturalRelServiceImpl implements QueryNaturalRelService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Neo4jSessionManager neo;

    @Override
    public Return queryNaturalRelService(QueryTwoNaturalEntity queryTwoNaturalEntity) {
        Session session = neo.getSession();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("numberType", queryTwoNaturalEntity.getNumberType1());
        param.put("serviceNbr", queryTwoNaturalEntity.getServiceNbr1());
        param.put("query_use", "0");

        param.put("latnId", queryTwoNaturalEntity.getLatnId());
        QueryCommon queryCommon = new QueryCommon(param, session);
        UserNaturalInfo userNaturalInfo1 = queryCommon.queryNaturalInfo();
        if (userNaturalInfo1 == null) {
            neo.closeSession(session);
            Return.failure(param + ",未查询到自然人信息");
        }
        param.put("oneidPrdInstId1", userNaturalInfo1.getInternetAccountEntity().getOneidPrdInstId());

        param.put("numberType", queryTwoNaturalEntity.getNumberType2());
        param.put("serviceNbr", queryTwoNaturalEntity.getServiceNbr2());
        param.put("latnId", queryTwoNaturalEntity.getLatnId());
        QueryCommon queryCommon2 = new QueryCommon(param, session);
        UserNaturalInfo userNaturalInfo2 = queryCommon2.queryNaturalInfo();
        if (userNaturalInfo2 == null) {
            neo.closeSession(session);
            Return.failure(param + ",未查询到自然人信息");
        }

        param.put("oneidPrdInstId2", userNaturalInfo2.getInternetAccountEntity().getOneidPrdInstId());

        List<String> resultPath = new ArrayList<String>();
        resultPath = queryShort(param, session);
        neo.closeSession(session);
        return Return.success(resultPath);
    }

    private List<String> queryShort(Map<String, Object> param, Session session) {
        List<String> resultPath = new ArrayList<String>();
        StringJoiner relationJoiner = new StringJoiner(" ");
        relationJoiner.add("MATCH (cust:NaturalPerson{oneidPrdInstId:{oneidPrdInstId1}}),(cc:NaturalPerson{oneidPrdInstId:{oneidPrdInstId2}}),");
        relationJoiner.add("p=shortestpath((cust)-[*]-(cc)) ");
        relationJoiner.add(" RETURN p");
        logger.info("begin query shortest path neo4j by sql=" + relationJoiner);
        Statement statement = new Statement(relationJoiner.toString(), param);
        StatementResult result = session.run(statement, TransactionConfig.empty());

        while (result.hasNext()) {
            Record record = result.next();
            List<Value> values = record.values();
            Map<Long, Node> nodesMap = new HashMap<>();
            for (org.neo4j.driver.v1.Value value : values) {
                if (value.type().name().equals("PATH")) {
                    Path p = (Path) value.asPath();
                    logger.info(" 最短路径长度为：" + p.length());
                    logger.info("====================================");
                    Iterable<Node> nodes = p.nodes();
                    for (Node node : nodes) {
                        nodesMap.put(node.id(), node);
                    }

                    /**
                     * 打印最短路径里面的关系 == 关系包括起始节点的ID和末尾节点的ID，以及关系的type类型
                     */
                    Iterable<Relationship> relationships = p.relationships();
                    String path = "";
                    for (Relationship relationship : relationships) {
                        Long startID = relationship.startNodeId();
                        Long endID = relationship.endNodeId();
                        String rType = relationship.type();
                        Map<String, Object> relationMap = relationship.asMap();
                        /**
                         * asMap 相当于 节点的properties属性信息
                         */
                        String startPath="{oneidPrdInstName:" + nodesMap.get(startID).asMap().get("oneidPrdInstName") + ",oneidPrdInstId:" + nodesMap.get(startID).asMap().get("oneidPrdInstId")
                                +",oneidServiceNbr:"+ nodesMap.get(startID).asMap().get("oneidServiceNbr")+ "}";
                        String relationType="-{" + rType+":"+ relationMap.get("person_rel") + "}-";
                        String endPath="{oneidPrdInstName:" + nodesMap.get(endID).asMap().get("oneidPrdInstName") + ",oneidPrdInstId:" + nodesMap.get(endID).asMap().get("oneidPrdInstId")
                                +",oneidServiceNbr:"+ nodesMap.get(endID).asMap().get("oneidServiceNbr")+"}";

                        if ("".equals(path)) {
                            path = startPath+relationType+endPath;
                        } else {
                            if(path.indexOf(startPath) == -1 && path.indexOf(endPath) > -1){
                                path=startPath+relationType+path;
                            }else if(path.indexOf(startPath) > -1 && path.indexOf(endPath) == -1){
                                path=path+relationType+endPath;
                            }else if(path.indexOf(startPath) == -1 && path.indexOf(endPath) == -1){
                                path=path+startPath+relationType+endPath;
                            }
                        }
                        logger.info("路径关系 : " + path);

                    }
                    resultPath.add(path);
                }
            }

        }


        return resultPath;
    }

}
