package com.tydic.scaffold.batchInsertNeo4j.service.Impl;

import com.tydic.backgroundInsert.BatchInsertNode;
import com.tydic.backgroundInsert.BatchInsertRelation;
import com.tydic.scaffold.batchInsertNeo4j.dao.QueryNaturalPersonsDao;
import com.tydic.scaffold.batchInsertNeo4j.model.BatchInsertNodeEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.BatchInsertRelationEntity;
import com.tydic.scaffold.batchInsertNeo4j.service.BatchInsertNeo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: batchInsertNeo4jServiceImpl
 * @Description
 * @Author: hubin
 * @Date: 2020-08-13
 * @Version:v1.0
 */
@Service
public class BatchInsertNeo4jServiceImpl implements BatchInsertNeo4jService {

    @Autowired
    BatchInsertNode batchInsertNode;

    @Autowired
    BatchInsertRelation batchInsertNodeRelation;

    @Autowired
    private QueryNaturalPersonsDao queryNaturalPersonsDao;

    @Override
    public void batchInsertNeo4jNodeService(BatchInsertNodeEntity nodeEntity) throws Exception {

        batchInsertNode.startThreadNode(queryNaturalPersonsDao,nodeEntity);
    }

    @Override
    public void batchInsertNeo4jRelationService(BatchInsertRelationEntity relationEntity) throws Exception {

        batchInsertNodeRelation.startThreadRelation(queryNaturalPersonsDao,relationEntity);
    }
}
