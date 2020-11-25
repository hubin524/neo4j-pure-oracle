package com.tydic.backgroundInsert;

import com.tydic.framework.util.BatchUtil;
import com.tydic.framework.util.GroupUtil;
import com.tydic.framework.util.ObjectUtil;
import com.tydic.framework.util.StringUtil;
import com.tydic.scaffold.batchInsertNeo4j.dao.QueryNaturalPersonsDao;
import com.tydic.scaffold.batchInsertNeo4j.model.NaturalPersonEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.EnterpriseEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.EnterpriseNaturalRelEntity;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.TransactionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @ClassName: BacthInsertEnterpriseNodeAndRelation
 * @Description
 * @Author: hubin
 * @Date: 2020-08-18
 * @Version:v1.0
 */
@Component
@Scope("prototype")//spring 多例
public class BacthInsertEnterpriseNodeAndRelation {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private Session session = null;
    private QueryNaturalPersonsDao queryNaturalPersonsDao;
    private int threadNum;

    public BacthInsertEnterpriseNodeAndRelation(Session session, QueryNaturalPersonsDao queryNaturalPersonsDao, int threadNum) {
        this.session = session;
        this.queryNaturalPersonsDao = queryNaturalPersonsDao;
        this.threadNum=threadNum;
    }

    public void bacthInsertEnterpriseNode(Map<String, Integer> param) {
        // 用户源数据提取
        List<EnterpriseEntity> naturalPersonList = queryNaturalPersonsDao.QueryEnterpriseNode(param);
        if (ObjectUtil.isNotEmpty(naturalPersonList)) {
            logger.info("thread [" + threadNum + "] need  synchronize [" + naturalPersonList.size() + "] Enterprise node to neo4j");
            Map<String, List<EnterpriseEntity>> nodeByLabel = new HashMap();

            for (int i = 0; i < naturalPersonList.size(); i++) {
                EnterpriseEntity userEntity = new EnterpriseEntity();
                userEntity = naturalPersonList.get(i);
                GroupUtil.group(nodeByLabel, "Enterprise").add(userEntity);
            }//组装标签

            logger.info("nodeByLabel count=：" + nodeByLabel.size());

            Set<String> labelSet = nodeByLabel.keySet();
            Iterator var15 = labelSet.iterator();

            while (var15.hasNext()) {
                String label = (String) var15.next();
                StringJoiner labelJoiner = new StringJoiner(":");
                if (ObjectUtil.isEmpty(label)) {
                    labelJoiner.add("Enterprise");
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
                List<EnterpriseEntity> ns = (List) nodeByLabel.get(label);
                Iterator var18 = ns.iterator();

                while (var18.hasNext()) {//组装数据
                    EnterpriseEntity relation = (EnterpriseEntity) var18.next();
                    Map<String, Object> map = new LinkedHashMap();

                    map.put("latnId",     StringUtil.StringDeal(relation.getLatnId()));
                    map.put("clientId",     StringUtil.StringDeal(relation.getClientId()));
                    map.put("clientName",     StringUtil.StringDeal(relation.getClientName()));
                    map.put("orgUscc",     StringUtil.StringDeal(relation.getOrgUscc()));
                    map.put("regno",     StringUtil.StringDeal(relation.getRegno()));
                    map.put("licid",     StringUtil.StringDeal(relation.getLicid()));
                    map.put("statusCd",     StringUtil.StringDeal(relation.getStatusCd()));
                    map.put("foundDate",    StringUtil.StringDeal( relation.getFoundDate()));
                    map.put("legalRepr",    StringUtil.StringDeal( relation.getLegalRepr()));
                    map.put("registerAddr",     StringUtil.StringDeal(relation.getRegisterAddr()));
                    map.put("orgPhone",     StringUtil.StringDeal(relation.getOrgPhone()));
                    map.put("bdLocation",     StringUtil.StringDeal(relation.getBdLocation()));
                    map.put("cityCode",     StringUtil.StringDeal(relation.getCityCode()));
                    map.put("cityName",     StringUtil.StringDeal(relation.getCityName()));
                    map.put("countyCode",     StringUtil.StringDeal(relation.getCountyCode()));
                    map.put("countyName",     StringUtil.StringDeal(relation.getCountyName()));
                    map.put("officeAddr",     StringUtil.StringDeal(relation.getOfficeAddr()));
                    map.put("keyMan",     StringUtil.StringDeal(relation.getKeyMan()));
                   // map.put("serviceNbr",     relation.getServiceNbr());
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
                        nodesJoiner.add("MERGE (n{latnId:node.latnId,clientId:node.clientId,clientName:node.clientName,orgUscc:node.orgUscc,regno:node.regno,licid:node.licid,statusCd:node.statusCd,foundDate:node.foundDate,legalRepr:node.legalRepr,registerAddr:node.registerAddr,orgPhone:node.orgPhone,bdLocation:node.bdLocation,cityCode:node.cityCode,cityName:node.cityName,countyCode:node.countyCode,countyName:node.countyName,officeAddr:node.officeAddr,keyMan:node.keyMan})");
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

    public void bacthInsertEnterpriseRel(Map<String, Integer> param) {
        // 用户、自然人关系源数据提取
        List<EnterpriseNaturalRelEntity> userRelList = queryNaturalPersonsDao.QueryEnterpriseRel(param);
        if (ObjectUtil.isNotEmpty(userRelList)) {
            logger.info("thread [" + threadNum + "] need  synchronize [" + userRelList.size() + "] EnterpriseRel Rel to neo4j");

            Map<String, List<EnterpriseNaturalRelEntity>> relationByLabel = new HashMap();
            for (int i = 0; i < userRelList.size(); i++) {
                EnterpriseNaturalRelEntity userRelEntity = userRelList.get(i);
                GroupUtil.group(relationByLabel,  "EnterpriseRel").add(userRelEntity);
            }

            Set<String> labelSet = relationByLabel.keySet();
            Iterator var15 = labelSet.iterator();

            while (var15.hasNext()) {
                String label = (String) var15.next();
                StringJoiner labelJoiner = new StringJoiner(":");
                if (ObjectUtil.isEmpty(label)) {
                    labelJoiner.add("EnterpriseRel");
                } else {
                    labelJoiner.add(StringUtil.neo4jLabel(label.split(",")[0]));
                }

                List<EnterpriseNaturalRelEntity> tempnaturalPersonRelList = (List) relationByLabel.get(label);
                List<Map<String, Object>> relations = new ArrayList();
                Iterator var8 = tempnaturalPersonRelList.iterator();

                while (var8.hasNext()) {
                    EnterpriseNaturalRelEntity relation = (EnterpriseNaturalRelEntity) var8.next();
                    Map<String, Object> map = new LinkedHashMap();

                    map.put("clientLatnId", StringUtil.StringDeal(relation.getClientLatnId()));
                    map.put("clientId", StringUtil.StringDeal(relation.getClientId()));
                    map.put("clientName", StringUtil.StringDeal(relation.getClientName()));
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
                    relationJoiner.add("MATCH (cust:Enterprise),(cc:NaturalPerson) WHERE cust.clientId = relation.clientId AND cc.oneidPrdInstId= relation.oneidPrdInstId");
                    relationJoiner.add("MERGE (cc)-[t:EnterpriseRel{clientLatnId:relation.clientLatnId,clientId:relation.clientId,clientName:relation.clientName,encryptOneidPrdInstId:relation.encryptOneidPrdInstId,oneidPrdInstId:relation.oneidPrdInstId}]->(cust)");//用户指向自然人  归属自然人
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
