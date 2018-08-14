package ru.fiz.fileuploaddemo.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.fiz.fileuploaddemo.dto.UploadFileResponse;
import ru.fiz.fileuploaddemo.properties.StorageProperties;
import ru.fiz.fileuploaddemo.service.exceptions.FileExecutionException;
import ru.fiz.fileuploaddemo.service.exceptions.FileNotFoundException;
import ru.fiz.fileuploaddemo.service.exceptions.WrongFileUrlException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Сервис
 */
@Service
public class DiskStorageService implements IStorageService {

    private static Logger log = LoggerFactory.getLogger(DiskStorageService.class);

    private static final String DOWNLOAD_LINK = "/files/download/";

    private final Path fileStorageDir;

    private final IDownloadService downloadService;

    /**
     * Конструктор.
     *
     * @param storageProperties Настройки хранилища.
     */
    @Autowired
    public DiskStorageService(@NotNull StorageProperties storageProperties, IDownloadService downloadService) {
        this.downloadService = downloadService;
        this.fileStorageDir = Paths.get(storageProperties.getUploadDir())
                .toAbsolutePath()
                .normalize();

        log.info("Try create directory: {}", this.fileStorageDir.toString());
        try {
            Files.createDirectories(this.fileStorageDir);

            log.debug("Successfully created");
        } catch (Exception e) {
            log.error("Cant create the directory for files.", e.getLocalizedMessage());

            throw new RuntimeException("Cant create the directory for files.", e);
        }
    }

    @Override
    public UploadFileResponse store(URL url) {
        if (StringUtils.getFilenameExtension(url.getFile()) == null) {
            throw new WrongFileUrlException("Incorrect file URL: " + url.toString());
        }

        log.debug("Try store file");

        String fileName = getFilename(url);
        Path path = this.fileStorageDir.resolve(fileName);
        long fileSize = 0L;

        try {
            fileSize = downloadService.download(url, path).get();

            log.debug("Success store");
        } catch (InterruptedException e) {
            log.warn("Interrupted, closing");
        } catch (ExecutionException e) {
            log.error("Ошибка обработки файла", e);

            throw new FileExecutionException(e.getLocalizedMessage());
        }

        return new UploadFileResponse(fileName, getDownloadUri(fileName), fileSize);
    }

    /**
     * Загрузка файла.
     *
     * @param fileName Имя файла.
     * @return Файл как ресурс.
     */
    public Resource load(String fileName) {
        try {
            Path filePath = this.fileStorageDir.resolve(fileName).normalize();
            log.debug("Try load file: {}", filePath);

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                log.debug("Success loaded");

                return resource;
            } else {
                log.error("File not found");
                throw new FileNotFoundException("File not found: " + fileName);
            }
        } catch (MalformedURLException e) {
            log.error("File not found");
            throw new FileNotFoundException("File not found " + fileName, e);
        }
    }

    /**
     * Список всех файлов каталога.
     *
     * @return Список полных путей к файлам.
     */
    public List<String> list() {
        try {
            return Files.walk(this.fileStorageDir, 1)
                    .filter(path -> !path.equals(this.fileStorageDir))
                    .map(path -> path.toFile().getName())
                    .map(this::getDownloadUri)
                    .collect(Collectors.toList());
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }
    }

    /**
     * Сформировать ссылку для скачивания по заданному имени файла.
     *
     * @param fileName Имя файла.
     * @return Ссылка для скачивания.
     */
    public String getDownloadUri(String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(DOWNLOAD_LINK)
                .path(fileName)
                .toUriString();
    }

    @NotNull
    private String getFilename(@NotNull URL url) {
        return String.valueOf(StringUtils.getFilename(url.getFile()).hashCode());
    }

}
