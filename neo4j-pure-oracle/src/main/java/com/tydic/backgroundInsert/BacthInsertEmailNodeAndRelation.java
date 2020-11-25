package com.tydic.backgroundInsert;

import com.tydic.framework.util.BatchUtil;
import com.tydic.framework.util.GroupUtil;
import com.tydic.framework.util.ObjectUtil;
import com.tydic.framework.util.StringUtil;
import com.tydic.scaffold.batchInsertNeo4j.dao.QueryNaturalPersonsDao;
import com.tydic.scaffold.batchInsertNeo4j.model.InternetAccountEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.NaturalPersonEntity;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.TransactionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @ClassName: bacthInsertEmailNodeAndRelation
 * @Description
 * @Author: hubin
 * @Date: 2020-08-18
 * @Version:v1.0
 */
@Component
@Scope("prototype")//spring 多例
public class BacthInsertEmailNodeAndRelation {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private Session session = null;
    private QueryNaturalPersonsDao queryNaturalPersonsDao;
    private int threadNum;

    public BacthInsertEmailNodeAndRelation(Session session, QueryNaturalPersonsDao queryNaturalPersonsDao, int threadNum) {
        this.session = session;
        this.queryNaturalPersonsDao = queryNaturalPersonsDao;
        this.threadNum=threadNum;
    }

    public void bacthInsertEmailNode(Map<String, Integer> param) {
        // 自然人源数据提取
        List<InternetAccountEntity> internetAccountList = queryNaturalPersonsDao.QueryEmailNode(param);
        if (ObjectUtil.isNotEmpty(internetAccountList)) {
            logger.info("thread [" + threadNum + "] need  synchronize [" + internetAccountList.size() + "] Email node to neo4j");
            Map<String, List<InternetAccountEntity>> nodeByLabel = new HashMap();

            for (int i = 0; i < internetAccountList.size(); i++) {
                InternetAccountEntity internetAccountEntity = new InternetAccountEntity();
                internetAccountEntity = internetAccountList.get(i);
                // naturalNodeMap.put(naturalPersonEntity.getOneidPrdInstId(),naturalPersonEntity);
                GroupUtil.group(nodeByLabel, "Email").add(internetAccountEntity);
            }//组装标签

            logger.info("nodeByLabel count=：" + nodeByLabel.size());

            Set<String> labelSet = nodeByLabel.keySet();
            Iterator var15 = labelSet.iterator();

            while (var15.hasNext()) {
                String label = (String) var15.next();
                StringJoiner labelJoiner = new StringJoiner(":");
                if (ObjectUtil.isEmpty(label)) {
                    labelJoiner.add("Email");
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
                List<InternetAccountEntity> ns = (List) nodeByLabel.get(label);
                Iterator var18 = ns.iterator();

                while (var18.hasNext()) {//组装数据
                    InternetAccountEntity n = (InternetAccountEntity) var18.next();
                    Map<String, Object> map = new LinkedHashMap();
                    map.put("emailId", StringUtil.StringDeal(n.getEmailId()));
//                    map.put("oneidPrdInstId", n.getOneidPrdInstId());
//                    map.put("prdInstId", n.getPrdInstId());
//                    map.put("serviceNbr", n.getServiceNbr());
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
                        // nodesJoiner.add("MERGE (n{emailId:node.emailId,prdInstId:node.prdInstId,oneidPrdInstId:node.oneidPrdInstId,serviceNbr:node.serviceNbr})");
                        nodesJoiner.add("MERGE (n{emailId:node.emailId})");
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


            internetAccountList.clear();
        }

    }

    public void bacthInsertEmailRel(Map<String, Integer> param) {
        // 用户、自然人关系源数据提取
        List<InternetAccountEntity> userRelList = queryNaturalPersonsDao.QueryEmailNaturalRel(param);
        if (ObjectUtil.isNotEmpty(userRelList)) {
            logger.info("thread [" + threadNum + "] need  synchronize [" + userRelList.size() + "] email_natural Rel to neo4j");

            Map<String, List<InternetAccountEntity>> relationByLabel = new HashMap();
            for (int i = 0; i < userRelList.size(); i++) {
                InternetAccountEntity userRelEntity = userRelList.get(i);
                GroupUtil.group(relationByLabel, "email_natural").add(userRelEntity);
            }

            Set<String> labelSet = relationByLabel.keySet();
            Iterator var15 = labelSet.iterator();

            while (var15.hasNext()) {
                String label = (String) var15.next();
                StringJoiner labelJoiner = new StringJoiner(":");
                if (ObjectUtil.isEmpty(label)) {
                    labelJoiner.add("email_natural");
                } else {
                    labelJoiner.add(StringUtil.neo4jLabel(label.split(",")[0]));
                }

                List<InternetAccountEntity> tempnaturalPersonRelList = (List) relationByLabel.get(label);
                List<Map<String, Object>> relations = new ArrayList();
                Iterator var8 = tempnaturalPersonRelList.iterator();

                while (var8.hasNext()) {
                    InternetAccountEntity relation = (InternetAccountEntity) var8.next();
                    Map<String, Object> map = new LinkedHashMap();

                    map.put("oneidPrdInstId", StringUtil.StringDeal(relation.getOneidPrdInstId()));
                    map.put("latnId", StringUtil.StringDeal(relation.getLatnId()));
                    map.put("prdInstId", StringUtil.StringDeal(relation.getPrdInstId()));
                    map.put("serviceNbr", StringUtil.StringDeal(relation.getServiceNbr()));
                    map.put("emailId", StringUtil.StringDeal(relation.getEmailId()));
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
                    relationJoiner.add("MATCH (cc:Email),(cust:NaturalPerson) WHERE cc.emailId = relation.emailId AND cust.oneidPrdInstId= relation.oneidPrdInstId");
                    relationJoiner.add("MERGE (cc)-[t:email_natural{emailId:relation.emailId,oneidPrdInstId:relation.oneidPrdInstId,prdInstId:relation.prdInstId,serviceNbr:relation.serviceNbr,latnId:relation.latnId}]->(cust)");//QQ指向自然人  归属自然人
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
