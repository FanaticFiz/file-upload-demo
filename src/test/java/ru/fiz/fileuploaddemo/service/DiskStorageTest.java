package ru.fiz.fileuploaddemo.service;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import ru.fiz.fileuploaddemo.properties.StorageProperties;
import ru.fiz.fileuploaddemo.service.exceptions.FileNotFoundException;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DiskStorageTest {
    private StorageProperties properties = new StorageProperties();
    private IStorageService storageService;

    @Before
    public void init() {
        properties.setUploadDir("target/files/" + Math.abs(new Random().nextLong()));
//        storageService = new DiskStorageService(properties, );
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
    public void storeEndLoad() {
//        String fileName = "foo.txt";
//        MockMultipartFile file = new MockMultipartFile("foo", fileName,
//                MediaType.TEXT_PLAIN_VALUE, "Test".getBytes());
//
//        assertEquals("foo.txt", storageService.store(file));
//        assertTrue(storageService.load(fileName).exists());
    }
}
