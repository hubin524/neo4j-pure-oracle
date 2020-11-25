package com.tydic.neo4j;

import com.tydic.framework.util.ObjectUtil;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: Neo4jSessionManager
 * @Description
 * @Author: hubin
 * @Date: 2020-08-17
 * @Version:v1.0
 */
@Component
public class Neo4jSessionManager {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${fixThrea.fixThreadNum:}")
    private int fixThreadNum;

    @Value("${neo4j.uri:}")
    private  String uri = "bolt://127.0.0.1:7687";

    @Value("${neo4j.userName:}")
    private  String userName = "neo4j";

    @Value("${neo4j.password:}")
    private  String password = "neo4j";

    private Driver driver;

    @Value("${neo4j.maxConnectionPoolSize:}")
    private  String maxConnectionPoolSize = "100";

    @Value("${neo4j.connectionTimeoutMillis:}")
    private  String connectionTimeoutMillis = "10000";

    public static Map<String, Neo4jDatasource> neo4jDatasourceMap = new ConcurrentHashMap<String, Neo4jDatasource>();

    public  Session getSession() {
        Session session=null;
        try {
            Neo4jDatasource neo4jDatasource = null;
            if ( neo4jDatasourceMap.size() == 0) {
                logger.info("begin init neo4j param");
                Map<String, Object> config = new HashMap<String, Object>();
                config.put("uri", uri);
                config.put("userName", userName);
                config.put("password", password);
                config.put("connectionTimeoutMillis", connectionTimeoutMillis);
                config.put("maxConnectionPoolSize", maxConnectionPoolSize);
                logger.info("end init neo4j param=" + config);

                neo4jDatasource = DataSourceFactoryUtil.buildNeo4jDatasource(config);
                neo4jDatasourceMap.put("neo4j", neo4jDatasource);
                if (neo4jDatasource == null) {
                    logger.error("DataSourceFactoryUtil  buildNeo4jDatasource fail");
                }

            } else {
                logger.info("begin get neo4j neo4jDatasource");
                neo4jDatasource = neo4jDatasourceMap.get("neo4j");
                if (neo4jDatasource == null) {
                    logger.error("get neo4jDatasource is null");
                }
            }

             session = Neo4jConnectionUtil.session(neo4jDatasource);
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e+e.getMessage());
        }
        return session;
    }

    public void closeSession(Session session){
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}
