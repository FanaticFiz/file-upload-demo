package ru.fiz.fileuploaddemo;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.fiz.fileuploaddemo.controller.FileController;
import ru.fiz.fileuploaddemo.dto.UploadFileResponse;
import ru.fiz.fileuploaddemo.service.DiskStorageService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class RestControllerTests {

    private MockMvc mockMvc;

    @Mock
    private DiskStorageService storageService;

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
        fileUriList.add("http://localhost:8314/downloadFile/1.png");
        fileUriList.add("http://localhost:8314/downloadFile/2.png");
        fileUriList.add("http://localhost:8314/downloadFile/3.png");

        when(storageService.list()).thenReturn(fileUriList);

        // Act / Assert
        mockMvc.perform(get("/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONArray.toJSONString(fileUriList)))
                .andReturn();
    }

    @Test
    public void shouldNotDownloadNotExistingFile() throws Exception {
        mockMvc.perform(get("/downloadFile/"))
                .andExpect(status().isNotFound())
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
        String fileName = "test.txt";
        String downloadLink = "http://localhost:8314/downloadFile/" + fileName;

        when(storageService.load(fileName)).thenReturn(new UrlResource(downloadLink));

        mockMvc.perform(get("/downloadFile/" + fileName))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldUploadFile() throws Exception {
        // Prepare
//        String fileName = "file";
//        String originFileName = "file.txt";
//        String contentType = "text/plain";
//        String uri = "http://localhost:8314/downloadFile/file.txt";
//        byte[] payload = "Fiz write this test".getBytes();
//
//        MockMultipartFile file = new MockMultipartFile(fileName, originFileName, contentType, payload);
//        UploadFileResponse uploadFileResponse = new UploadFileResponse(originFileName, uri, payload.length);
//
//        when(storageService.store(file)).thenReturn(originFileName);
//        when(storageService.getDownloadUri(originFileName)).thenReturn(uri);
//
//        // Act / Assert
//        mockMvc.perform(multipart("/uploadFile", "file").file(file))
//                .andExpect(status().isOk())
//                .andExpect(content().json(JSONValue.toJSONString(uploadFileResponse)));
    }

}
