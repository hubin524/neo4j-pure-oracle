package com.tydic.neo4j;

/**
 * @ClassName: Neo4jDatasource
 * @Description
 * @Author: hubin
 * @Date: 2020-08-13
 * @Version:v1.0
 */

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import com.tydic.framework.util.StringUtil;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Config.ConfigBuilder;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

public class Neo4jDatasource implements Closeable {
    private String uri = "bolt://localhost:7687";
    private String userName = "neo4j";
    private String password = "neo4j";
    private Driver driver;
    private int maxConnectionPoolSize = 10;
    private int connectionTimeoutMillis = 10000;

    public Neo4jDatasource() {
    }

    public void init() {
        ConfigBuilder build = Config.build();
        build.withMaxConnectionPoolSize(this.maxConnectionPoolSize);
        build.withConnectionTimeout((long)this.connectionTimeoutMillis, TimeUnit.MILLISECONDS);
        this.driver = GraphDatabase.driver(this.uri, AuthTokens.basic(this.userName, this.password), build.toConfig());
     }

    @Override
    public void close() {
        if (this.driver != null) {
            this.driver.close();
        }

    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Driver getDriver() {
        return this.driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public int getMaxConnectionPoolSize() {
        return this.maxConnectionPoolSize;
    }

    public void setMaxConnectionPoolSize(int maxConnectionPoolSize) {
        this.maxConnectionPoolSize = maxConnectionPoolSize;
    }

    public int getConnectionTimeoutMillis() {
        return this.connectionTimeoutMillis;
    }

    public void setConnectionTimeoutMillis(int connectionTimeoutMillis) {
        this.connectionTimeoutMillis = connectionTimeoutMillis;
    }
}
