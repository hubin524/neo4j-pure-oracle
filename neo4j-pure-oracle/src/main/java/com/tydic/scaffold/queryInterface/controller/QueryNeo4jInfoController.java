package com.tydic.scaffold.queryInterface.controller;

import com.tydic.core.rational.Return;
import com.tydic.scaffold.queryInterface.model.QueryEnterpriseEntity;
import com.tydic.scaffold.queryInterface.model.QueryNaturalEntity;
import com.tydic.scaffold.queryInterface.model.QueryTwoNaturalEntity;
import com.tydic.scaffold.queryInterface.service.*;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: QueryNeo4jInfoController
 * @Description 接口查询
 * @Author: hubin
 * @Date: 2020-08-19
 * @Version:v1.0
 */
@RestController
@RequestMapping("/queryNeo4jInfo")
public class QueryNeo4jInfoController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    QueryEnterpriseService queryEnterpriseService;

    @Autowired
    QueryFamilyService queryFamilyService;

    @Autowired
    QueryNaturalEnterpriseService queryNaturalEnterpriseService;

    @Autowired
    QueryNaturalRelService queryNaturalRelService;

    @Autowired
    QueryNaturalService queryNaturalService;

    @ApiOperation(value = "自然人信息查询", tags = {"自然人信息查询接口"})
    @PostMapping("queryNaturalInfo")
    public Return queryNaturalInfo(@RequestBody QueryNaturalEntity queryNaturalEntity ) {
        try {
            Map<String, Object> param=new HashMap<String, Object>();
            param.put("numberType", queryNaturalEntity.getNumberType());
            param.put("serviceNbr", queryNaturalEntity.getServiceNbr());
            param.put("latnId", queryNaturalEntity.getLatnId());

            logger.info("param=  "+param);

            return queryNaturalService.queryNaturalService(param);
        } catch (Exception e) {
            logger.error(e+e.getMessage());
            return Return.failure("查询失败:"+e+e.getMessage());
        }
    }

    @ApiOperation(value = "家庭信息查询", tags = {"家庭信息查询接口"})
    @PostMapping("queryFamilyInfo")
    public Return queryFamilyInfo(@RequestBody QueryNaturalEntity queryNaturalEntity) {
        try {
            Map<String, Object> param=new HashMap<String, Object>();
            param.put("numberType", queryNaturalEntity.getNumberType());
            param.put("serviceNbr", queryNaturalEntity.getServiceNbr());
            param.put("latnId", queryNaturalEntity.getLatnId());
            logger.info("param=  "+param);

            return queryFamilyService.queryFamilyService(param);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Return.failure("查询失败:"+e.getMessage());
        }
    }

    @ApiOperation(value = "企业信息查询", tags = {"企业信息查询接口"})
    @PostMapping("queryEnterpriseInfo")
    public Return queryEnterpriseInfo(@RequestBody QueryEnterpriseEntity queryEnterpriseEntity) {
        try {
            Map<String, Object> param=new HashMap<String, Object>();
            param.put("clientName", queryEnterpriseEntity.getClientName());
            logger.info("param=  "+param);

            return queryEnterpriseService.queryEnterpriseService(param);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Return.failure("查询失败:"+e.getMessage());
        }
    }

    @ApiOperation(value = "自然人所属企业信息查询", tags = {"自然人所属企业信息查询接口"})
    @PostMapping("queryNaturalEnterpriseInfo")
    public Return queryNaturalEnterpriseInfo(@RequestBody QueryNaturalEntity queryNaturalEntity) {
        try {
            Map<String, Object> param=new HashMap<String, Object>();
            param.put("numberType", queryNaturalEntity.getNumberType());
            param.put("serviceNbr", queryNaturalEntity.getServiceNbr());
            param.put("latnId", queryNaturalEntity.getLatnId());
            logger.info("param=  "+param);

            return queryNaturalEnterpriseService.queryNaturalEnterpriseService(param);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Return.failure("查询失败:"+e.getMessage());
        }
    }


    @ApiOperation(value = "自然人关系查询", tags = {"自然人关系查询接口"})
    @PostMapping("queryNaturalRelInfo")
    public Return queryNaturalRelInfo(@RequestBody QueryTwoNaturalEntity queryTwoNaturalEntity) {
        try {
            return queryNaturalRelService.queryNaturalRelService(queryTwoNaturalEntity);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Return.failure("查询失败:"+e.getMessage());
        }
    }
}
