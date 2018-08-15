package ru.fiz.fileuploaddemo.service;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class DownloadServiceTest {

    private IDownloadService downloadService = new DownloadService();

    /**
     * Should download - getProgress - stop - getProgress
     * @throws MalformedURLException
     */
    @Test
    public void complexTest() throws MalformedURLException {
        String spec = "https://github.com/FanaticFiz/file-upload-demo/blob/master/pom.xml";

        Path path = Paths.get("resources").toAbsolutePath().subpath(0, 4);

        assertNotNull(downloadService.download(new URL(spec), path.resolve(path + "/test.txt")));
        assertFalse(downloadService.getProgress().isEmpty());

        downloadService.stopAll();

        assertTrue(downloadService.getProgress().isEmpty());
    }

}
