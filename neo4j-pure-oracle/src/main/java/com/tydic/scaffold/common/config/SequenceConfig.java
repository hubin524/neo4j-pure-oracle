package com.tydic.scaffold.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.tydic.scaffold.common.config.SequenceConfig.SEQUENCE_CONFIG_PREFIX;

/**
 * @author jianghs
 * @version 1.0.0
 * @Description 配置类
 * @createTime 2019-12-25 15:32
 */
@Configuration
@ConfigurationProperties(prefix = SEQUENCE_CONFIG_PREFIX)
@Data
public class SequenceConfig {
    public static final String SEQUENCE_CONFIG_PREFIX = "sequence";
    private static final String CACHE_KEY_SEPARATOR = "::";

    private List<Sequence> list;


    @Data
    public static class Sequence {
        /**
         * 类型，作为redis-key
         */
        private String type;

        /**
         * 业务编号的前缀
         */
        private String prefix;

        /**
         * 时间格式
         */
        private String pattern;


        /**
         * 序列号长度， 不足补0
         */
        private int length;


        ///////////////////////////////////////////////////////////////////////////////////////////////
        public String getCacheKey() {
            return SEQUENCE_CONFIG_PREFIX + CACHE_KEY_SEPARATOR + type;
        }

        private DateTimeFormatter dft ;
        private DateTimeFormatter getDateTimeFormatter(){
            if(this.dft == null) {
                this.dft = DateTimeFormatter.ofPattern(this.pattern);
            }
            return  this.dft;
        }

        public String next(Long seq) {
            String timeStr = LocalDateTime.now().format(getDateTimeFormatter());
            String content = String.format("%0" + this.length + "d", seq);
            StringBuilder sb = new StringBuilder();
            sb.append(this.prefix)
                    .append(timeStr)
                    .append(content);
            return sb.toString();
        }
    }
}
