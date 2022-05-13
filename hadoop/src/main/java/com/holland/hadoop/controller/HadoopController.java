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
    private HadoopConfig hadoop;

    @PostMapping("/createDirectory")
    public <T> Response<T> createDirectory(String dir) {
        hadoop.createDirectory(dir);
        return Response.success();
    }

    @PostMapping("/writeFile")
    public <T> Response<T> writeFile(String path, String fileName, InputStream inputStream) {
        hadoop.writeFile(path, fileName, inputStream);
        return Response.success();
    }

    @PostMapping("/downloadFile")
    public Response<byte[]> downloadFile(String path, String fileName) {
        final byte[] bytes = hadoop.downloadFile(path, fileName);
        return Response.success(bytes);
    }
}
