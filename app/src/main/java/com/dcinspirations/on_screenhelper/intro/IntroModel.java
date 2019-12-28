package com.dcinspirations.on_screenhelper.intro;

public class IntroModel {
    private int img_id;
    private String description;

    public IntroModel(int img_id, String description) {
        this.img_id = img_id;
        this.description = description;
    }

    public int getImg_id() {
        return img_id;
    }

    public void setImg_id(int img_id) {
        this.img_id = img_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
