package main.service;

import main.api.response.profile.ErrorsProfileResponse;
import main.api.request.profile.ProfileRequest;
import main.api.request.profile.ProfileRequestWithPhoto;
import main.api.response.profile.ProfileResponse;
import main.model.User;
import main.model.UserRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

@Service
public class ProfileService {


    private final int IMAGE_WIDTH = 36;
    private final int IMAGE_HEIGHT = 36;

    private static final String CHECK_NAME = "([а-яА-Я]*)";
    private static final String FOLDER_INPUT_IMAGE = "avatars";
    private static final String ERROR_EMAIL = "Этот e-mail уже зарегистрирован";
    private static final String ERROR_PHOTO = "Фото слишком большое, нужно не более 5 Мб";
    private static final String ERROR_NAME = "Имя указано неверно";
    private static final String ERROR_PASSWORD = "Пароль короче 6-ти символов";
    private static final int SIZE = 5242880;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession httpSession;

    public ProfileResponse updateUserProfileWithPhoto(ProfileRequestWithPhoto profile, int idUser) throws IOException {
        ProfileResponse profileResponse = new ProfileResponse();
        ErrorsProfileResponse errorsProfileResponse = new ErrorsProfileResponse();

        Optional<User> optionalUser = userRepository.findById(idUser);

        HashMap<String, String> errors = checkProfileWithPhotoErrors(profile, optionalUser);
        if (!errors.isEmpty()) {
            errorsProfileResponse.setEmail(errors.get("email"));
            errorsProfileResponse.setName(errors.get("name"));
            errorsProfileResponse.setPassword(errors.get("password"));
            errorsProfileResponse.setPhoto(errors.get("photo"));
            profileResponse.setErrors(errorsProfileResponse);
            profileResponse.setResult(false);
            return profileResponse;
        }
        String email = profile.getEmail();
        String name = profile.getName();
        String password = profile.getPassword();

        optionalUser.get().setPhoto(writeImage(profile.getPhoto()));

        if (email != null) {
            optionalUser.get().setEmail(email);
        }
        if (name != null) {
            optionalUser.get().setName(name);
        }
        if (password != null) {
            optionalUser.get().setPassword(password);
        }
        userRepository.save(optionalUser.get());
        profileResponse.setResult(true);
        return profileResponse;
    }


    public ProfileResponse updateUserProfile(ProfileRequest profile, int idUser) {
        ProfileResponse profileResponse = new ProfileResponse();
        ErrorsProfileResponse errorsProfileResponse = new ErrorsProfileResponse();

        Optional<User> optionalUser = userRepository.findById(idUser);

        HashMap<String, String> errors = checkProfileErrors(profile, optionalUser);
        if (!errors.isEmpty()) {
            errorsProfileResponse.setEmail(errors.get("email"));
            errorsProfileResponse.setName(errors.get("name"));
            errorsProfileResponse.setPassword(errors.get("password"));
            errorsProfileResponse.setPhoto(errors.get("photo"));
            profileResponse.setErrors(errorsProfileResponse);
            profileResponse.setResult(false);
            return profileResponse;
        }

        String email = profile.getEmail();
        String name = profile.getName();
        String password = profile.getPassword();

        if (profile.getRemovePhoto() == 1) {
            optionalUser.get().setPhoto(null);
        }
        if (email != null) {
            optionalUser.get().setEmail(email);
        }
        if (name != null) {
            optionalUser.get().setName(name);
        }
        if (password != null) {
            optionalUser.get().setPassword(password);
        }
        userRepository.save(optionalUser.get());
        profileResponse.setResult(true);
        return profileResponse;
    }

    private HashMap<String, String> checkProfileErrors(ProfileRequest profile, Optional<User> optionalUser) {
        HashMap<String, String> errors = new HashMap<>();
        if (profile.getEmail() != null) {
            if (!checkEmail(profile.getEmail(), optionalUser)) {
                errors.put("email", ERROR_EMAIL);
            }
        }
        if (profile.getName() != null) {
            if (!profile.getName().matches(CHECK_NAME)) {
                errors.put("name", ERROR_NAME);
            }
        }
        if (profile.getPassword() != null) {
            if (profile.getPassword().length() < 6) {
                errors.put("password", ERROR_PASSWORD);
            }
        }

        return errors;
    }

    private HashMap<String, String> checkProfileWithPhotoErrors(ProfileRequestWithPhoto profile, Optional<User> optionalUser) {
        HashMap<String, String> errors = new HashMap<>();
        if (profile.getEmail() != null) {
            if (!checkEmail(profile.getEmail(), optionalUser)) {
                errors.put("email", ERROR_EMAIL);
            }
        }
        if (profile.getName() != null) {
            if (!profile.getName().matches(CHECK_NAME)) {
                errors.put("name", ERROR_NAME);
            }
        }
        if (profile.getPassword() != null) {
            if (profile.getPassword().length() < 6) {
                errors.put("password", ERROR_PASSWORD);
            }
        }

        if (profile.getPhoto().getSize() > SIZE) {
            errors.put("photo", ERROR_PHOTO);
        }


        return errors;
    }

    private boolean checkEmail(String email, Optional<User> optionalUser) {
        if (email.equals(optionalUser.get().getEmail())) {
            return false;
        } else {
            return true;
        }
    }

    public String writeImage(MultipartFile imageFile) throws IOException {

        BufferedImage newImage = ImageIO.read(imageFile.getInputStream());

        BufferedImage image = Scalr.resize(newImage, IMAGE_WIDTH, IMAGE_HEIGHT);

        Path path = Path.of(getPath() + "/"
                + imageFile.getOriginalFilename());

        File newFile = new File(path.toString());
        ImageIO.write(image, "jpg", newFile);

        return path.toString();
    }

    private String getPath() {

        String folderCode = getGeneratedSecretCode();
        String folder1 = folderCode.substring(0, 2);
        String folder2 = folderCode.substring(2, 4);
        String folder3 = folderCode.substring(4, 6);


        Path path = Path.of(FOLDER_INPUT_IMAGE + "/" + folder1 + "/" + folder2 + "/" + folder3);

        if (Files.exists(path)) {
            getPath();
        }
        new File(path.toString()).mkdirs();
        return path.toString();

    }

    public static String getGeneratedSecretCode() {
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
