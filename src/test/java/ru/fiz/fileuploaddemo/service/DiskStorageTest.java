package ru.fiz.fileuploaddemo.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.StringUtils;
import ru.fiz.fileuploaddemo.dto.UploadFileResponse;
import ru.fiz.fileuploaddemo.properties.StorageProperties;
import ru.fiz.fileuploaddemo.service.exceptions.FileExecutionException;
import ru.fiz.fileuploaddemo.service.exceptions.FileNotFoundException;
import ru.fiz.fileuploaddemo.service.exceptions.WrongFileUrlException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class DiskStorageTest {

    private StorageProperties properties = new StorageProperties();
    private IDownloadService downloadService = new DownloadService();
    private DiskStorageService storageService;

    @Before
    public void init() {
        properties.setUploadDir("target/uploads/" + Math.abs(new Random().nextLong()));
        storageService = new DiskStorageService(properties, downloadService);
    }

    @Test
    public void directoryShouldBeEmpty() {
        assertEquals(0, storageService.list().size());
    }

    @Test(expected = FileNotFoundException.class)
    public void loadNotExistedFile() {
        storageService.load("someFileName");
    }

    @Test
    public void shouldStoreFile() throws MalformedURLException {
        String spec = "https://github.com/FanaticFiz/file-upload-demo/blob/master/pom.xml";
        URL url = new URL(spec);

        UploadFileResponse uploadFileResponse = storageService.store(url);

        String expectedFileName = Integer.toString(StringUtils.getFilename(url.getFile()).hashCode());
        assertEquals(expectedFileName, uploadFileResponse.getName());
        assertEquals("http://localhost/files/download/" + expectedFileName, uploadFileResponse.getUri());

        assertTrue(storageService.load(expectedFileName).exists());
    }

    @Test(expected = FileExecutionException.class)
    public void shouldThrowFileExecutionException() throws MalformedURLException {
        storageService.store(new URL("https://fiz.ru/notExist.file"));
    }

    @Test(expected = WrongFileUrlException.class)
    public void shouldThrowWrongFileUrlExecutionException() throws MalformedURLException {
        storageService.store(new URL("https://file"));
    }

}
