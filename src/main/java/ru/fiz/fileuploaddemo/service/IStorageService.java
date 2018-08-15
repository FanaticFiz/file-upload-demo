package ru.fiz.fileuploaddemo.service;

import org.springframework.core.io.Resource;
import ru.fiz.fileuploaddemo.dto.UploadFileResponse;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public interface IStorageService {

    UploadFileResponse store(URL url);

    Resource load(String fileName);

    List<String> list();

    String getDownloadUri(String fileName);

    Path getStorageDir();
}
