package com.holland.hadoop.conf;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class HadoopConfig {

    private final FileSystem hdfs;
    private final String appName;

    public HadoopConfig(@Value("${hadoop.url}") String hdfsPath
            , @Value("${spring.application.name}") String appName) throws IOException, URISyntaxException {
        this.appName = appName;
        this.hdfs = FileSystem.get(new URI(hdfsPath), new Configuration());
    }

    public void createDirectory(String dir) throws Exception {
        final boolean mkdirs = hdfs.mkdirs(new Path(appName + File.separatorChar + dir));
        if (mkdirs) {
        } else {
            throw new Exception("Failed! File has been existed...");
        }
    }

    public void writeFile(String path, String fileName, InputStream inputStream) throws Exception {
        if (StringUtils.isEmpty(fileName)) {
            throw new Exception("Failed! Filename must not be null or empty...");
        }
        if (null == inputStream) {
            throw new Exception("Failed! InputStream must not be null...");
        }

        final Path outfile = new Path(path == null ? appName : appName + File.separatorChar + path, fileName);
        try {
            if (hdfs.exists(outfile)) {
                inputStream.close();
                throw new Exception("Failed! File has been existed...");
            } else {
                try (final FSDataOutputStream outputStream = hdfs.create(outfile)) {
                    IOUtils.copyBytes(inputStream, outputStream, hdfs.getConf(), true);
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            try {
                inputStream.close();
            } catch (IOException ignored) {

            }
            throw new Exception(e);
        }
    }

    public byte[] downloadFile(String path, String fileName) throws Exception {
        if (StringUtils.isEmpty(fileName)) {
            throw new Exception("Failed! Filename must not be null or empty...");
        }

        final Path outfile = new Path(path == null ? appName : appName + File.separatorChar + path);
        try {
            if (hdfs.exists(outfile)) {
                final FSDataInputStream inputStream = hdfs.open(outfile);
                return IOUtils.readFullyToByteArray(inputStream);
            } else {
                throw new Exception("Failed! File not exist...");
            }
        } catch (IOException e) {
            throw new Exception(e);
        }
    }
}
