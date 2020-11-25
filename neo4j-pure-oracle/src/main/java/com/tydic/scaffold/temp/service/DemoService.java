package com.tydic.scaffold.temp.service;


import com.tydic.scaffold.temp.model.Demo;

import java.util.List;

public interface DemoService {

	List<Demo> qryDemo(Demo param, int pageNum, int pageSize);

	Demo getDemo(Long demoId);

	void save(Demo demo);

	void update(Demo demo);

	void deleteDemo(Long demoId);
}
