package com.tydic.backgroundInsert;

import com.tydic.framework.util.BatchUtil;
import com.tydic.framework.util.GroupUtil;
import com.tydic.framework.util.ObjectUtil;
import com.tydic.framework.util.StringUtil;
import com.tydic.scaffold.batchInsertNeo4j.dao.QueryNaturalPersonsDao;
import com.tydic.scaffold.batchInsertNeo4j.model.NaturalPersonEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.NaturalRelEntity;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.TransactionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @ClassName: BatchInsertNaturalNodeAndRelation
 * @Description
 * @Author: hubin
 * @Date: 2020-08-18
 * @Version:v1.0
 */
@Component
@Scope("prototype")//spring 多例
public class BatchInsertNaturalNodeAndRelation {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private Session session = null;
    private QueryNaturalPersonsDao queryNaturalPersonsDao;
    private int threadNum;

    public BatchInsertNaturalNodeAndRelation(Session session, QueryNaturalPersonsDao queryNaturalPersonsDao, int threadNum) {
        this.session = session;
        this.queryNaturalPersonsDao = queryNaturalPersonsDao;
        this.threadNum=threadNum;
    }

    public void bacthInsertNaturalNode(Map<String, Integer> param) {

        // 自然人源数据提取
        List<NaturalPersonEntity> naturalPersonList = queryNaturalPersonsDao.QueryNaturalPersons(param);
        if (ObjectUtil.isNotEmpty(naturalPersonList)) {
            logger.info("thread [" + threadNum + "] need  synchronize [" + naturalPersonList.size() + "] naturalPerson node to neo4j");
            Map<String, List<NaturalPersonEntity>> nodeByLabel = new HashMap();

            for (int i = 0; i < naturalPersonList.size(); i++) {
                NaturalPersonEntity naturalPersonEntity = new NaturalPersonEntity();
                naturalPersonEntity = naturalPersonList.get(i);
                // naturalNodeMap.put(naturalPersonEntity.getOneidPrdInstId(),naturalPersonEntity);
                GroupUtil.group(nodeByLabel, naturalPersonEntity.getOneidLabels()).add(naturalPersonEntity);
            }//组装标签

            logger.info("nodeByLabel count=：" + nodeByLabel.size());

            Set<String> labelSet = nodeByLabel.keySet();
            Iterator var15 = labelSet.iterator();

            while (var15.hasNext()) {
                String label = (String) var15.next();
                StringJoiner labelJoiner = new StringJoiner(":");
                if (ObjectUtil.isEmpty(label)) {
                    labelJoiner.add("NaturalPerson");
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
                List<NaturalPersonEntity> ns = (List) nodeByLabel.get(label);
                Iterator var18 = ns.iterator();

                while (var18.hasNext()) {//组装数据
                    NaturalPersonEntity n = (NaturalPersonEntity) var18.next();
                    Map<String, Object> map = new LinkedHashMap();
                    map.put("encryptOneidPrdInstId", StringUtil.StringDeal(n.getEncryptOneidPrdInstId()));
                    map.put("oneidPrdInstId", StringUtil.StringDeal(n.getOneidPrdInstId()));
                    map.put("oneidPrdInstName", StringUtil.StringDeal(n.getOneidPrdInstName()));
                    map.put("oneidServiceNbr", StringUtil.StringDeal(n.getOneidServiceNbr()));
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
                        nodesJoiner.add("MERGE (n{oneidPrdInstId:node.oneidPrdInstId,encryptOneidPrdInstId:node.encryptOneidPrdInstId,oneidPrdInstName:node.oneidPrdInstName,oneidServiceNbr:node.oneidServiceNbr})");
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


            naturalPersonList.clear();
        }


    }

    public void bacthInsertNaturalRel(Map<String, Integer> param)  {

        // 自然人关系 源数据提取
        List<NaturalRelEntity> naturalPersonRelList = queryNaturalPersonsDao.QueryNaturalRel(param);
        if (ObjectUtil.isNotEmpty(naturalPersonRelList)) {
            logger.info("thread [" + threadNum + "] need  synchronize [" + naturalPersonRelList.size() + "] naturalPerson Rel to neo4j");

            Map<String, List<NaturalRelEntity>> relationByLabel = new HashMap();

            for (int i = 0; i < naturalPersonRelList.size(); i++) {
                NaturalRelEntity naturalRelEntity = naturalPersonRelList.get(i);
                GroupUtil.group(relationByLabel, naturalRelEntity.getPersonRel()).add(naturalRelEntity);
            }

            Set<String> labelSet = relationByLabel.keySet();
            Iterator var15 = labelSet.iterator();

            while (var15.hasNext()) {
                String label = (String) var15.next();
                StringJoiner labelJoiner = new StringJoiner(":");
                if (ObjectUtil.isEmpty(label)) {
                    labelJoiner.add("PersonRel");
                } else {
                    labelJoiner.add(StringUtil.neo4jLabel(label.split(",")[0]));
                }

                List<NaturalRelEntity> tempnaturalPersonRelList = (List) relationByLabel.get(label);
                List<Map<String, Object>> relations = new ArrayList();
                Iterator var8 = tempnaturalPersonRelList.iterator();

                while (var8.hasNext()) {
                    NaturalRelEntity relation = (NaturalRelEntity) var8.next();
                    Map<String, Object> map = new LinkedHashMap();

                    map.put("aOneidOrdOnstOd", StringUtil.StringDeal(relation.getaOneidPrdInstId()));
                    map.put("zOneidPrdOnstOd", StringUtil.StringDeal(relation.getzOneidPrdInstId()));
                    map.put("personRel", StringUtil.StringDeal(relation.getPersonRel()));

                    relations.add(map);

                }

                List<List<Map<String, Object>>> batches = BatchUtil.batch(relations, 1000);
                Iterator var17 = batches.iterator();

                while (var17.hasNext()) {

                    List<Map<String, Object>> batch = (List) var17.next();
                    logger.info("thread [" + threadNum + "]  batch [" + batch.size() + "] naturalPerson Rel to neo4j");

                    Map<String, Object> parameter = new HashMap();
                    parameter.put("relations", batch);
                    StringJoiner relationJoiner = new StringJoiner(" ");
                    relationJoiner.add("unwind $relations as relation");
                    relationJoiner.add("MATCH (cust:NaturalPerson),(cc:NaturalPerson) WHERE cust.oneidPrdInstId = relation.aOneidOrdOnstOd AND cc.oneidPrdInstId= relation.zOneidPrdOnstOd");
                    relationJoiner.add("MERGE (cust)-[r:person_rel{person_rel:relation.personRel}]-(cc)");

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
