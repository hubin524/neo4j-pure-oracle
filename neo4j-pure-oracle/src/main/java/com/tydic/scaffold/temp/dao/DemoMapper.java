package com.tydic.scaffold.temp.dao;

import com.tydic.framework.config.mybatis.dao.BaseDao;
import com.tydic.scaffold.temp.model.Demo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019/5/9 9:35
 */
@Mapper
public interface DemoMapper extends BaseDao<Demo> {

	List<Demo> qryDemo(Demo param);

	@Select("select * from TB_TEMP_DEMO where DEMO_ID= #{demoId} ")
	@Results({
			@Result(column = "DEMO_ID", property = "demoId"),
			@Result(column = "DEMO_NAME", property = "demoName"),
			@Result(column = "AGE", property = "age"),
			@Result(column = "BIRTHDATE", property = "birthdate")
	})
	Demo getDemo(@Param("demoId") Long demoId);

	int updateDemo(Demo demo);
}
