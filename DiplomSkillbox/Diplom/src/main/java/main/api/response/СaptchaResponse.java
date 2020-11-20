package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class СaptchaResponse {

    @JsonProperty("secret")
    private String secret;
    @JsonProperty("image")
    private String image;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
