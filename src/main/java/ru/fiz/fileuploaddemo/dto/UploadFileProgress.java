package ru.fiz.fileuploaddemo.dto;

public class UploadFileProgress {
    private String name;
    private long size;
    private long fullSize;
    private boolean isDone;

    public UploadFileProgress(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public long getFullSize() {
        return fullSize;
    }

    public void setFullSize(long fullSize) {
        this.fullSize = fullSize;
    }
}
