package com.itcast.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * 阿里云oss工具类
 */
@Data
public class OssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    /**
     * 上传图片
     * @param bytes
     * @return
     */
    public String uploadImg(byte[] bytes){
        String fileName = UUID.randomUUID().toString().concat(".png");
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, fileName, new ByteArrayInputStream(bytes));
        ossClient.putObject(putObjectRequest);
        // https://redbook512.oss-cn-beijing.aliyuncs.com/default.png
        return "https://redbook512.oss-cn-beijing.aliyuncs.com/" + fileName;
    }
}
