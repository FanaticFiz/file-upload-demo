package ru.fiz.fileuploaddemo.dto;

public class UploadFileResponse {
    private String name;
    private String uri;
    private long size;

    public UploadFileResponse(String name, String uri, long size) {
        this.name = name;
        this.uri = uri;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
