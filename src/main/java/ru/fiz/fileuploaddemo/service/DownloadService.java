package ru.fiz.fileuploaddemo.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.concurrent.*;

@Service
public class DownloadService implements IDownloadService {

    private static Logger log = LoggerFactory.getLogger(DownloadService.class);

    private static final int BUFFER_SIZE = 1024;

    private ExecutorService taskExecutor = Executors.newCachedThreadPool();

    private ConcurrentHashMap<String, Future<Long>> taskPool = new ConcurrentHashMap<>();

    @Override
    public Future<Long> download(@NotNull URL downloadUrl, @NotNull Path path) {

        Future<Long> futureDownload = taskExecutor.submit(() -> {
            log.debug("Try downloading");

            File outputFile = new File(path.toUri());
            URLConnection downloadFileConnection = downloadUrl.openConnection();

            long bytesDownloaded = 0;
            try (InputStream is = downloadFileConnection.getInputStream();
                 OutputStream os = new FileOutputStream(outputFile, true)) {

                byte[] buffer = new byte[BUFFER_SIZE];

                int bytesCount;
                while ((bytesCount = is.read(buffer)) > 0) {
                    os.write(buffer, 0, bytesCount);
                    bytesDownloaded += bytesCount;

                    log.debug("- {}", bytesDownloaded);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getLocalizedMessage());
            }

            return bytesDownloaded;
        });

        taskPool.put(path.toString(), futureDownload);

        return futureDownload;
    }

    @Override
    public ConcurrentHashMap<String, Future<Long>> getTaskPool() {
        return taskPool;
    }

    @Override
    public void stopAll() {
        taskExecutor.shutdown();
    }

    @Override
    public void stop(String fileName) {
        // taskPool.get(fileName);
    }

    @Override
    public Future<Long> getTask(String path) {
        log.debug("get task by name: {}", path);

        return taskPool.get(path);
    }
}
