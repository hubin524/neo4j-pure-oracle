package com.tydic.scaffold.temp.service.impl;

import com.github.pagehelper.PageHelper;
import com.tydic.framework.config.log.aop.anno.Loggable;
import com.tydic.framework.config.log.aop.enums.LoggableTypeEnum;
import com.tydic.scaffold.temp.dao.DemoMapper;
import com.tydic.scaffold.temp.model.Demo;
import com.tydic.scaffold.temp.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019/5/9 9:52
 */
@Profile({"dev", "test"})
@Service
public class DemoServiceImpl implements DemoService {
    private Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    @Autowired
    private DemoMapper demoMapper;

    @Loggable(type = LoggableTypeEnum.BUSINESS, module = "样例demo", features = "查询DemoList")
    @Override
    public List<Demo> qryDemo(Demo param, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //使用mapper.xml
        return demoMapper.qryDemo(param);
    }

    @Loggable(type = LoggableTypeEnum.BUSINESS, module = "样例demo", features = "获取单个Demo")
    @Override
    public Demo getDemo(Long demoId) {
        //使用Mappger.java注解方法
        return demoMapper.getDemo(demoId);

    }

    @Loggable(type = LoggableTypeEnum.BUSINESS, module = "样例demo", features = "保存Demo")
    @Transactional(rollbackFor = {})
    @Override
    public void save(Demo demo) {
        //使用tk.mybatis提供的方法;
        demoMapper.insert(demo);
    }

    @Loggable(type = LoggableTypeEnum.BUSINESS, module = "样例demo", features = "更新Demo")
    @Transactional(rollbackFor = {})
    @Override
    public void update(Demo demo) {
        //使用mapper.xml
        if (demoMapper.updateDemo(demo) <= 0) {
            logger.warn("实际更新数据0条");
        }
        if ((System.currentTimeMillis() & 1) == 0) {
            throw new RuntimeException("主动抛出异常，测试事务");
        }
    }

    @Loggable(type = LoggableTypeEnum.BUSINESS, module = "样例demo", features = "删除Demo")
    @Transactional(rollbackFor = {})
    @Override
    public void deleteDemo(Long demoId) {
        //使用tk.mybatis提供的方法; ---- <T>中必须带有@Id 注解，否则PrimaryKey为全部字段组成的联合主键;
        int result = demoMapper.deleteByPrimaryKey(demoId);
        if (result <= 0) {
            logger.warn("删除数据{}条", result);
        }
    }
}
