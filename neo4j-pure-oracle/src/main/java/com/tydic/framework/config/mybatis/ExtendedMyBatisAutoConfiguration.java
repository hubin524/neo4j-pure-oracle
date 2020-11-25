package com.tydic.framework.config.mybatis;

import com.tydic.framework.config.mybatis.interceptor.ClobConvertInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.ClobTypeHandler;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.sql.Clob;
import java.util.List;

@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class ExtendedMyBatisAutoConfiguration {

	private List<SqlSessionFactory> sqlSessionFactoryList;

	public ExtendedMyBatisAutoConfiguration(List<SqlSessionFactory> sqlSessionFactoryList) {
		this.sqlSessionFactoryList = sqlSessionFactoryList;
	}

	@PostConstruct
	public void addPageInterceptor() {
		Interceptor interceptor = new ClobConvertInterceptor();
		for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
			sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
			sqlSessionFactory.getConfiguration().getTypeHandlerRegistry().register(Clob.class, ClobTypeHandler.class);
		}
	}
}
