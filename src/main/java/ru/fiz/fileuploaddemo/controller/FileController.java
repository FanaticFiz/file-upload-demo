package ru.fiz.fileuploaddemo.controller;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fiz.fileuploaddemo.dto.UploadFileResponse;
import ru.fiz.fileuploaddemo.service.IDownloadService;
import ru.fiz.fileuploaddemo.service.IStorageService;
import ru.fiz.fileuploaddemo.service.exceptions.FileNotFoundException;
import ru.fiz.fileuploaddemo.service.exceptions.WrongFileUrlException;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@RestController
public class FileController {

    private static Logger log = LoggerFactory.getLogger(FileController.class);

    private final IStorageService storageService;

    private final IDownloadService downloadService;

    @Autowired
    public FileController(IStorageService storageService, IDownloadService downloadService) {
        this.storageService = storageService;
        this.downloadService = downloadService;
    }

    @ApiIgnore
    @RequestMapping(value = {"/", ""})
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

    @PostMapping("/upload")
    public UploadFileResponse uploadByLink(@RequestParam("link") URL link) {
        log.debug("Request to upload file by link: {}", link);

        return storageService.store(link);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> download(@PathVariable String fileName, HttpServletRequest request) {
        log.debug("Request to download file: {}", fileName);

        Resource resource = storageService.load(fileName);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(determinateContentType(request, resource)))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listOfFiles() {
        log.debug("Request to list of files");

        List<String> result = storageService.list();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @GetMapping("/stop")
    public ResponseEntity stopLoading() {
        log.debug("Request to stop loading");

        return ResponseEntity.ok("");
    }

    @GetMapping("/progress")
    public ResponseEntity progress() {
        log.debug("Request to progress");

        downloadService.getTaskPool();

        return ResponseEntity.ok("");
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(FileNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(WrongFileUrlException.class)
    public ResponseEntity<?> handleWrongUrl(WrongFileUrlException e) {
        return ResponseEntity.badRequest().build();
    }

    @NotNull
    private String determinateContentType(@NotNull HttpServletRequest request, @NotNull Resource resource) {
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            log.warn("Not determine content type");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return contentType;
    }
}
