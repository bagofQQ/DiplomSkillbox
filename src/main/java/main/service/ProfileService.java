package main.service;

import main.PostsException;
import main.api.request.profile.ProfileRequest;
import main.api.request.profile.ProfileRequestWithPhoto;
import main.api.response.profile.ErrorsProfileResponse;
import main.api.response.profile.ProfileResponse;
import main.model.User;
import main.model.UserRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Random;

@Service
public class ProfileService {

    @Value("${blog.constants.imageWidth}")
    private int IMAGE_WIDTH;
    @Value("${blog.constants.imageHeight}")
    private int IMAGE_HEIGHT;

    @Value("${blog.constants.checkName}")
    private String CHECK_NAME;
    @Value("${blog.constants.folderInputImage2}")
    private String FOLDER_INPUT_IMAGE;
    @Value("${blog.constants.errorEmail}")
    private String ERROR_EMAIL;
    @Value("${blog.constants.errorPhoto}")
    private String ERROR_PHOTO;
    @Value("${blog.constants.errorName}")
    private String ERROR_NAME;
    @Value("${blog.constants.errorPassword}")
    private String ERROR_PASSWORD;
    @Value("${blog.constants.size}")
    private int SIZE;

    private final UserRepository userRepository;

    @Autowired
    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProfileResponse updateUserProfileWithPhoto(ProfileRequestWithPhoto profile, int idUser) throws IOException {
        ProfileResponse profileResponse = new ProfileResponse();
        User user = userRepository.findById(idUser).orElseThrow(PostsException::new);

        HashMap<String, String> errors = checkProfileWithPhotoErrors(profile.getEmail(), profile.getName(), profile.getPassword(), profile.getPhoto(), user);
        if (!errors.isEmpty()) {
            return setErrors(errors);
        }
        user.setPhoto(writeImage(profile.getPhoto()));
        setUser(profile.getEmail(), profile.getName(), profile.getPassword(), user);
        profileResponse.setResult(true);
        return profileResponse;
    }

    public ProfileResponse updateUserProfile(ProfileRequest profile, int idUser) {
        ProfileResponse profileResponse = new ProfileResponse();
        User user = userRepository.findById(idUser).orElseThrow(PostsException::new);

        HashMap<String, String> errors = checkProfileErrors(profile.getEmail(), profile.getName(), profile.getPassword(), user);
        if (!errors.isEmpty()) {
            return setErrors(errors);
        }

        if (profile.getRemovePhoto() == 1) {
            user.setPhoto(null);
        }
        setUser(profile.getEmail(), profile.getName(), profile.getPassword(), user);
        profileResponse.setResult(true);
        return profileResponse;
    }

    private HashMap<String, String> checkProfileErrors(String email, String name, String password, User user) {
        HashMap<String, String> errors = new HashMap<>();
        if (email != null && !email.equals(user.getEmail()) && userRepository.countEmail(email) > 0) {
            errors.put("email", ERROR_EMAIL);
        }
        if (name != null && !name.equals(user.getName()) && !name.matches(CHECK_NAME)) {
            errors.put("name", ERROR_NAME);
        }
        if (password != null && password.length() < 6) {
            errors.put("password", ERROR_PASSWORD);
        }
        return errors;
    }

    private HashMap<String, String> checkProfileWithPhotoErrors(String email, String name, String password, MultipartFile imageFile, User user) {
        HashMap<String, String> errors = checkProfileErrors(email, name, password, user);
        if (imageFile.getSize() > SIZE) {
            errors.put("photo", ERROR_PHOTO);
        }
        return errors;
    }

    private ProfileResponse setErrors(HashMap<String, String> errors) {
        ProfileResponse profileResponse = new ProfileResponse();
        ErrorsProfileResponse errorsProfileResponse = new ErrorsProfileResponse();
        errorsProfileResponse.setEmail(errors.get("email"));
        errorsProfileResponse.setName(errors.get("name"));
        errorsProfileResponse.setPassword(errors.get("password"));
        errorsProfileResponse.setPhoto(errors.get("photo"));
        profileResponse.setErrors(errorsProfileResponse);
        profileResponse.setResult(false);
        return profileResponse;
    }

    private void setUser(String email, String name, String password, User user) {
        if (email != null) {
            user.setEmail(email);
        }
        if (name != null) {
            user.setName(name);
        }
        if (password != null) {
            user.setPassword(password);
        }
        userRepository.save(user);
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
