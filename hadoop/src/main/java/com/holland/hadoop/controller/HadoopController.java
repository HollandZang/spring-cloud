package com.holland.hadoop.controller;

import com.holland.common.utils.Response;
import com.holland.hadoop.conf.HadoopConfig;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.InputStream;

@RestController
@RequestMapping("core")
public class HadoopController {
    @Resource
    private HadoopConfig hadoop;

    @PostMapping("/directory/{dir}")
    public <T> Response<T> createDirectory(@PathVariable String dir) {
        hadoop.createDirectory(dir);
        return Response.success();
    }

    @PostMapping("/file/{path}/{fileName}")
    public <T> Response<T> writeFile(@PathVariable String path, @PathVariable String fileName, InputStream inputStream) {
        hadoop.writeFile(path, fileName, inputStream);
        return Response.success();
    }

    @GetMapping("/file/{path}/{fileName}")
    public Response<byte[]> downloadFile(@PathVariable String path, @PathVariable String fileName) {
        final byte[] bytes = hadoop.downloadFile(path, fileName);
        return Response.success(bytes);
    }
}
