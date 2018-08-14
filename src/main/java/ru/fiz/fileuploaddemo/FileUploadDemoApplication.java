package ru.fiz.fileuploaddemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.fiz.fileuploaddemo.properties.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        StorageProperties.class
})
public class FileUploadDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileUploadDemoApplication.class, args);
    }
}
