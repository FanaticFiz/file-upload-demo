package ru.fiz.fileuploaddemo.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;

/**
 * Класс хелпер для загрузки файлов по URL.
 */
public class FileDownload {

    private static final int BUFFER_SIZE = 1024;

    /**
     * Загружает файл по заданному URL и ложит его по указанному пути.
     *
     * @param downloadUrl URL
     * @param path Путь куда сохранить файл.
     * @return Размер загруженного файла.
     * @throws IOException
     */
//    public static long downloadFile(@NotNull URL downloadUrl, @NotNull Path path) throws IOException {
//        File outputFile = new File(path.toUri());
//        URLConnection downloadFileConnection = downloadUrl.openConnection();
//
//        long bytesDownloaded = 0;
//        try (InputStream is = downloadFileConnection.getInputStream();
//             OutputStream os = new FileOutputStream(outputFile, true)) {
//
//            byte[] buffer = new byte[BUFFER_SIZE];
//
//            int bytesCount;
//            while ((bytesCount = is.read(buffer)) > 0) {
//                os.write(buffer, 0, bytesCount);
//                bytesDownloaded += bytesCount;
//            }
//        }
//
//        return bytesDownloaded;
//    }

}