package com.holland.filesystem.controller;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@RequestMapping("/upload")
public class UploadController {
    @Resource
    private GridFsOperations operations;

    @PostMapping("/android/web")
    public Mono<ResponseEntity<?>> androidWeb(MultipartFile file) throws IOException {
        final ObjectId objectId = operations.store(file.getInputStream(), "androidWeb");
        return Mono.defer(() -> Mono.just(objectId))
                .map(it -> ResponseEntity.ok().body(it));
    }
}
