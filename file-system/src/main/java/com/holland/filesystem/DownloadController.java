package com.holland.filesystem;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/download")
public class DownloadController {

    @Resource
    private HttpServletResponse response;

    @Resource
    private GridFsOperations operations;

    @GetMapping("/android/web")
    public void androidWeb() throws IOException {
        GridFSFile gridFSFile = operations.find(Query.query(Criteria
                .where("filename").is("androidWeb")
        )).sort(new Document("uploadDate", -1)).limit(1)
                .first();

        if (null == gridFSFile) {
            return;
        }

        GridFsResource resource = operations.getResource(gridFSFile);
        InputStream inputStream = resource.getInputStream();

        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename=dist.zip");
        response.addHeader("Content-Length", Long.toString(gridFSFile.getLength()));
        response.getOutputStream().write(inputStream.readAllBytes());
        response.getOutputStream().flush();
    }
}
