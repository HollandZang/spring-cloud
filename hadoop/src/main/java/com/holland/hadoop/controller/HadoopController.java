package com.holland.hadoop.controller;

import com.holland.common.utils.Response;
import com.holland.hadoop.conf.HadoopConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.InputStream;

@RestController
@RequestMapping("core")
public class HadoopController {
    @Resource
    private HadoopConfig hadoopConfig;

    @PostMapping("/createDirectory")
    public <T> Response<T> createDirectory(String dir) {
        try {
            hadoopConfig.createDirectory(dir);
            return Response.success();
        } catch (Exception e) {
            return Response.failed(e);
        }
    }

    @PostMapping("/writeFile")
    public <T> Response<T> writeFile(String path, String fileName, InputStream inputStream) {
        try {
            hadoopConfig.writeFile(path, fileName, inputStream);
            return Response.success();
        } catch (Exception e) {
            return Response.failed(e);
        }
    }

    @PostMapping("/downloadFile")
    public Response<byte[]> downloadFile(String path, String fileName) {
        try {
            final byte[] bytes = hadoopConfig.downloadFile(path, fileName);
            return Response.success(bytes);
        } catch (Exception e) {
            return Response.failed(e);
        }
    }
}
