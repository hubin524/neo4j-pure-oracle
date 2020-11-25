package com.tydic.framework.config.file;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author jianghuaishuang
 * @desc 附件存储路径----自动化配置类
 * @date 2019/5/30 17:09
 */

@ConfigurationProperties("attachment.store")
@Data
public class AttachmentStorePropertiesAndDirCreator implements InitializingBean {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private String baseDir;

    private String temp;
    private String business;
    private String deleted;


    @Override
    public void afterPropertiesSet() throws Exception {
        /**
         * 初始化文件上传 文件夹
         * linux 服务器，注意目标目录是否拥有权限;
         */
        createDirIfNotExists(this.getBaseDir());
        createDirIfNotExists(this.getTemp());
        createDirIfNotExists(this.getBusiness());
        createDirIfNotExists(this.getDeleted());
    }

    private void createDirIfNotExists(String dir) {
        Path path = Paths.get(dir);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                logger.error("初始化目录失败:" + e.getMessage());
                throw new ExceptionInInitializerError("初始化目录失败【" + e.getMessage() + "】");
            }
        }
    }
}
