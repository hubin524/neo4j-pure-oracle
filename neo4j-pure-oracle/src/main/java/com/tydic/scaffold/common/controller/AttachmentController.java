package com.tydic.scaffold.common.controller;

import com.tydic.core.exceptions.ServiceException;
import com.tydic.core.rational.Return;
import com.tydic.core.rational.ReturnCode;
import com.tydic.core.util.collection.Maps;
import com.tydic.framework.config.file.AttachmentStorePropertiesAndDirCreator;
import com.tydic.framework.config.file.proxy.BASE64DecodedMultipartFile;
import com.tydic.framework.config.file.util.FileNameEncodeUtil;
import com.tydic.framework.config.log.aop.anno.LoggableIgnore;
import com.tydic.framework.spring.security.SecurityConstants;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author jianghuaishuang
 * @desc 附件下载组件
 * @date 2019-08-05 16:28
 */
@RestController
@RequestMapping("/common/attachment")
@Validated
@ConditionalOnProperty(prefix = "attachment", name = "enable", havingValue = "true")
public class AttachmentController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AttachmentStorePropertiesAndDirCreator attachmentStoreProperties;

    @ApiOperation(value = "附件上传接口", tags = {"附件attachment"})
    @PostMapping("/upload")
    public Return uploadAttachment(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new Return().setCode(ReturnCode.INTERNAL_ERROR).setMessage("上传失败，未选择文件或文件内容为空").setData(null);
        }
        try {
            //todo 业务处理略....

            //todo 存入磁盘

            storeFile(file);
            return Return.success("上传成功");
        } catch (ServiceException e) {
            return new Return().setCode(ReturnCode.INTERNAL_ERROR).setMessage(e.getMessage()).setData(null);
        }

    }


    @ApiOperation(value = "base64附件上传接口", tags = {"附件attachment"})
    @PostMapping("/base64Upload")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "form", name = "base64", value = "base64编码格式'data:img/jpg;base64,iVBORw0KGgoAAA...'", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "form", name = "fileName", value = "附件名称", dataType = "String", required = true)
    })
    public Return base64Upload(@NotNull @LoggableIgnore("tl;dr") String base64, @NotNull String fileName) {

        try {
            BASE64DecodedMultipartFile file = BASE64DecodedMultipartFile.of(base64, fileName);
            //todo 业务处理略....

            //todo 存入磁盘
            storeFile(file);
            return Return.success("base64上传成功");
        } catch (ServiceException e) {
            return new Return().setCode(ReturnCode.INTERNAL_ERROR).setMessage(e.getMessage()).setData(null);
        }

    }

    /**
     * 存放附件至磁盘,demo.....
     *
     * @param file
     */
    private void storeFile(MultipartFile file) {


        // 获取文件原名
        String fileName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("\\") + 1);
        // 获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).trim();

        // 防止重名，将文件加时间戳后重命名
        String fileNameWithoutSuffix = fileName.substring(0, fileName.lastIndexOf("."));
        String fileLocalName = fileNameWithoutSuffix + "." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "." + suffix;
			 /*
		    将文件写入磁盘
		     */
        Path destPath = Paths.get(attachmentStoreProperties.getTemp(), fileLocalName);
        try {
            file.transferTo(destPath.toFile());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ServiceException("文件写入磁盘失败");
        }

    }


    /**
     * 使用参数形式的jwtToken时,需要配合Parameter2HeaderAuthenticationFilter使用
     *
     * @param attachmentId
     * @param httpResponse
     * @param request
     * @return
     */
    @ApiOperation(value = "附件下载接口", tags = {"附件attachment"})
    @GetMapping("/download/{attachmentId}")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = SecurityConstants.URL_JWT_PARAMETER_AUTHORIZATION_CODE, value = "URL传递jwtToken(与Headeer传递二选一)", dataType = "String"),
            @ApiImplicitParam(paramType = "header", name = SecurityConstants.JWT_HEADER_AUTHORIZATION_CODE, value = "Header传递jwtToken(与URL传递二选一)", required = false, dataType = "String")
    })
    @LoggableIgnore()
    public ResponseEntity downLoad(@NotNull @ApiParam(value = "附件id", required = true) @PathVariable String attachmentId, HttpServletResponse httpResponse, HttpServletRequest request) {
        //todo 根据附件id，查找附件信息

        //todo 根据附件信息获取： 附件名称， 附件路径
        String attachmentName = "java修炼技能树.20190805165704.JPG";
        Path path = Paths.get(attachmentStoreProperties.getTemp(), attachmentName);
        if (Files.exists(path)) {
            try {
                //为了解决中文名称乱码问题
                String fileName = FileNameEncodeUtil.encode(attachmentName, request);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", fileName);
                ResponseEntity<byte[]> byteArr = null;
                try {
                    byteArr = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(path.toFile()), headers, HttpStatus.OK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return byteArr;

            } catch (IOException e) {
                logger.error("附件下载失败:" + e.getMessage());
                return new ResponseEntity(Maps.of("message", "附件下载失败【" + e.getMessage() + "】").of("status", "500"), HttpStatus.OK);
            }
        }

        return new ResponseEntity(Maps.of("message", "附件下载失败【" + path + "不存在】").of("status", "500"), HttpStatus.OK);
    }

}
