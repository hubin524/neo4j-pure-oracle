package com.tydic.scaffold;

import com.tydic.framework.mybatis.dynamicsql.configuration.setup.EnableSetup;
import com.tydic.framework.mybatis.dynamicsql.configuration.setup.enums.DBEnum;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableSetup(dbType = DBEnum.ORACLE)
@ComponentScan(basePackages = {"com.tydic"})
@MapperScan(basePackages = {"com.tydic"}, annotationClass = Mapper.class)
@EnableAsync
public class ScaffoldApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScaffoldApplication.class, args);
	}

}
