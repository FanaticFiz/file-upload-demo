package ru.fiz.fileuploaddemo;

import net.minidev.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import ru.fiz.fileuploaddemo.controller.FileController;
import ru.fiz.fileuploaddemo.dto.UploadFileProgress;
import ru.fiz.fileuploaddemo.dto.UploadFileResponse;
import ru.fiz.fileuploaddemo.service.DiskStorageService;
import ru.fiz.fileuploaddemo.service.DownloadService;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class FileControllerTests {

    private MockMvc mockMvc;

    @Mock
    private DiskStorageService storageService;

    @Mock
    private DownloadService downloadService;

    @InjectMocks
    private FileController fileController;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    @Test
    public void shouldGetLinksToAllFiles() throws Exception {
        // Prepare
        List<String> fileUriList = new ArrayList<>();
        fileUriList.add("http://localhost:8314/files/download/123");
        fileUriList.add("http://localhost:8314/files/download/124");
        fileUriList.add("http://localhost:8314/files/download/125");

        when(storageService.list()).thenReturn(fileUriList);

        // Act / Assert
        mockMvc.perform(get("/files"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONArray.toJSONString(fileUriList)))
                .andReturn();
    }

    @Test
    public void shouldRedirectToDoc() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
    }

    @Test
    public void shouldDownloadFile() throws Exception {
        File file = ResourceUtils.getFile("classpath:test.txt");
        UrlResource resource = new UrlResource(file.toURI());

        when(storageService.load("foo.txt")).thenReturn(resource);

        mockMvc.perform(get("/files/download/" + "foo.txt"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldUploadFile() throws Exception {
        URL url = new URL("https://github.com/FanaticFiz/file-upload-demo/blob/master/pom.xml");

        when(storageService.store(url)).thenReturn(new UploadFileResponse("name", "", 100L));

        // Act / Assert
        mockMvc.perform(post("/files/upload").param("link", url.toString()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldInvokeStopAll() throws Exception {
        Mockito.doNothing().when(downloadService).stopAll();

        mockMvc.perform(get("/files/stop"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldInvokeProgress() throws Exception {
        UploadFileProgress fileProgress = new UploadFileProgress("fileName");
        fileProgress.setDone(true);
        fileProgress.setSize(100L);
        fileProgress.setFullSize(100L);

        List<UploadFileProgress> progresses = Collections.singletonList(fileProgress);

        when(downloadService.getProgress()).thenReturn(progresses);

        mockMvc.perform(get("/files/progress"))
                .andExpect(status().isOk())
                .andExpect(content().json(JSONArray.toJSONString(progresses)))
                .andReturn();
    }

}
