package com.tydic.backgroundInsert;

import com.tydic.neo4j.DataSourceFactoryUtil;
import com.tydic.neo4j.Neo4jConnectionUtil;
import com.tydic.neo4j.Neo4jDatasource;
import com.tydic.neo4j.Neo4jSessionManager;
import com.tydic.scaffold.batchInsertNeo4j.dao.QueryNaturalPersonsDao;
import com.tydic.scaffold.batchInsertNeo4j.model.BatchInsertRelationEntity;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: batchInsert
 * @Description
 * @Author: hubin
 * @Date: 2020-08-12
 * @Version:v1.0
 */
@Component
public class BatchInsertRelation {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${fixThrea.fixThreadNum:}")
    private int fixThreadNum;

    @Value("${neo4j.uri:}")
    private  String uri = "";

    @Value("${neo4j.userName:}")
    private  String userName = "";

    @Value("${neo4j.password:}")
    private  String password = "";

    private Driver driver;

    @Value("${neo4j.maxConnectionPoolSize:}")
    private  String maxConnectionPoolSize = "";

    @Value("${neo4j.connectionTimeoutMillis:}")
    private  String connectionTimeoutMillis = "";

    @Autowired
    Neo4jSessionManager neo;

    public void startThreadRelation(QueryNaturalPersonsDao queryNaturalPersonsDao, BatchInsertRelationEntity relationEntity) throws Exception {
        int tempThreadNum=relationEntity.getThreadNum();
        String relationName=relationEntity.getRelationName();
        logger.info("BatchInsertRelation startThreadRelation confThreadNum=" +fixThreadNum+",intoThreadNum="+tempThreadNum+",relationName="+relationName);

        if(tempThreadNum > 0){
            fixThreadNum=tempThreadNum;
        }
        logger.info("BatchInsertRelation  startThread =" +fixThreadNum);

        //构造一个线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                fixThreadNum+1, fixThreadNum+1, 10000, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(100),
                new ThreadPoolExecutor.DiscardOldestPolicy());

        Map<String, String> naturalNodeMap = new ConcurrentHashMap<String, String>();
        Session session= neo.getSession();

        if(!deleteInit(session,relationName)){
            return ;
        }

        for (int i = 0; i < fixThreadNum; i++) {

            if(session == null){
                Neo4jDatasource neo4jDatasourceInner = neo4jDatasource();
                if (neo4jDatasourceInner == null) {
                    throw new NullPointerException("neo4jDatasourceInner construct is null");
                }

                session=Neo4jConnectionUtil.session(neo4jDatasourceInner);

            }
            BatchInsertRelationThread backInvoiceBankWork = new BatchInsertRelationThread(i,session,queryNaturalPersonsDao,fixThreadNum,relationName);
            threadPool.execute(backInvoiceBankWork);
            logger.info("BatchInsertRelation  fixThreadNum =" + i);
        }

        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            try {
                Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        neo.closeSession(session);

    }

    private boolean deleteInit(Session session,String relationName) {

        //EnterpriseRel/FamilyRel/LittleFamilyRel/UserNaturalRel/aqiyi_natural/email_natural
        // /jd_natural/person_rel/qq_natural/taobao_natural/wechat_natural/weibo_natural/youku_natural
        if ("person_rel".equalsIgnoreCase(relationName)) {

            String deleteSql="MATCH (cust:NaturalPerson)-[t]-(cc:NaturalPerson) DELETE t";//删除自然人关系
            deleteNodeCommon(session,deleteSql,relationName);

        } else if ("UserNaturalRel".equalsIgnoreCase(relationName)) {
            String deleteSql="MATCH (cust:User)-[t]-(cc:NaturalPerson) DELETE t";//删除用户与自然人关系
            deleteNodeCommon(session,deleteSql,relationName);

        } else if ("FamilyRel".equalsIgnoreCase(relationName)) {

            String deleteSql="MATCH (cust:LittleFamily)-[t]-(cc:Family) DELETE t";//删除家庭与小家庭关系
            deleteNodeCommon(session,deleteSql,relationName);

            deleteSql="MATCH (cust:NaturalPerson)-[t]-(cc:LittleFamily) DELETE t";//删除自然人与小家庭关系
            deleteNodeCommon(session,deleteSql,relationName);

        } else if ("EnterpriseRel".equalsIgnoreCase(relationName)) {
            String deleteSql="MATCH (cust:NaturalPerson)-[t]-(cc:Enterprise) DELETE t";//删除自然人与企业关系
            deleteNodeCommon(session,deleteSql,relationName);

        } else if ("qq_natural".equalsIgnoreCase(relationName)) {
            String deleteSql="MATCH (cust:NaturalPerson)-[t]-(cc:QQ) DELETE t";//删除自然人与QQ关系
            deleteNodeCommon(session,deleteSql,relationName);

        } else if ("email_natural".equalsIgnoreCase(relationName)) {
            String deleteSql="MATCH (cust:NaturalPerson)-[t]-(cc:Email) DELETE t";//删除自然人与Email关系
            deleteNodeCommon(session,deleteSql,relationName);

        } else if ("wechat_natural".equalsIgnoreCase(relationName)) {
            String deleteSql="MATCH (cust:NaturalPerson)-[t]-(cc:Wechat) DELETE t";//删除自然人与Wechat关系
            deleteNodeCommon(session,deleteSql,relationName);

        } else if ("jd_natural".equalsIgnoreCase(relationName)) {
            String deleteSql="MATCH (cust:NaturalPerson)-[t]-(cc:Jd) DELETE t";//删除自然人与Jd关系
            deleteNodeCommon(session,deleteSql,relationName);

        } else if ("taobao_natural".equalsIgnoreCase(relationName)) {
            String deleteSql="MATCH (cust:NaturalPerson)-[t]-(cc:Taobao) DELETE t";//删除自然人与Taobao关系
            deleteNodeCommon(session,deleteSql,relationName);

        } else if ("weibo_natural".equalsIgnoreCase(relationName)) {
            String deleteSql="MATCH (cust:NaturalPerson)-[t]-(cc:Weibo) DELETE t";//删除自然人与QQ关系
            deleteNodeCommon(session,deleteSql,relationName);

        } else if ("youku_natural".equalsIgnoreCase(relationName)) {
            String deleteSql="MATCH (cust:NaturalPerson)-[t]-(cc:Youku) DELETE t";//删除自然人与Youku关系
            deleteNodeCommon(session,deleteSql,relationName);

        } else if ("aqiyi_natural".equalsIgnoreCase(relationName)) {
            String deleteSql="MATCH (cust:NaturalPerson)-[t]-(cc:Aqiyi) DELETE t";//删除自然人与Aqiyi关系
            deleteNodeCommon(session,deleteSql,relationName);

        } else {
            logger.error("不支持的节点名称 relationName=" + relationName);
            return false;
        }
        return true;
    }

    private void deleteNodeCommon(Session session,String deleteSql,String relationName) {
        Map<String, Object> parameter = new HashMap();
        logger.info("begin delete  relationName=" + relationName +",by sql="+deleteSql);
        Statement statement = new Statement(deleteSql, parameter);
        try {
            session.run(statement, TransactionConfig.empty());
        }catch(Exception e){
            int i=0;
            while(i < 3){
                try {
                    session.run(statement, TransactionConfig.empty());
                    i=3;
                }catch(Exception e1){
                    i++;
                    if(i == 3){
                        logger.error(e1+e1.getMessage());
                    }
                }
            }
        }
    }



    public static void main(String[] args) {
        Neo4jSessionManager neo=new Neo4jSessionManager();
        Session session= neo.getSession();

        // queryShort(session);
    }



    private static void queryShort(Session session) {
        List<String> resultPath = new ArrayList<String>();
        Map<String, Object> parameter = new HashMap();
        StringJoiner relationJoiner = new StringJoiner(" ");
        parameter.put("serviceNbr", "15375231938");
        relationJoiner.add("MATCH (cust:Student{no:1}),(cc:Student{no:8}),");
        relationJoiner.add("p=shortestpath((cust)-[*]-(cc)) ");
        relationJoiner.add(" RETURN p");
        System.out.println("begin query shortest path neo4j by sql=" + relationJoiner);
        Statement statement = new Statement(relationJoiner.toString(), parameter);
        StatementResult result = session.run(statement, TransactionConfig.empty());

        while (result.hasNext()) {
            Record record = result.next();
            List<org.neo4j.driver.v1.Value> values = record.values();
            Map<Long, Node> nodesMap = new HashMap<>();
            for (org.neo4j.driver.v1.Value value : values) {
                if (value.type().name().equals("PATH")) {
                    Path p = (Path) value.asPath();
                    System.out.println(" 最短路径长度为：" + p.length());
                    System.out.println("====================================");
                    Iterable<Node> nodes = p.nodes();
                    for (Node node : nodes) {
                        nodesMap.put(node.id(), node);
                    }

                    /**
                     * 打印最短路径里面的关系 == 关系包括起始节点的ID和末尾节点的ID，以及关系的type类型
                     */
                    Iterable<Relationship> relationships = p.relationships();
                    for (Relationship relationship : relationships) {
                        Long startID = relationship.startNodeId();
                        Long endID = relationship.endNodeId();
                        String rType = relationship.type();
                        Map<String,Object> aa= relationship.asMap();
                        /**
                         * asMap 相当于 节点的properties属性信息
                         */
                        String tempPath=nodesMap.get(startID).asMap() + "-[" + rType+"{"+aa+"}" + "]-" + nodesMap.get(endID).asMap();
                        System.out.println("路径关系 : "+tempPath);
                        resultPath.add(tempPath);
                    }
                }
            }

        }


        return  ;
    }


    private void queryq(Session session) {
        Map<String, Object> parameter = new HashMap();
        StringJoiner relationJoiner = new StringJoiner(" ");
        parameter.put("serviceNbr","15375231938");
        relationJoiner.add("MATCH (user:User)-[r]-(person:NaturalPerson)");
        relationJoiner.add("WHERE user.serviceNbr =  {serviceNbr} ");
        relationJoiner.add("RETURN person.oneidPrdInstId,user.prdInstId,user.prdInstName,user.serviceNbr,user.userAccount,user.prdId,user.prdInstStasId,user.installDate,user.completeDate,user.acctId,user.custId,user.netNum,user.cImsi,user.lImsi,user.gImsi,user.simCard,user.meid,user.imei");
        Statement statement = new Statement(relationJoiner.toString(), parameter);
        StatementResult result= session.run(statement, TransactionConfig.empty());
        List<Record> aa= result.list();
        logger.info("result.list().size="+result.list().size());

        for(int i=0;i< aa.size();i++){
            Record record=aa.get(i);
            logger.info("person.oneidPrdInstId ="+record.asMap());
        }



    }

    private  Neo4jDatasource neo4jDatasource() throws Exception {
        Map<String, Object> config=new HashMap<String, Object>();
        config.put("uri",uri);
        config.put("userName",userName);
        config.put("password",password);
        config.put("connectionTimeoutMillis",connectionTimeoutMillis);
        config.put("maxConnectionPoolSize",maxConnectionPoolSize);
        logger.info("--------config="+config);
        return DataSourceFactoryUtil.buildNeo4jDatasource(config);
    }


}
