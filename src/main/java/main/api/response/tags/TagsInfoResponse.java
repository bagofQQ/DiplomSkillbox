package main.api.response.tags;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TagsInfoResponse {

    @JsonProperty("name")
    private String name;
    @JsonProperty("weight")
    private double weight;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
