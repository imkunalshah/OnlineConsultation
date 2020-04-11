package com.kunal.onlineconsultation.Model;

public class DoctorCategoryModel {
    String image,category,name;

    public DoctorCategoryModel() {
    }

    public DoctorCategoryModel(String image, String category, String name) {
        this.image = image;
        this.category = category;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
