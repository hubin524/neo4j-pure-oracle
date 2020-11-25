package com.tydic.scaffold.batchInsertNeo4j.controller;

import com.tydic.core.rational.Return;
import com.tydic.framework.util.StringUtil;
import com.tydic.scaffold.batchInsertNeo4j.model.BatchInsertNodeEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.BatchInsertRelationEntity;
import com.tydic.scaffold.batchInsertNeo4j.service.BatchInsertNeo4jService;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: BatchInsertNeo4jController
 * @Description 批量插入
 * @Author: hubin
 * @Date: 2020-08-13
 * @Version:v1.0
 */
@RestController
@RequestMapping("/batchInsertNeo4j")
public class BatchInsertNeo4jController {
    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private BatchInsertNeo4jService batchInsertNeo4jService;

    @ApiOperation(value = "启动批量导入节点数据到NEO4J", tags = {"启动批量导入节点接口"})
    @PostMapping("startBatchInsertNeo4jNode")
    public Return startBatchInsertNeo4jNode(@RequestBody BatchInsertNodeEntity nodeEntity) {
        String nodeName=nodeEntity.getNodeName();
        if(StringUtil.isNull(nodeName)){
            logger.error("入参错误、传入的节点名称为空");
            return Return.failure("启动失败:入参错误、传入的节点名称为空");
        }
        try {
            new Thread(){
                @SneakyThrows
                @Override
                public void run() {
                    batchInsertNeo4jService.batchInsertNeo4jNodeService(nodeEntity);
                }

            }.start();
            return Return.success("启动成功");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Return.failure("启动失败:"+e.getMessage());
        }
    }

    @ApiOperation(value = "启动批量导入关系数据到NEO4J", tags = {"启动批量导入节点间关系接口"})
    @PostMapping("startBatchInsertNeo4jRelation")
    public Return startBatchInsertNeo4jRelation(@RequestBody BatchInsertRelationEntity relationEntity) {
        String nodeName=relationEntity.getRelationName();
        if(StringUtil.isNull(nodeName)){
            logger.error("入参错误、传入的关系名称为空");
            return Return.failure("启动失败:入参错误、传入的关系名称为空");
        }

        try {
            new Thread(){
                @SneakyThrows
                @Override
                public void run() {
                    batchInsertNeo4jService.batchInsertNeo4jRelationService(relationEntity);
                }

            }.start();
            return Return.success("启动成功");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Return.failure("启动失败:"+e.getMessage());
        }
    }
}
