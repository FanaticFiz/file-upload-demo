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

/**
 * Сервис управления загрузками.
 */
@Service
public class DownloadService implements IDownloadService {

    private static Logger log = LoggerFactory.getLogger(DownloadService.class);

    private static final int BUFFER_SIZE = 8192;

    /**
     * Менеджер управления потоками.
     */
    private ExecutorService taskExecutor = Executors.newCachedThreadPool();

    /**
     * Пул тасок.
     */
    private ConcurrentHashMap<String, DownloadModel> taskPool = new ConcurrentHashMap<>();

    /**
     * Загружает и сохраняет файл по указанным путям.
     *
     * @param downloadUrl URL для загрузки файла.
     * @param path Путь сохранения файла.
     * @return Hfpмер загруженного файла.
     */
    @Override
    public Future<Long> download(@NotNull URL downloadUrl, @NotNull Path path) {
        DownloadModel downloadModel = new DownloadModel(path);

        Future<Long> futureDownload = taskExecutor.submit(downloading(downloadUrl, downloadModel));

        downloadModel.setFutureDownload(futureDownload);
        taskPool.put(path.getFileName().toString(), downloadModel);

        return futureDownload;
    }

    /**
     * Подгатавливает данные для отображения прогресса загрузки по всем таскам.
     *
     * @return Модель отображения загрузки.
     */
    @Override
    public List<UploadFileProgress> getProgress() {
        List<UploadFileProgress> result = new ArrayList<>();

        taskPool.forEach((fileName, downloadModel) -> {
            UploadFileProgress fileProgress = new UploadFileProgress(fileName);
            fileProgress.setSize(downloadModel.getByteDownloaded());

            if (downloadModel.getFutureDownload().isDone()) {
                fileProgress.setSize(downloadModel.getByteDownloaded());
                fileProgress.setFullSize(downloadModel.getByteDownloaded());
                fileProgress.setDone(true);
            } else {
                fileProgress.setSize(downloadModel.getByteDownloaded());
                fileProgress.setFullSize(downloadModel.getSize());
            }

            result.add(fileProgress);
        });

        return result;
    }

    /**
     * Прерывает все незаконченные загрузки.
     * Очищает пул тасок и удаляет недогруженные файлы.
     */
    @Override
    public void stopAll() {
        taskPool.forEach((fileName, downloadModel) -> {
            Future<Long> futureDownload = downloadModel.getFutureDownload();
            if (!futureDownload.isDone()) {
                log.debug("Cancel process for file: {}", fileName);

                futureDownload.cancel(true);

                removeNotLoadedFile(downloadModel);
            }
        });

        taskPool.clear();
    }

    private void removeNotLoadedFile(@NotNull DownloadModel downloadModel) {
        log.debug("Try remove file");
        try {
            Files.deleteIfExists(downloadModel.getPath());
        } catch (IOException e) {
            log.error("Ошибка при попытке удалить файл. {}", e.getLocalizedMessage());
        }
    }

    @NotNull
    private Callable<Long> downloading(@NotNull URL downloadUrl, DownloadModel downloadModel) {
        return () -> {
            log.debug("Try downloading");

            File outputFile = new File(downloadModel.getPath().toUri());
            URLConnection downloadFileConnection = downloadUrl.openConnection();
            downloadModel.setSize(downloadFileConnection.getContentLengthLong());

            long bytesDownloaded = 0;
            try (InputStream is = downloadFileConnection.getInputStream();
                 OutputStream os = new FileOutputStream(outputFile, true)) {

                byte[] buffer = new byte[BUFFER_SIZE];

                int bytesCount;
                while (!Thread.currentThread().isInterrupted() && (bytesCount = is.read(buffer)) > 0) {
                    os.write(buffer, 0, bytesCount);
                    bytesDownloaded += bytesCount;

                    // log.debug("- {}", bytesDownloaded);
                    downloadModel.setByteDownloaded(bytesDownloaded);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getLocalizedMessage());
            }

            return bytesDownloaded;
        };
    }

}
