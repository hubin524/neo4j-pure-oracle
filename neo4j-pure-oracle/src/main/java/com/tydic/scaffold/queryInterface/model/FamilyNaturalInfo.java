package com.tydic.scaffold.queryInterface.model;

import com.tydic.scaffold.batchInsertNeo4j.model.LittleFamilyEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: FamilyNaturalInfo
 * @Description
 * @Author: hubin
 * @Date: 2020-08-21
 * @Version:v1.0
 */
@Data
public class FamilyNaturalInfo implements Serializable {
    private UserNaturalInfo userNaturalInfo;
    private List<Map<String, Object>> littleFamily;
}
