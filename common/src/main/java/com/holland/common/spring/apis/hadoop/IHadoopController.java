package com.holland.common.spring.apis.hadoop;

import com.holland.common.utils.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.InputStream;

@FeignClient("hadoop")
public interface IHadoopController {

    @PostMapping("/core/test")
    Response<String> test(String dir);

    @PostMapping("/core/createDirectory")
    <T> Response<T> createDirectory(String dir);

    @PostMapping("/core/writeFile")
    <T> Response<T> writeFile(String path, String fileName, InputStream inputStream);

    @PostMapping("/core/downloadFile")
    Response<byte[]> downloadFile(String path, String fileName);
}
