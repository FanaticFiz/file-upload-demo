package ru.fiz.fileuploaddemo.service;

import org.jetbrains.annotations.NotNull;
import ru.fiz.fileuploaddemo.dto.UploadFileProgress;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Future;

public interface IDownloadService {

    Future<Long> download(@NotNull URL downloadUrl, @NotNull Path path);

    List<UploadFileProgress> getProgress();

    void stopAll();

}
