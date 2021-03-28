package com.holland.filesystem;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Resource
    private GridFsOperations operations;

    @PostMapping("/android/web")
    public ResponseEntity<?> androidWeb(MultipartFile file) throws IOException {
        ObjectId objectId = operations.store(file.getInputStream(), "androidWeb");
        return ResponseEntity.ok(objectId.toString());
    }
}
