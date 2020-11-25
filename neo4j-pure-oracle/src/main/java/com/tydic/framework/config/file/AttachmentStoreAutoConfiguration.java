package com.tydic.framework.config.file;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author jianghuaishuang
 * @version 1.0.0
 * @Description 是否开启
 * @createTime 2019-10-23 15:34
 */
@Configuration
@ConditionalOnProperty(prefix = "attachment" ,name = "enable",havingValue = "true")
@EnableConfigurationProperties({AttachmentStorePropertiesAndDirCreator.class})
public class AttachmentStoreAutoConfiguration {
}
