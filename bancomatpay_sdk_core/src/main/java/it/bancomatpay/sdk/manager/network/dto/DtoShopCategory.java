package it.bancomatpay.sdk.manager.network.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DtoShopCategory implements Serializable {
    private String title;
    private String description;
    private String imageName;
    private String uuid;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
