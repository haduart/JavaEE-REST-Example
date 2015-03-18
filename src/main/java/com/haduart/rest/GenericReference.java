package com.haduart.rest;


public class GenericReference {
    private String name;
    private String uri;

    public GenericReference() {}

    public GenericReference(String uri, String name) {
        this.uri = uri;
        this.name = name;
    }

    public String getHref() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public void setHref(String uri) {
        this.uri = uri;
    }

    public void setName(String name) {
        this.name = name;
    }
}
