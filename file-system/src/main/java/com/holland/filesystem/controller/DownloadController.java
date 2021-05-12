package com.holland.filesystem.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Controller
@RequestMapping("/download")
public class DownloadController {

    @Resource
    private HttpServletResponse response;

    @Resource
    private GridFsOperations operations;

    @GetMapping("/android/web/version")
    public ResponseEntity<?> androidWebVersion() {
        GridFSFile gridFile = operations.find(Query.query(Criteria
                .where("filename").is("androidWeb")
        )).sort(new Document("uploadDate", -1)).limit(1)
                .first();
        return ResponseEntity.ok(Map.of(
                "objectId", gridFile == null ? "" : gridFile.getObjectId().toString(),
                "updateTime", gridFile == null ? "" : gridFile.getUploadDate()
        ));
    }

    @GetMapping("/android/web")
    public void androidWeb(String objectId) throws IOException {
        final GridFSFile gridFile = operations.findOne(Query.query(Criteria.where("_id").is(objectId)));

        if (null == gridFile) {
            return;
        }

        final GridFsResource resource = operations.getResource(gridFile);
        final InputStream inputStream = resource.getInputStream();

        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename=dist.zip");
        response.addHeader("Content-Length", Long.toString(gridFile.getLength()));
        response.getOutputStream().write(inputStream.readAllBytes());
        response.getOutputStream().flush();
    }
}
