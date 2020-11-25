package com.tydic.framework.config.mybatis.interceptor;

import com.tydic.core.util.Strings;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Clob;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Guang YANG
 */
@Intercepts({
    @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class,
            ResultHandler.class, CacheKey.class, BoundSql.class}
    ),
    @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class ClobConvertInterceptor implements Interceptor {

  @Override
  @SuppressWarnings("unchecked")
  public Object intercept(Invocation invocation) throws Throwable {
    Object record = invocation.proceed();
    // 处理CLOB字段
    if (!(record instanceof List)) {
      return record;
    }
    List recordList = (List) record;
    recordList.stream()
        .filter(recordRow -> recordRow instanceof Map)
        .forEach(recordRow -> this.convertClob((Map) recordRow));
    return recordList;
  }

  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {
    // "Nothing!" - in an India accent voice.
  }

  private void convertClob(Map<Object, Object> map) {
    map.forEach((key, value) -> {
      if (value instanceof Clob) {
        Clob clob = (Clob) value;
        map.put(key, Strings.of(clob));
      }
    });
  }

}
