package com.holland.filesystem.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.bson.Document;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Api(tags = "文件下载模块")
@Controller
@RequestMapping("/download")
public class DownloadController {

    @Resource
    private GridFsOperations operations;

    @ApiOperation(value = "获取安卓h5页面当前版本信息")
    @GetMapping("/android/web/version")
    public Mono<ResponseEntity<?>> androidWebVersion() {
        GridFSFile gridFile = operations.find(Query.query(Criteria
                .where("filename").is("androidWeb")
        )).sort(new Document("uploadDate", -1)).limit(1)
                .first();
        return Mono.defer(() -> Mono.just(
                Map.of(
                        "objectId", gridFile == null ? "" : gridFile.getObjectId().toString(),
                        "updateTime", gridFile == null ? "" : gridFile.getUploadDate()
                )))
                .map(it -> ResponseEntity.ok().body(it));
    }

    @ApiOperation(value = "下载安卓h5页面当前版本")
    @GetMapping("/android/web")
    public Mono<ResponseEntity<InputStreamResource>> androidWeb(String objectId) throws IOException {
        final GridFSFile gridFile = operations.findOne(Query.query(Criteria.where("_id").is(objectId)));
        if (null == gridFile) {
            return Mono.defer(() -> Mono.just(
                    ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + objectId)
                            .body(new InputStreamResource(InputStream.nullInputStream()))));
        }

        final GridFsResource resource = operations.getResource(gridFile);
        final InputStream inputStream = resource.getInputStream();

        return Mono.defer(() -> Mono.just(inputStream))
                .map(it -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + gridFile.getFilename())
                        .body(new InputStreamResource(it)));
    }

}
