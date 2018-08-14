package ru.fiz.fileuploaddemo.service;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.Future;

public class DownloadModel {

    private Path path;

    private Long byteDownloaded = 0L;

    private Long size = -1L;

    private Future<Long> futureDownload;

    public DownloadModel(@NotNull Path path) {
        this.path = path;
    }

    public Long getByteDownloaded() {
        return byteDownloaded;
    }

    public void setByteDownloaded(Long byteDownloaded) {
        this.byteDownloaded = byteDownloaded;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Future<Long> getFutureDownload() {
        return futureDownload;
    }

    public void setFutureDownload(Future<Long> futureDownload) {
        this.futureDownload = futureDownload;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
