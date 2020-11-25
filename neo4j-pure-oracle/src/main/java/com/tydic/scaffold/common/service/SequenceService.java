package com.tydic.scaffold.common.service;

/**
 * @author jianghs
 * @version 1.0.0
 * @Description TODO
 * @createTime 2020-02-24 9:21
 */
public interface SequenceService {

    /**
     * 获取每日序列
     * @param type
     * @return
     */
    String dailyNext(String type);
}
