package main.service;

import main.api.response.image.ErrorsImageResponse;
import main.api.response.image.ImageResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

@Service
public class ImageService {

    private static final String FOLDER_INPUT_IMAGE = "upload";
    private static final String ERROR_IMAGE = "Размер файла превышает допустимый размер";
    private static final String ERROR_FORMAT = "Файл не соответствует формату. Поддерживаемые форматы: jpg и png.";

    public ImageResponse getImageError() {
        ImageResponse imageResponse = new ImageResponse();
        ErrorsImageResponse errorsImageResponse = new ErrorsImageResponse();
        errorsImageResponse.setImage(ERROR_IMAGE);
        imageResponse.setErrors(errorsImageResponse);
        imageResponse.setResult(false);
        return imageResponse;
    }

    public String writeImage(MultipartFile image) throws IOException {
        Path path = Path.of(getPath() + "/"
                + image.getOriginalFilename());
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(image.getBytes());
            os.close();
        }
        return path.toString();
    }

    private String getPath() {

        String folderCode = getGeneratedSecretCode();
        String folder1 = folderCode.substring(0, 2);
        String folder2 = folderCode.substring(2, 4);
        String folder3 = folderCode.substring(4, 6);

//        File file = new File("upload");
//        String abs = file.getAbsolutePath();
//        System.out.println(abs);

        Path path = Path.of(FOLDER_INPUT_IMAGE + "/" + folder1 + "/" + folder2 + "/" + folder3);
        if (Files.exists(path)) {
            getPath();
        }
        new File(path.toString()).mkdirs();
        return path.toString();

    }

    public ImageResponse getFormatError() {
        ImageResponse imageResponse = new ImageResponse();
        ErrorsImageResponse errorsImageResponse = new ErrorsImageResponse();
        errorsImageResponse.setImage(ERROR_FORMAT);
        imageResponse.setErrors(errorsImageResponse);
        imageResponse.setResult(false);
        return imageResponse;
    }

    private static String getGeneratedSecretCode() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 22;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }
}
