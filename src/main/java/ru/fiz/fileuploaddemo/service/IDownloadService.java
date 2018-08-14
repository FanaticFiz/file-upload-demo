package ru.fiz.fileuploaddemo.service;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public interface IDownloadService {
    Future<Long> download(@NotNull URL downloadUrl, @NotNull Path path);

    ConcurrentHashMap<String, Future<Long>> getTaskPool();

    void stopAll();

    void stop(String fileName);

    Future<Long> getTask(String path);
}
