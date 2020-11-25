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
public class BatchInsertRelationThread implements Runnable {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private int threadNum;
    private Session session = null;
    private QueryNaturalPersonsDao queryNaturalPersonsDao;
    private int mod;
    private String relationName;

    public BatchInsertRelationThread(int threadNum, Session session, QueryNaturalPersonsDao queryNaturalPersonsDao, int mod, String relationName) {
        this.threadNum = threadNum;
        this.session = session;
        this.queryNaturalPersonsDao = queryNaturalPersonsDao;
        this.mod = mod;
        this.relationName = relationName;
    }

    @Override
    public void run() {

        Map<String, Integer> param = new HashMap<String, Integer>();
        param.put("mod", mod);
        param.put("index", threadNum);
        logger.info("begin insert relations run =" + threadNum);

        //EnterpriseRel/FamilyRel/LittleFamilyRel/UserNaturalRel/aqiyi_natural/email_natural/jd_natural/person_rel/qq_natural/taobao_natural/wechat_natural/weibo_natural/youku_natural
        if ("UserNaturalRel".equalsIgnoreCase(relationName)) {
            //批量导入用户与自然人节点到图数据库
            BacthInsertUserNodeAndRelation bacthInsertUserNode = new BacthInsertUserNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertUserNode.bacthInsertUserRel(param);
        } else if ("person_rel".equalsIgnoreCase(relationName)) {
            //批量导入自然人与自然人关系到图数据库
            BatchInsertNaturalNodeAndRelation batchInsertNaturalNode = new BatchInsertNaturalNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            batchInsertNaturalNode.bacthInsertNaturalRel(param);
        } else if ("FamilyRel".equalsIgnoreCase(relationName)) {
            //批量导入家庭关系节点到图数据库
            BacthInsertFamilyNodeAndRelation bacthInsertFamilyNode = new BacthInsertFamilyNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertFamilyNode.bacthInsertFamilyAndLittleFamilyRel(param);

            bacthInsertFamilyNode.bacthInsertLittleFamilyAndNaturalRel(param);
        } else if ("EnterpriseRel".equalsIgnoreCase(relationName)) {
            //批量导入企业自然人关系节点到图数据库
            BacthInsertEnterpriseNodeAndRelation bacthInsertEnterpriseNode = new BacthInsertEnterpriseNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertEnterpriseNode.bacthInsertEnterpriseRel(param);
        } else if ("qq_natural".equalsIgnoreCase(relationName)) {
            //批量导入qq自然人关系到图数据库
            BacthInsertQQNodeAndRelation bacthInsertQQNode = new BacthInsertQQNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertQQNode.bacthInsertQQRel(param);
        } else if ("email_natural".equalsIgnoreCase(relationName)) {
            //批量导入Email自然人关系图数据库
            BacthInsertEmailNodeAndRelation bacthInsertEmailNode = new BacthInsertEmailNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertEmailNode.bacthInsertEmailRel(param);
        } else if ("wechat_natural".equalsIgnoreCase(relationName)) {
            //批量导入Wechat自然人关系到图数据库
            BacthInsertWechatNodeAndRelation bacthInsertWechatNode = new BacthInsertWechatNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertWechatNode.bacthInsertWechatRel(param);
        } else if ("jd_natural".equalsIgnoreCase(relationName)) {
            //批量导入Jd自然人关系到图数据库
            BacthInsertJdNodeAndRelation bacthInsertJdNode = new BacthInsertJdNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertJdNode.bacthInsertJdRel(param);
        } else if ("taobao_natural".equalsIgnoreCase(relationName)) {
            //批量导入TaoBao自然人关系到图数据库
            BacthInsertTaobaoNodeAndRelation bacthInsertTaobaoNode = new BacthInsertTaobaoNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertTaobaoNode.bacthInsertTaobaoRel(param);
        } else if ("weibo_natural".equalsIgnoreCase(relationName)) {
            //批量导入Weibo自然人关系到图数据库
            BacthInsertWeiboNodeAndRelation bacthInsertWeiboNode = new BacthInsertWeiboNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertWeiboNode.bacthInsertWeiboRel(param);
        } else if ("youku_natural".equalsIgnoreCase(relationName)) {
            //批量导入Youku自然人关系到图数据库
            BacthInsertYoukuNodeAndRelation bacthInsertYoukuNode = new BacthInsertYoukuNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertYoukuNode.bacthInsertYoukuRel(param);
        } else if ("aqiyi_natural".equalsIgnoreCase(relationName)) {
            //批量导入Aqiyi自然人关系到图数据库
            BacthInsertAqiyiNodeAndRelation bacthInsertAqiyiNode = new BacthInsertAqiyiNodeAndRelation(session, queryNaturalPersonsDao, threadNum);
            bacthInsertAqiyiNode.bacthInsertAqiyiQRel(param);
        } else {
            logger.error("不支持的关系名称 relationName=" + relationName);
        }


        logger.info("end  insert relations run =" + threadNum);

    }

}
