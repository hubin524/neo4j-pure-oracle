package com.tydic.backgroundInsert;

import com.tydic.framework.util.BatchUtil;
import com.tydic.framework.util.GroupUtil;
import com.tydic.framework.util.ObjectUtil;
import com.tydic.framework.util.StringUtil;
import com.tydic.scaffold.batchInsertNeo4j.dao.QueryNaturalPersonsDao;
import com.tydic.scaffold.batchInsertNeo4j.model.FamilyEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.LittleFamilyEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.LittleFamilyRelEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.LittleFamilyRelEntity;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.TransactionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: BacthInsertFamilyNodeAndRelation
 * @Description
 * @Author: hubin
 * @Date: 2020-08-18
 * @Version:v1.0
 */
@Component
@Scope("prototype")//spring 多例
public class BacthInsertFamilyNodeAndRelation {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private Session session = null;
    private QueryNaturalPersonsDao queryNaturalPersonsDao;
    private int threadNum;

    public BacthInsertFamilyNodeAndRelation(Session session, QueryNaturalPersonsDao queryNaturalPersonsDao, int threadNum) {
        this.session = session;
        this.queryNaturalPersonsDao = queryNaturalPersonsDao;
        this.threadNum=threadNum;
    }

    public void bacthInsertFamilyNode(Map<String, Integer> param) {
        // FamilyNode源数据提取
        List<FamilyEntity> naturalPersonList = queryNaturalPersonsDao.QueryFamilyNode(param);
        if (ObjectUtil.isNotEmpty(naturalPersonList)) {
            logger.info("thread [" + threadNum + "] need  synchronize [" + naturalPersonList.size() + "] Family node to neo4j");
            Map<String, List<FamilyEntity>> nodeByLabel = new HashMap();

            for (int i = 0; i < naturalPersonList.size(); i++) {
                FamilyEntity naturalPersonEntity = new FamilyEntity();
                naturalPersonEntity = naturalPersonList.get(i);
                // naturalNodeMap.put(naturalPersonEntity.getOneidPrdInstId(),naturalPersonEntity);
                GroupUtil.group(nodeByLabel, "Family").add(naturalPersonEntity);
            }//组装标签

            logger.info("nodeByLabel count=：" + nodeByLabel.size());

            Set<String> labelSet = nodeByLabel.keySet();
            Iterator var15 = labelSet.iterator();

            while (var15.hasNext()) {
                String label = (String) var15.next();
                StringJoiner labelJoiner = new StringJoiner(":");
                if (ObjectUtil.isEmpty(label)) {
                    labelJoiner.add("Family");
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
                List<FamilyEntity> ns = (List) nodeByLabel.get(label);
                Iterator var18 = ns.iterator();

                while (var18.hasNext()) {//组装数据
                    FamilyEntity n = (FamilyEntity) var18.next();
                    Map<String, Object> map = new LinkedHashMap();
                    map.put("encryptVirtualFamilyId", StringUtil.StringDeal(n.getEncryptVirtualFamilyId()));
                    map.put("virtualFamilyId", StringUtil.StringDeal(n.getVirtualFamilyId()));
                    map.put("virtualFamilyType", StringUtil.StringDeal(n.getVirtualFamilyType()));
                    map.put("keyMan", StringUtil.StringDeal(n.getKeyMan()));

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
                        nodesJoiner.add("MERGE (n{encryptVirtualFamilyId:node.encryptVirtualFamilyId,virtualFamilyId:node.virtualFamilyId,virtualFamilyType:node.virtualFamilyType,keyMan:node.keyMan})");
                        nodesJoiner.add("set n+=node,n:" + labelJoiner);
                        Statement statement = new Statement(nodesJoiner.toString(), parameter);

                        StatementResult result =null;
                        try {
                            result=this.session.run(statement, TransactionConfig.empty());
                        }catch(Exception e){
                            int i=0;
                            while(i < 3){
                                try {
                                    result=this.session.run(statement, TransactionConfig.empty());
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


            naturalPersonList.clear();
        }

    }

    public void bacthInsertLittleFamilyNode(Map<String, Integer> param) {
        // 自然人源数据提取
        List<LittleFamilyEntity> naturalPersonList = queryNaturalPersonsDao.QueryLittleFamilyNode(param);
        if (ObjectUtil.isNotEmpty(naturalPersonList)) {
            logger.info("thread [" + threadNum + "] need  synchronize [" + naturalPersonList.size() + "] LittleFamily node to neo4j");
            Map<String, List<LittleFamilyEntity>> nodeByLabel = new HashMap();

            for (int i = 0; i < naturalPersonList.size(); i++) {
                LittleFamilyEntity naturalPersonEntity = new LittleFamilyEntity();
                naturalPersonEntity = naturalPersonList.get(i);
                // naturalNodeMap.put(naturalPersonEntity.getOneidPrdInstId(),naturalPersonEntity);
                GroupUtil.group(nodeByLabel,  "LittleFamily").add(naturalPersonEntity);
            }//组装标签

            logger.info("nodeByLabel count=：" + nodeByLabel.size());

            Set<String> labelSet = nodeByLabel.keySet();
            Iterator var15 = labelSet.iterator();

            while (var15.hasNext()) {
                String label = (String) var15.next();
                StringJoiner labelJoiner = new StringJoiner(":");
                if (ObjectUtil.isEmpty(label)) {
                    labelJoiner.add("LittleFamily");
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
                List<LittleFamilyEntity> ns = (List) nodeByLabel.get(label);
                Iterator var18 = ns.iterator();

                while (var18.hasNext()) {//组装数据
                    LittleFamilyEntity n = (LittleFamilyEntity) var18.next();
                    Map<String, Object> map = new LinkedHashMap();
                    map.put("familyId", StringUtil.StringDeal(n.getFamilyId()));
                    map.put("householderLatnId", StringUtil.StringDeal(n.getHouseholderLatnId()));
                    map.put("householderPrdInstId", StringUtil.StringDeal(n.getHouseholderPrdInstId()));
                    map.put("householderServiceNbr", StringUtil.StringDeal(n.getHouseholderServiceNbr()));
                    map.put("householderCustId", StringUtil.StringDeal(n.getHouseholderCustId()));
                    map.put("householderAcctId", StringUtil.StringDeal(n.getHouseholderAcctId()));
                    map.put("prodOfferInstId", StringUtil.StringDeal(n.getProdOfferInstId()));
                    map.put("keyMan", StringUtil.StringDeal(n.getKeyMan()));

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
                        nodesJoiner.add("MERGE (n{familyId:node.familyId,householderLatnId:node.householderLatnId,householderPrdInstId:node.householderPrdInstId,householderServiceNbr:node.householderServiceNbr,householderCustId:node.householderCustId,householderAcctId:node.householderAcctId,prodOfferInstId:node.prodOfferInstId,keyMan:node.keyMan})");
                        nodesJoiner.add("set n+=node,n:" + labelJoiner);
                        Statement statement = new Statement(nodesJoiner.toString(), parameter);


                        StatementResult result=null;
                        try {
                            result=this.session.run(statement, TransactionConfig.empty());
                        }catch(Exception e){
                            int i=0;
                            while(i < 3){
                                try {
                                    result=this.session.run(statement, TransactionConfig.empty());
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


            naturalPersonList.clear();
        }

    }

    public void bacthInsertFamilyAndLittleFamilyRel(Map<String, Integer> param) {
        // 用户、自然人关系源数据提取
        List<LittleFamilyRelEntity> userRelList = queryNaturalPersonsDao.QueryLittleFamilyAndFamilyRel(param);
        if (ObjectUtil.isNotEmpty(userRelList)) {
            logger.info("thread [" + threadNum + "] need  synchronize [" + userRelList.size() + "] FamilyAndLittleFamilyRel Rel to neo4j");

            Map<String, List<LittleFamilyRelEntity>> relationByLabel = new HashMap();
            for (int i = 0; i < userRelList.size(); i++) {
                LittleFamilyRelEntity userRelEntity = userRelList.get(i);
                GroupUtil.group(relationByLabel, "").add(userRelEntity);
            }

            Set<String> labelSet = relationByLabel.keySet();
            Iterator var15 = labelSet.iterator();

            while (var15.hasNext()) {
                String label = (String) var15.next();
                StringJoiner labelJoiner = new StringJoiner(":");
                if (ObjectUtil.isEmpty(label)) {
                    labelJoiner.add("FamilyRel");
                } else {
                    labelJoiner.add(StringUtil.neo4jLabel(label.split(",")[0]));
                }

                List<LittleFamilyRelEntity> tempnaturalPersonRelList = (List) relationByLabel.get(label);
                List<Map<String, Object>> relations = new ArrayList();
                Iterator var8 = tempnaturalPersonRelList.iterator();

                while (var8.hasNext()) {
                    LittleFamilyRelEntity relation = (LittleFamilyRelEntity) var8.next();
                    Map<String, Object> map = new LinkedHashMap();

                    map.put("encryptVirtualFamilyId", StringUtil.StringDeal(relation.getEncryptVirtualFamilyId()));
                    map.put("virtualFamilyId", StringUtil.StringDeal(relation.getVirtualFamilyId()));
                    map.put("familyId", StringUtil.StringDeal(relation.getFamilyId()));

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
                    relationJoiner.add("MATCH (cust:Family),(cc:LittleFamily) WHERE cust.virtualFamilyId = relation.virtualFamilyId AND cc.familyId= relation.familyId");
                    relationJoiner.add("MERGE (cc)-[t:FamilyRel{encryptVirtualFamilyId:relation.encryptVirtualFamilyId,virtualFamilyId:relation.virtualFamilyId,familyId:relation.familyId}]->(cust)");//用户指向自然人  归属自然人
                    Statement statement = new Statement(relationJoiner.toString(), parameter);

                    StatementResult result=null;
                    try {
                        result=this.session.run(statement, TransactionConfig.empty());
                    }catch(Exception e){
                        int i=0;
                        while(i < 3){
                            try {
                                result=this.session.run(statement, TransactionConfig.empty());
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

            userRelList.clear();
        }
    }

    public void bacthInsertLittleFamilyAndNaturalRel(Map<String, Integer> param) {
        // 用户、自然人关系源数据提取
        List<LittleFamilyRelEntity> userRelList = queryNaturalPersonsDao.QueryLittleFamilyAndNaturalRel(param);
        if (ObjectUtil.isNotEmpty(userRelList)) {
            logger.info("thread [" + threadNum + "] need  synchronize [" + userRelList.size() + "] LittleFamilyAndNaturalRel to neo4j");

            Map<String, List<LittleFamilyRelEntity>> relationByLabel = new HashMap();
            for (int i = 0; i < userRelList.size(); i++) {
                LittleFamilyRelEntity userRelEntity = userRelList.get(i);
                GroupUtil.group(relationByLabel,  "LittleFamilyRel").add(userRelEntity);
            }

            Set<String> labelSet = relationByLabel.keySet();
            Iterator var15 = labelSet.iterator();

            while (var15.hasNext()) {
                String label = (String) var15.next();
                StringJoiner labelJoiner = new StringJoiner(":");
                if (ObjectUtil.isEmpty(label)) {
                    labelJoiner.add("LittleFamilyRel");
                } else {
                    labelJoiner.add(StringUtil.neo4jLabel(label.split(",")[0]));
                }

                List<LittleFamilyRelEntity> tempnaturalPersonRelList = (List) relationByLabel.get(label);
                List<Map<String, Object>> relations = new ArrayList();
                Iterator var8 = tempnaturalPersonRelList.iterator();

                while (var8.hasNext()) {
                    LittleFamilyRelEntity relation = (LittleFamilyRelEntity) var8.next();
                    Map<String, Object> map = new LinkedHashMap();

                    map.put("encryptVirtualFamilyId", StringUtil.StringDeal(relation.getEncryptVirtualFamilyId()));
                    map.put("virtualFamilyId", StringUtil.StringDeal(relation.getVirtualFamilyId()));
                    map.put("familyId", StringUtil.StringDeal(relation.getFamilyId()));
                    map.put("encryptOneidPrdInstId", StringUtil.StringDeal(relation.getEncryptOneidPrdInstId()));
                    map.put("oneidPrdInstId", StringUtil.StringDeal(relation.getOneidPrdInstId()));
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
                    relationJoiner.add("MATCH (cust:LittleFamily),(cc:NaturalPerson) WHERE cust.familyId = relation.familyId AND cc.oneidPrdInstId= relation.oneidPrdInstId");
                    relationJoiner.add("MERGE (cc)-[t:LittleFamilyRel{familyId:relation.familyId,encryptOneidPrdInstId:relation.encryptOneidPrdInstId,oneidPrdInstId:relation.oneidPrdInstId}]->(cust)");//用户指向自然人  归属自然人
                    Statement statement = new Statement(relationJoiner.toString(), parameter);

                    StatementResult result=null;
                    try {
                        result=this.session.run(statement, TransactionConfig.empty());
                    }catch(Exception e){
                        int i=0;
                        while(i < 3){
                            try {
                                result=this.session.run(statement, TransactionConfig.empty());
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
        userRelList.clear();
    }

}
