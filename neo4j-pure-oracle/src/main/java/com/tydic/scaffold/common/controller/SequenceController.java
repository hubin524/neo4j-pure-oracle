package com.tydic.scaffold.common.controller;

import com.tydic.core.rational.Return;
import com.tydic.scaffold.common.config.SequenceConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jianghs
 * @version 1.0.0
 * @Description TODO
 * @createTime 2020-02-24 9:17
 */
@RestController
@RequestMapping("/common/seq")
@ConditionalOnProperty(prefix = SequenceConfig.SEQUENCE_CONFIG_PREFIX, name = "enable", havingValue = "true")
public class SequenceController {

    @ApiOperation(value = "每日序列", tags = {"序列管理"})
    @GetMapping("/daily/{type}")
    public Return dailyNext(@PathVariable("type") String type) {

        return null;
    }
}
