package com.tydic.backgroundInsert;

import com.tydic.framework.util.BatchUtil;
import com.tydic.framework.util.GroupUtil;
import com.tydic.framework.util.ObjectUtil;
import com.tydic.framework.util.StringUtil;
import com.tydic.neo4j.Neo4jSessionManager;
import com.tydic.scaffold.batchInsertNeo4j.dao.QueryNaturalPersonsDao;
import com.tydic.scaffold.batchInsertNeo4j.model.NaturalPersonEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.NaturalRelEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.UserEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.UserRelEntity;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: batchInsertThread
 * @Description
 * @Author: hubin
 * @Date: 2020-08-13
 * @Version:v1.0
 */
@Component
@Scope("prototype")//spring 多例
public class BatchInsertNodeThread implements Runnable {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private int threadNum;
    private Session session = null;
    private QueryNaturalPersonsDao queryNaturalPersonsDao;
    private int mod;
    private String nodeName;

    public BatchInsertNodeThread(int threadNum, Session session, QueryNaturalPersonsDao queryNaturalPersonsDao, int mod, String nodeName) {
        this.threadNum = threadNum;
        this.session = session;
        this.queryNaturalPersonsDao = queryNaturalPersonsDao;
        this.mod = mod;
        this.nodeName = nodeName;
    }

    @Override
    public void run() {

        logger.info("begin insert node run =" + threadNum);
        Map<String, Integer> param = new HashMap<String, Integer>();
        param.put("mod", mod);
        param.put("index", threadNum);



        //NaturalPerson/User/Family/LittleFamily/Enterprise/QQ/Aqiyi/Email/Jd/Taobao/Wechat/Weibo/Youku
        if ("NaturalPerson".equalsIgnoreCase(nodeName)) {

         //  批量导入自然人节点到图数据库
            BatchInsertNaturalNodeAndRelation batchInsertNaturalNode = new BatchInsertNaturalNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            batchInsertNaturalNode.bacthInsertNaturalNode(param);

        } else if ("User".equalsIgnoreCase(nodeName)) {
            //批量导入用户节点到图数据库
            BacthInsertUserNodeAndRelation bacthInsertUserNode = new BacthInsertUserNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertUserNode.bacthInsertUserNode(param);

        } else if ("Family".equalsIgnoreCase(nodeName)) {

            //批量导入家庭节点到图数据库
            BacthInsertFamilyNodeAndRelation bacthInsertFamilyNode = new BacthInsertFamilyNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertFamilyNode.bacthInsertFamilyNode(param);
            bacthInsertFamilyNode.bacthInsertLittleFamilyNode(param);
        } else if ("Enterprise".equalsIgnoreCase(nodeName)) {

            //批量导入企业节点到图数据库
            BacthInsertEnterpriseNodeAndRelation bacthInsertEnterpriseNode = new BacthInsertEnterpriseNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertEnterpriseNode.bacthInsertEnterpriseNode(param);
        } else if ("QQ".equalsIgnoreCase(nodeName)) {

            //批量导入qq节点到图数据库
            BacthInsertQQNodeAndRelation bacthInsertQQNode = new BacthInsertQQNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertQQNode.bacthInsertQQNode(param);
        } else if ("Email".equalsIgnoreCase(nodeName)) {
            //批量导入Email节点到图数据库
            BacthInsertEmailNodeAndRelation bacthInsertEmailNode = new BacthInsertEmailNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertEmailNode.bacthInsertEmailNode(param);
        } else if ("Wechat".equalsIgnoreCase(nodeName)) {
            //批量导入Wechat节点到图数据库
            BacthInsertWechatNodeAndRelation bacthInsertWechatNode = new BacthInsertWechatNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertWechatNode.bacthInsertWechatNode(param);
        } else if ("Jd".equalsIgnoreCase(nodeName)) {

            //批量导入Jd节点到图数据库
            BacthInsertJdNodeAndRelation bacthInsertJdNode = new BacthInsertJdNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertJdNode.bacthInsertJdNode(param);
        } else if ("Taobao".equalsIgnoreCase(nodeName)) {

            //批量导入TaoBao节点到图数据库
            BacthInsertTaobaoNodeAndRelation bacthInsertTaobaoNode = new BacthInsertTaobaoNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertTaobaoNode.bacthInsertTaobaoNode(param);
        } else if ("Weibo".equalsIgnoreCase(nodeName)) {

            //批量导入Weibo节点到图数据库
            BacthInsertWeiboNodeAndRelation bacthInsertWeiboNode = new BacthInsertWeiboNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertWeiboNode.bacthInsertWeiboNode(param);
        } else if ("Youku".equalsIgnoreCase(nodeName)) {
            //批量导入Youku节点到图数据库
            BacthInsertYoukuNodeAndRelation bacthInsertYoukuNode = new BacthInsertYoukuNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertYoukuNode.bacthInsertYoukuNode(param);
        } else if ("Aqiyi".equalsIgnoreCase(nodeName)) {

            //批量导入Aqiyi节点到图数据库
            BacthInsertAqiyiNodeAndRelation bacthInsertAqiyiNode = new BacthInsertAqiyiNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertAqiyiNode.bacthInsertAqiyiNode(param);
        } else {
            logger.error("不支持的节点名称 nodeName=" + nodeName);
        }
        logger.info("end  insert Node run =" + threadNum);

    }


    private void deleteNodeCommon(String sql){


    }

}
