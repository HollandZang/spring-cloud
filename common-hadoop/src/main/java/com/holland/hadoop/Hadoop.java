package com.holland.hadoop;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class Hadoop {

    private final FileSystem hdfs;
    private final String appName;

    public Hadoop(String hdfsPath, String appName) throws IOException, URISyntaxException {
        this.appName = appName;
        this.hdfs = FileSystem.get(new URI(hdfsPath), new Configuration());
    }

    /**
     * Need not recursion create every directory
     */
    public void createDirectory(String dir) {
        final boolean mkdirs;
        try {
            mkdirs = hdfs.mkdirs(new Path(appName + File.separatorChar + dir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!mkdirs)
            throw new RuntimeException("Failed! File has been existed...");
    }

    public void writeFile(String path, String fileName, InputStream is) {
        if (null == is)
            throw new RuntimeException("Failed! InputStream must not be null...");
        final Path outfile = getOutfile(path, fileName);

        try {
            if (hdfs.exists(outfile)) {
                appendFile(path, fileName, is);
//                is.close();
//                throw new RuntimeException("Failed! File has been existed...");
            } else {
                final FSDataOutputStream os = hdfs.create(outfile);
                IOUtils.copyBytes(is, os, hdfs.getConf(), true);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendFile(String path, String fileName, InputStream is) {
        if (null == is)
            throw new RuntimeException("Failed! InputStream must not be null...");
        final Path outfile = getOutfile(path, fileName);

        try {
            if (hdfs.exists(outfile)) {
                final FSDataOutputStream os = hdfs.create(outfile);
                IOUtils.copyBytes(is, os, hdfs.getConf(), true);
            } else {
                writeFile(path, fileName, is);
//                is.close();
//                throw new RuntimeException("Failed! File doesn't existed...");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] downloadFile(String path, String fileName) {
        final Path outfile = getOutfile(path, fileName);

        try {
            if (hdfs.exists(outfile)) {
                final FSDataInputStream inputStream = hdfs.open(outfile);
                return IOUtils.readFullyToByteArray(inputStream);
            } else {
                throw new RuntimeException("Failed! File not exist...");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getOutfile(String path, String fileName) {
        if (StringUtils.isEmpty(fileName))
            throw new RuntimeException("Failed! Filename must not be null or empty...");
        return new Path(null == path ? appName : appName + File.separatorChar + path);
    }
}
