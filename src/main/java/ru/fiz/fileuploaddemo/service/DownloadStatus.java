package ru.fiz.fileuploaddemo.service;

import java.util.concurrent.Future;

public class DownloadStatus {

    private String fileName;

    private Long byteDownloaded;

    Future<Long> futureDownload;


}
