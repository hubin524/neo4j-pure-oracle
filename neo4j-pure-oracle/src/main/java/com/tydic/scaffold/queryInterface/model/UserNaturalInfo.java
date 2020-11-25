package com.tydic.scaffold.queryInterface.model;

import com.tydic.scaffold.batchInsertNeo4j.model.InternetAccountEntity;
import com.tydic.scaffold.batchInsertNeo4j.model.UserEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: userNaturalInfo
 * @Description
 * @Author: hubin
 * @Date: 2020-08-20
 * @Version:v1.0
 */
@Data
public class UserNaturalInfo implements Serializable {
    private List<Map<String, Object>> userEntity;
    private InternetAccountEntity internetAccountEntity;

}
