package com.tydic.scaffold.common.service.impl;

import com.tydic.core.exceptions.ServiceException;
import com.tydic.scaffold.common.config.SequenceConfig;
import com.tydic.scaffold.common.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;

/**
 * @author jianghs
 * @version 1.0.0
 * @Description 基于redis实现每日序列
 * @createTime 2020-02-24 9:22
 */
@Service
@ConditionalOnProperty(prefix = SequenceConfig.SEQUENCE_CONFIG_PREFIX, name = "enable", havingValue = "true")
public class SequenceServiceImpl implements SequenceService {

    @Autowired
    private SequenceConfig sequenceConfig;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public String dailyNext(String type) {
        SequenceConfig.Sequence sequence = sequenceConfig.getList()
                .stream()
                .filter(item -> item.getType().equalsIgnoreCase(type))
                .findAny()
                .orElseThrow(() -> new ServiceException("未找到序列{" + type + "}的配置"));


        String redisKey = sequence.getCacheKey();
        //需要确保 set和expire在一个事务中
        if(!stringRedisTemplate.hasKey(redisKey)) {
            stringRedisTemplate.execute((RedisConnection connection) -> {
                Long ttl = Duration.between(LocalTime.now(),LocalTime.MAX).getSeconds();
                return connection.set(redisKey.getBytes(), "0".getBytes(), Expiration.seconds(ttl), RedisStringCommands.SetOption.SET_IF_ABSENT);
            });
        }

        //自增1
        Long value = stringRedisTemplate.opsForValue().increment(redisKey, 1);
        return sequence.next(value);
    }
}
