package com.tydic.neo4j;

import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @ClassName: DataSourceFactoryUtil
 * @Description
 * @Author: hubin
 * @Date: 2020-08-13
 * @Version:v1.0
 */

public class DataSourceFactoryUtil {
    public DataSourceFactoryUtil() {
    }

    public static Neo4jDatasource buildNeo4jDatasource(Map<String, Object> config) throws Exception {
        Neo4jDatasource neo4jDatasource = null;
        if (config == null) {
            return null;
        } else {
            neo4jDatasource = new Neo4jDatasource();
            neo4jDatasource.setUri(MapUtils.getString(config, "uri", "bolt://127.0.0.1:7687"));
            neo4jDatasource.setUserName(MapUtils.getString(config, "userName", "neo4j"));
            neo4jDatasource.setPassword(MapUtils.getString(config, "password", "neo4j"));
            neo4jDatasource.setConnectionTimeoutMillis( MapUtils.getIntValue(config, "connectionTimeoutMillis", 10000));
            neo4jDatasource.setMaxConnectionPoolSize(MapUtils.getIntValue(config, "maxConnectionPoolSize", 100));

            if (neo4jDatasource != null) {
                try {
                    neo4jDatasource.init();
                    pushCloseObject2Map(config, neo4jDatasource);
                } catch (Exception var4) {
                    var4.printStackTrace();
                    neo4jDatasource.close();
                    throw  var4;
                }
            }

            return neo4jDatasource;
        }
    }

    public static void pushCloseObject2Map(Map<String, Object> config, Object object) {
        if (config != null) {
            Stack<Object> stack = (Stack)config.get("callbackCloseStack");
            if (!config.containsKey("callbackCloseStack")) {
                stack = new Stack();
                config.put("callbackCloseStack", stack);
            }

            stack.push(object);
        }

    }

}
