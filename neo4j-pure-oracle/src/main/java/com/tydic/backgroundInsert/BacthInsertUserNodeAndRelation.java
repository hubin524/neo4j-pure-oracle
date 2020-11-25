package com.tydic.backgroundInsert;

import com.tydic.framework.util.BatchUtil;
import com.tydic.framework.util.GroupUtil;
import com.tydic.framework.util.ObjectUtil;
import com.tydic.framework.util.StringUtil;
import com.tydic.scaffold.batchInsertNeo4j.dao.QueryNaturalPersonsDao;
import com.tydic.scaffold.batchInsertNeo4j.model.UserEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.UserRelEntity;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.TransactionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @ClassName: bacthInsertUserNodeAndRelation
 * @Description
 * @Author: hubin
 * @Date: 2020-08-18
 * @Version:v1.0
 */
@Component
@Scope("prototype")//spring 多例
public class BacthInsertUserNodeAndRelation {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private Session session = null;
    private QueryNaturalPersonsDao queryNaturalPersonsDao;
    private int threadNum;

    public BacthInsertUserNodeAndRelation(Session session, QueryNaturalPersonsDao queryNaturalPersonsDao, int threadNum) {
        this.session = session;
        this.queryNaturalPersonsDao = queryNaturalPersonsDao;
        this.threadNum=threadNum;
    }

    public void bacthInsertUserNode(Map<String, Integer> param) {
        // 用户源数据提取
        List<UserEntity> naturalPersonList = queryNaturalPersonsDao.QueryUserNode(param);
        if (ObjectUtil.isNotEmpty(naturalPersonList)) {
            logger.info("thread [" + threadNum + "] need  synchronize [" + naturalPersonList.size() + "] user node to neo4j");
            Map<String, List<UserEntity>> nodeByLabel = new HashMap();

            for (int i = 0; i < naturalPersonList.size(); i++) {
                UserEntity userEntity = new UserEntity();
                userEntity = naturalPersonList.get(i);
                GroupUtil.group(nodeByLabel, userEntity.getOneidLabels()).add(userEntity);
            }//组装标签

            logger.info("nodeByLabel count=：" + nodeByLabel.size());

            Set<String> labelSet = nodeByLabel.keySet();
            Iterator var15 = labelSet.iterator();

            while (var15.hasNext()) {
                String label = (String) var15.next();
                StringJoiner labelJoiner = new StringJoiner(":");
                if (ObjectUtil.isEmpty(label)) {
                    labelJoiner.add("User");
                } else {
                    String[] sps = label.split(",");
                    String[] var7 = sps;
                    int var8 = sps.length;

                    for (int var9 = 0; var9 < var8; ++var9) {
                        String sp = var7[var9];
                        labelJoiner.add(StringUtil.neo4jLabel(sp));
                    }
                }

                List<Map<String, Object>> nodes = new ArrayList();
                List<UserEntity> ns = (List) nodeByLabel.get(label);
                Iterator var18 = ns.iterator();

                while (var18.hasNext()) {//组装数据
                    UserEntity userEntity = (UserEntity) var18.next();
                    Map<String, Object> map = new LinkedHashMap();

                    map.put("serviceNbr", StringUtil.StringDeal(userEntity.getServiceNbr()));
                    map.put("prdInstId", StringUtil.StringDeal(userEntity.getPrdInstId()));
                    map.put("prdInstName", StringUtil.StringDeal(userEntity.getPrdInstName()));
                    map.put("userAccount", StringUtil.StringDeal(userEntity.getUserAccount()));
                    map.put("prdId", StringUtil.StringDeal(userEntity.getPrdId()));
                    map.put("prdInstStasId", StringUtil.StringDeal(userEntity.getPrdInstStasId()));
                    map.put("installDate", StringUtil.StringDeal(userEntity.getInstallDate()));
                    map.put("completeDate", StringUtil.StringDeal(userEntity.getCompleteDate()));
                    map.put("acctId", StringUtil.StringDeal(userEntity.getAcctId()));
                    map.put("custId", StringUtil.StringDeal(userEntity.getCustId()));
                    map.put("netNum", StringUtil.StringDeal(userEntity.getNetNum()));
                    map.put("cImsi", StringUtil.StringDeal(userEntity.getcImsi()));
                    map.put("lImsi", StringUtil.StringDeal(userEntity.getgImsi()));
                    map.put("gImsi", StringUtil.StringDeal(userEntity.getgImsi()));
                    map.put("simCard", StringUtil.StringDeal(userEntity.getSimCard()));
                    map.put("meid", StringUtil.StringDeal(userEntity.getMeid()));
                    map.put("imei", StringUtil.StringDeal(userEntity.getImei()));


                    nodes.add(map);
                }

                List<List<Map<String, Object>>> batches = BatchUtil.batch(nodes, 1000);
                Iterator var21 = batches.iterator();

                while (var21.hasNext()) {//分批次插入
                    List<Map<String, Object>> batch = (List) var21.next();
                    if (!ObjectUtil.isEmpty(batch)) {
                        Map<String, Object> parameter = new HashMap();
                        parameter.put("nodes", batch);
                        StringJoiner nodesJoiner = new StringJoiner(" ");
                        nodesJoiner.add("UNWIND $nodes AS node");
                        nodesJoiner.add("MERGE (n{prdInstId:node.prdInstId,prdInstName:node.prdInstName,serviceNbr:node.serviceNbr,userAccount:node.userAccount,prdId:node.prdId,prdInstStasId:node.prdInstStasId,installDate:node.installDate,completeDate:node.completeDate,acctId:node.acctId,custId:node.custId,netNum:node.netNum,cImsi:node.cImsi,lImsi:node.lImsi,gImsi:node.gImsi,simCard:node.simCard,meid:node.meid,imei:node.imei})");
                        nodesJoiner.add("set n+=node,n:" + labelJoiner);
                        Statement statement = new Statement(nodesJoiner.toString(), parameter);


                        try {
                            this.session.run(statement, TransactionConfig.empty());
                        }catch(Exception e){
                            int i=0;
                            while(i < 3){
                                try {
                                    this.session.run(statement, TransactionConfig.empty());
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
                }
            }


        }


    }

    public void bacthInsertUserRel(Map<String, Integer> param) {
        // 用户、自然人关系源数据提取
        List<UserRelEntity> userRelList = queryNaturalPersonsDao.QueryUserRel(param);
        if (ObjectUtil.isNotEmpty(userRelList)) {
            logger.info("thread [" + threadNum + "] need  synchronize [" + userRelList.size() + "] User Rel to neo4j");

            Map<String, List<UserRelEntity>> relationByLabel = new HashMap();
            for (int i = 0; i < userRelList.size(); i++) {
                UserRelEntity userRelEntity = userRelList.get(i);
                GroupUtil.group(relationByLabel, userRelEntity.getOneidLabels()).add(userRelEntity);
            }

            Set<String> labelSet = relationByLabel.keySet();
            Iterator var15 = labelSet.iterator();

            while (var15.hasNext()) {
                String label = (String) var15.next();
                StringJoiner labelJoiner = new StringJoiner(":");
                if (ObjectUtil.isEmpty(label)) {
                    labelJoiner.add("UserNatural");
                } else {
                    labelJoiner.add(StringUtil.neo4jLabel(label.split(",")[0]));
                }

                List<UserRelEntity> tempnaturalPersonRelList = (List) relationByLabel.get(label);
                List<Map<String, Object>> relations = new ArrayList();
                Iterator var8 = tempnaturalPersonRelList.iterator();

                while (var8.hasNext()) {
                    UserRelEntity relation = (UserRelEntity) var8.next();
                    Map<String, Object> map = new LinkedHashMap();

                    map.put("oneidPrdInstId", StringUtil.StringDeal(relation.getOneidPrdInstId()));
                    map.put("latnId", StringUtil.StringDeal(relation.getLatnId()));
                    map.put("prdInstId", StringUtil.StringDeal(relation.getPrdInstId()));

                    relations.add(map);
                }

                List<List<Map<String, Object>>> batches = BatchUtil.batch(relations, 1000);
                Iterator var17 = batches.iterator();

                while (var17.hasNext()) {
                    List<Map<String, Object>> batch = (List) var17.next();
                    Map<String, Object> parameter = new HashMap();
                    parameter.put("relations", batch);
                    StringJoiner relationJoiner = new StringJoiner(" ");
                    relationJoiner.add("unwind $relations as relation");
                    relationJoiner.add("MATCH (cust:NaturalPerson),(cc:User) WHERE cust.oneidPrdInstId = relation.oneidPrdInstId AND cc.prdInstId= relation.prdInstId");
                    relationJoiner.add("MERGE (cc)-[t:UserNaturalRel{latnId:relation.latnId}]->(cust)");//用户指向自然人  归属自然人
                    Statement statement = new Statement(relationJoiner.toString(), parameter);
                    try {
                        this.session.run(statement, TransactionConfig.empty());
                    }catch(Exception e){
                        int i=0;
                        while(i < 3){
                            try {
                                this.session.run(statement, TransactionConfig.empty());
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
            }


        }
    }

}
