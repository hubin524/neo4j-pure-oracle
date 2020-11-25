package com.tydic.scaffold.batchInsertNeo4j.service;

import com.tydic.scaffold.batchInsertNeo4j.model.BatchInsertNodeEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.BatchInsertRelationEntity;

public interface BatchInsertNeo4jService {

    void batchInsertNeo4jNodeService(BatchInsertNodeEntity nodeEntity) throws Exception;
    void batchInsertNeo4jRelationService(BatchInsertRelationEntity relationEntity) throws Exception;
}
