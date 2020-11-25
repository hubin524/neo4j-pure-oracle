package com.tydic.neo4j;

import org.neo4j.driver.v1.Session;

public class Neo4jConnectionUtil {
    public Neo4jConnectionUtil() {
    }

    public static void releaseSession(Session session) {
        if (session != null && session.isOpen()) {
            session.close();
        }

    }

    public static Session session(Neo4jDatasource neo4jDatasource) {
        Session res = null;
        if (neo4jDatasource != null && neo4jDatasource.getDriver() != null) {
            res = neo4jDatasource.getDriver().session();
        }

        return res;
    }
}