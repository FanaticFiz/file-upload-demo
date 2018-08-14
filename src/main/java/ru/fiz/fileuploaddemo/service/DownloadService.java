package ru.fiz.fileuploaddemo.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.fiz.fileuploaddemo.dto.UploadFileProgress;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class DownloadService implements IDownloadService {

    private static Logger log = LoggerFactory.getLogger(DownloadService.class);

    private static final int BUFFER_SIZE = 2048;

    private ExecutorService taskExecutor = Executors.newCachedThreadPool();

    private ConcurrentHashMap<String, DownloadInfo> taskPool = new ConcurrentHashMap<>();

    @Override
    public Future<Long> download(@NotNull URL downloadUrl, @NotNull Path path) {
        DownloadInfo downloadInfo = new DownloadInfo(path);

        Future<Long> futureDownload = taskExecutor.submit(downloading(downloadUrl, downloadInfo));

        downloadInfo.setFutureDownload(futureDownload);
        taskPool.put(path.getFileName().toString(), downloadInfo);

        return futureDownload;
    }

    @Override
    public List<UploadFileProgress> getProgress() {
        List<UploadFileProgress> result = new ArrayList<>();

        taskPool.forEach((fileName, downloadInfo) -> {
            UploadFileProgress fileProgress = new UploadFileProgress(fileName);
            fileProgress.setSize(downloadInfo.getByteDownloaded());

            if (downloadInfo.getFutureDownload().isDone()) {
                fileProgress.setSize(downloadInfo.getByteDownloaded());
                fileProgress.setFullSize(downloadInfo.getByteDownloaded());
                fileProgress.setDone(true);
            } else {
                fileProgress.setSize(downloadInfo.getByteDownloaded());
                fileProgress.setFullSize(downloadInfo.getSize());
            }

            result.add(fileProgress);
        });

        return result;
    }

    @Override
    public void stopAll() {
        taskPool.forEach((fileName, downloadInfo) -> {
            Future<Long> futureDownload = downloadInfo.getFutureDownload();
            if (!futureDownload.isDone()) {
                log.debug("Cancel process for file: {}", fileName);

                futureDownload.cancel(true);

                removeNotLoadedFile(downloadInfo);
            }
        });

        taskPool.clear();
    }

    private void removeNotLoadedFile(@NotNull DownloadInfo downloadInfo) {
        log.debug("Try remove file");
        try {
            Files.deleteIfExists(downloadInfo.getPath());
        } catch (IOException e) {
            log.error("Ошибка при попытке удалить файл. {}", e.getLocalizedMessage());
        }
    }

    @NotNull
    private Callable<Long> downloading(@NotNull URL downloadUrl, DownloadInfo downloadInfo) {
        return () -> {
            log.debug("Try downloading");

            File outputFile = new File(downloadInfo.getPath().toUri());
            URLConnection downloadFileConnection = downloadUrl.openConnection();
            downloadInfo.setSize(downloadFileConnection.getContentLengthLong());

            long bytesDownloaded = 0;
            try (InputStream is = downloadFileConnection.getInputStream();
                 OutputStream os = new FileOutputStream(outputFile, true)) {

                byte[] buffer = new byte[BUFFER_SIZE];

                int bytesCount;
                while (!Thread.currentThread().isInterrupted() && (bytesCount = is.read(buffer)) > 0) {
                    os.write(buffer, 0, bytesCount);
                    bytesDownloaded += bytesCount;

                    // log.debug("- {}", bytesDownloaded);
                    downloadInfo.setByteDownloaded(bytesDownloaded);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getLocalizedMessage());
            }

            return bytesDownloaded;
        };
    }

}
