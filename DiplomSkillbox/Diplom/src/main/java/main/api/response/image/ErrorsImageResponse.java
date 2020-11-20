package main.api.response.image;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorsImageResponse {

    @JsonProperty("image")
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
