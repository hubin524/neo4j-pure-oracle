package com.tydic.framework.util;

import com.tydic.neo4j.Neo4jDatasource;
import com.tydic.scaffold.batchInsertNeo4j.model.NaturalPersonEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: StringUtil
 * @Description
 * @Author: hubin
 * @Date: 2020-08-14
 * @Version:v1.0
 */


public class StringUtil {

    public StringUtil() {
    }

    public static String StringDeal(String oriString){
        if(oriString == null || "NULL".equalsIgnoreCase(oriString) || "".equals(oriString)){
            return "-1";
        }else{
            return oriString;
        }
    }


    public static boolean isNull(String oriString){
        if(oriString == null || "NULL".equalsIgnoreCase(oriString) || "".equals(oriString)){
            return true;
        }else{
            return false;
        }
    }

    public static String neo4jLabel(String label) {
        StringBuilder stringBuilder = new StringBuilder();
        if (label != null) {
            char[] chars = label.toCharArray();
            char[] var3 = chars;
            int var4 = chars.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                char t = var3[var5];
                if ('a' <= t && t <= 'z') {
                    stringBuilder.append(t);
                } else if ('A' <= t && t <= 'Z') {
                    stringBuilder.append(t);
                } else if (t == '_') {
                    stringBuilder.append(t);
                } else if (19968 <= t && t <= '龥') {
                    stringBuilder.append(t);
                }
            }
        }

        return stringBuilder.toString();
    }
}

