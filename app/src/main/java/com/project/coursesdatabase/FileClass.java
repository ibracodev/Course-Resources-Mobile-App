package com.project.coursesdatabase;

public class FileClass {

    String name;
    String url;

    public FileClass(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public FileClass() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
