package com.tydic.backgroundInsert;

import com.tydic.neo4j.DataSourceFactoryUtil;
import com.tydic.neo4j.Neo4jConnectionUtil;
import com.tydic.neo4j.Neo4jDatasource;
import com.tydic.neo4j.Neo4jSessionManager;
import com.tydic.scaffold.batchInsertNeo4j.dao.QueryNaturalPersonsDao;
import com.tydic.scaffold.batchInsertNeo4j.model.BatchInsertNodeEntity;
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
public class BatchInsertNode {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${fixThrea.fixThreadNum:}")
    private int fixThreadNum;

    @Value("${neo4j.uri:}")
    private String uri = "";

    @Value("${neo4j.userName:}")
    private String userName = "";

    @Value("${neo4j.password:}")
    private String password = "";

    private Driver driver;

    @Value("${neo4j.maxConnectionPoolSize:}")
    private String maxConnectionPoolSize = "";

    @Value("${neo4j.connectionTimeoutMillis:}")
    private String connectionTimeoutMillis = "";

    @Autowired
    Neo4jSessionManager neo;

    public void startThreadNode(QueryNaturalPersonsDao queryNaturalPersonsDao, BatchInsertNodeEntity nodeEntity) throws Exception {

        int tempThreadNum = nodeEntity.getThreadNum();
        String nodeName = nodeEntity.getNodeName();
        logger.info("BatchInsertNode startThreadNode confThreadNum=" + fixThreadNum + ",intoThreadNum=" + tempThreadNum + ",nodeName=" + nodeName);

        if (tempThreadNum > 0) {
            fixThreadNum = tempThreadNum;
        }
        logger.info("BatchInsertNode  startThread =" + fixThreadNum);
        //构造一个线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                fixThreadNum + 1, fixThreadNum + 1, 10000, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(100),
                new ThreadPoolExecutor.DiscardOldestPolicy());

        Map<String, String> naturalNodeMap = new ConcurrentHashMap<String, String>();
        Session session = neo.getSession();

        //queryq(session);

        if (!deleteInit(session, nodeName)) {
            return;
        }
        for (int i = 0; i < fixThreadNum; i++) {

            if (session == null) {
                Neo4jDatasource neo4jDatasourceInner = neo4jDatasource();
                if (neo4jDatasourceInner == null) {
                    throw new NullPointerException("neo4jDatasourceInner construct is null");
                }

                session = Neo4jConnectionUtil.session(neo4jDatasourceInner);

            }
            BatchInsertNodeThread backInvoiceBankWork = new BatchInsertNodeThread(i, session, queryNaturalPersonsDao, fixThreadNum, nodeName);
            threadPool.execute(backInvoiceBankWork);
            logger.info("BatchInsertNode  fixThreadNum =" + i);
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

    private boolean deleteInit(Session session, String nodeName) {

        //NaturalPerson/User/Family/LittleFamily/Enterprise/QQ/Aqiyi/Email/Jd/Taobao/Wechat/Weibo/Youku
        if ("NaturalPerson".equalsIgnoreCase(nodeName)) {

            String deleteSql = "match p=()-[r]-() delete r";//因为其他所有节点均与自然人节点有关系，故直接删除所有关系  再建立节点
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (e: NaturalPerson) DELETE e";
            deleteNodeCommon(session, deleteSql, nodeName);//删除自然人节点

        } else if ("User".equalsIgnoreCase(nodeName)) {
            String deleteSql = "MATCH (cust:User)-[t]-(cc:NaturalPerson) DELETE t";//删除用户与自然人关系
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (e: User) DELETE e";//删除用户节点
            deleteNodeCommon(session, deleteSql, nodeName);

        } else if ("Family".equalsIgnoreCase(nodeName)) {

            String deleteSql = "MATCH (cust:LittleFamily)-[t]-(cc:Family) DELETE t";//删除家庭与小家庭关系
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (cust:NaturalPerson)-[t]-(cc:LittleFamily) DELETE t";//删除自然人与小家庭关系
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (e: Family) DELETE e";//删除家庭节点
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (e: LittleFamily) DELETE e";//删除小家庭节点
            deleteNodeCommon(session, deleteSql, nodeName);

        } else if ("Enterprise".equalsIgnoreCase(nodeName)) {
            String deleteSql = "MATCH (cust:NaturalPerson)-[t]-(cc:Enterprise) DELETE t";//删除自然人与企业关系
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (cust:Enterprise)-[t] DELETE t";//删除企业节点
            deleteNodeCommon(session, deleteSql, nodeName);

        } else if ("QQ".equalsIgnoreCase(nodeName)) {
            String deleteSql = "MATCH (cust:NaturalPerson)-[t]-(cc:QQ) DELETE t";//删除自然人与QQ关系
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (cust:QQ)-[t] DELETE t";//删除QQ节点
            deleteNodeCommon(session, deleteSql, nodeName);

        } else if ("Email".equalsIgnoreCase(nodeName)) {
            String deleteSql = "MATCH (cust:NaturalPerson)-[t]-(cc:Email) DELETE t";//删除自然人与Email关系
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (cust:Email)-[t] DELETE t";//删除Email节点
            deleteNodeCommon(session, deleteSql, nodeName);

        } else if ("Wechat".equalsIgnoreCase(nodeName)) {
            String deleteSql = "MATCH (cust:NaturalPerson)-[t]-(cc:Wechat) DELETE t";//删除自然人与Wechat关系
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (cust:Wechat)-[t] DELETE t";//删除Wechat节点
            deleteNodeCommon(session, deleteSql, nodeName);

        } else if ("Jd".equalsIgnoreCase(nodeName)) {
            String deleteSql = "MATCH (cust:NaturalPerson)-[t]-(cc:Jd) DELETE t";//删除自然人与Jd关系
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (cust:Jd)-[t] DELETE t";//删除Jd节点
            deleteNodeCommon(session, deleteSql, nodeName);

        } else if ("Taobao".equalsIgnoreCase(nodeName)) {
            String deleteSql = "MATCH (cust:NaturalPerson)-[t]-(cc:Taobao) DELETE t";//删除自然人与Taobao关系
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (cust:Taobao)-[t] DELETE t";//删除Taobao节点
            deleteNodeCommon(session, deleteSql, nodeName);

        } else if ("Weibo".equalsIgnoreCase(nodeName)) {
            String deleteSql = "MATCH (cust:NaturalPerson)-[t]-(cc:Weibo) DELETE t";//删除自然人与QQ关系
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (cust:Weibo)-[t] DELETE t";//删除Weibo节点
            deleteNodeCommon(session, deleteSql, nodeName);

        } else if ("Youku".equalsIgnoreCase(nodeName)) {
            String deleteSql = "MATCH (cust:NaturalPerson)-[t]-(cc:Youku) DELETE t";//删除自然人与Youku关系
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (cust:Youku)-[t] DELETE t";//删除Youku节点
            deleteNodeCommon(session, deleteSql, nodeName);

        } else if ("Aqiyi".equalsIgnoreCase(nodeName)) {
            String deleteSql = "MATCH (cust:NaturalPerson)-[t]-(cc:Aqiyi) DELETE t";//删除自然人与Aqiyi关系
            deleteNodeCommon(session, deleteSql, nodeName);

            deleteSql = "MATCH (cust:Aqiyi)-[t] DELETE t";//删除Aqiyi节点
            deleteNodeCommon(session, deleteSql, nodeName);

        } else {
            logger.error("不支持的节点名称 nodeName=" + nodeName);
            return false;
        }
        return true;
    }

    private void deleteNodeCommon(Session session, String deleteSql, String nodeName) {
        Map<String, Object> parameter = new HashMap();
        logger.info("begin delete  nodeName=" + nodeName + ",by sql=" + deleteSql);
        Statement statement = new Statement(deleteSql, parameter);
        try {
            session.run(statement, TransactionConfig.empty());
        } catch (Exception e) {
            int i = 0;
            while (i < 3) {
                try {
                    session.run(statement, TransactionConfig.empty());
                    i = 3;
                } catch (Exception e1) {
                    i++;
                    if (i == 3) {
                        logger.error(e1 + e1.getMessage());
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        Neo4jSessionManager neo = new Neo4jSessionManager();
        Session session = neo.getSession();

        List<String> resultPath=queryShort(session);

        for(String ss : resultPath){
            System.out.println(ss);
        }
    }


    private static List<String> queryShort(Session session) {
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
                    String path = "";
                    for (Relationship relationship : relationships) {
                        Long startID = relationship.startNodeId();
                        Long endID = relationship.endNodeId();
                        String rType = relationship.type();
                        Map<String, Object> relationMap = relationship.asMap();
                        /**
                         * asMap 相当于 节点的properties属性信息
                         */
                        String startPath="{no:" + nodesMap.get(startID).asMap().get("no") +"}";
                        String relationType="-{" + rType+ "}-";
                        String endPath="{no:" + nodesMap.get(endID).asMap().get("no")+"}";

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
                        System.out.println("路径关系 : " + path);

                    }
                    resultPath.add(path);
                }
            }

        }


        return resultPath;
    }


    private void queryq(Session session) {
        Map<String, Object> parameter = new HashMap();
        StringJoiner relationJoiner = new StringJoiner(" ");
        parameter.put("serviceNbr", "15375231938");
        relationJoiner.add("MATCH (user:User)-[r]-(person:NaturalPerson)");
        relationJoiner.add("WHERE user.serviceNbr =  {serviceNbr} ");
        relationJoiner.add("RETURN person.oneidPrdInstId,user.prdInstId,user.prdInstName,user.serviceNbr,user.userAccount,user.prdId,user.prdInstStasId,user.installDate,user.completeDate,user.acctId,user.custId,user.netNum,user.cImsi,user.lImsi,user.gImsi,user.simCard,user.meid,user.imei");
        Statement statement = new Statement(relationJoiner.toString(), parameter);
        StatementResult result = session.run(statement, TransactionConfig.empty());
        List<Record> aa = result.list();
        logger.info("result.list().size=" + result.list().size());

        for (int i = 0; i < aa.size(); i++) {
            Record record = aa.get(i);
            logger.info("person.oneidPrdInstId =" + record.asMap());
        }


    }

    private Neo4jDatasource neo4jDatasource() throws Exception {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("uri", uri);
        config.put("userName", userName);
        config.put("password", password);
        config.put("connectionTimeoutMillis", connectionTimeoutMillis);
        config.put("maxConnectionPoolSize", maxConnectionPoolSize);
        logger.info("--------config=" + config);
        return DataSourceFactoryUtil.buildNeo4jDatasource(config);
    }


}
