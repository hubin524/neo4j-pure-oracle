package com.tydic.framework.config.file.proxy;

import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.*;

/**
 * @author jianghuaishuang
 * @desc 手机端拍照上传时, 只能传 base64编码; 在这里特殊处理;
 * data:img/jpg;base64, xxxxxx
 * @date 2019-07-02 14:15
 */
public class BASE64DecodedMultipartFile implements MultipartFile {
	private final byte[] imgContent;
	private final String header;
	private final String fileName;

	private BASE64DecodedMultipartFile(byte[] imgContent, String header, String fileName) {
		this.imgContent = imgContent;
		this.header = header.split(";")[0];
		this.fileName = fileName;
	}

	@Override
	public String getName() {
		return fileName;
	}

	@Override
	public String getOriginalFilename() {
		return fileName;
	}

	@Override
	public String getContentType() {
		return header.split(":")[1];
	}

	@Override
	public boolean isEmpty() {
		return imgContent == null || imgContent.length == 0;
	}

	@Override
	public long getSize() {
		return imgContent.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return imgContent;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(imgContent);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		//try(){} ---自动关闭文件流
		try (FileOutputStream fos = new FileOutputStream(dest);) {
			fos.write(imgContent);
		}
	}


	public static BASE64DecodedMultipartFile of(String base64, String fileName) {
		try {
			String[] baseParts = base64.split(",");
			String header = baseParts[0];
			String content = baseParts[1];

			BASE64Decoder decoder = new BASE64Decoder();
			byte[] b = decoder.decodeBuffer(content);

			return new BASE64DecodedMultipartFile(b, header, fileName);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
