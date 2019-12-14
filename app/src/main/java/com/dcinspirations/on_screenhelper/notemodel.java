package com.dcinspirations.on_screenhelper;

public class notemodel {
    private int id;
    private String text, time;

    public notemodel(int id, String text, String time) {
        this.id = id;
        this.text = text;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
