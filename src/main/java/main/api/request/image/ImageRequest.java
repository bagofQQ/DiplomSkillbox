package main.api.request.image;

import org.springframework.web.multipart.MultipartFile;

public class ImageRequest {

    private MultipartFile image;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
