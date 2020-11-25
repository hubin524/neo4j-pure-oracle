package com.tydic.backgroundInsert;

import com.tydic.neo4j.Neo4jSessionManager;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;

import java.util.*;

/**
 * @ClassName: Test
 * @Description
 * @Author: hubin
 * @Date: 2020-08-25
 * @Version:v1.0
 */

public class Test {
    public static void main(String[] args) {
        Neo4jSessionManager neo=new Neo4jSessionManager();
        Session session= neo.getSession();

        Map<String, Integer> param = new HashMap<String, Integer>();
        // queryShort(session);
    }


}
