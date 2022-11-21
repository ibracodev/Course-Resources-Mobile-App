package com.project.coursesdatabase;


public class FileClass {

    String name;
    String url;
    String username;
    String uploadtime;
    String descc;


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


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(String uploadtime) {
        this.uploadtime = uploadtime;
    }

    public String getDescc() {
        return descc;
    }

    public void setDescc(String descc) {
        this.descc = descc;
    }
}
