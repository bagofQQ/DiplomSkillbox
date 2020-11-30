package main.api.response.restore;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailResponse {

    @JsonProperty("email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
