package main.service;

import com.github.cage.GCage;
import main.api.response.СaptchaResponse;
import main.model.CaptchaCodes;
import main.model.CaptchaCodesRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class СaptchaService {

    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;

    private final int CAPTCHA_WIDTH = 110;
    private final int CAPTCHA_HEIGHT = 300;

    public СaptchaResponse getCaptcha() throws IOException {

        СaptchaResponse сaptchaResponse = new СaptchaResponse();
        CaptchaCodes captchaCodes = new CaptchaCodes();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        GCage gcage = new GCage();
        String text = gcage.getTokenGenerator().next();

        Iterable<CaptchaCodes> captchaCodesIterable = captchaCodesRepository.findAll();

        if (captchaCodesIterable.iterator().hasNext()) {
            for (CaptchaCodes captcha : captchaCodesIterable) {
                Optional<CaptchaCodes> optionalCaptchaCodes = captchaCodesRepository.findById(captcha.getId());
                if (date.after(getDatePlusHour(optionalCaptchaCodes))) {
                    captchaCodesRepository.delete(optionalCaptchaCodes.get());
                    String secretCode = getGeneratedSecretCode();
                    if (!secretCode.equals(optionalCaptchaCodes.get().getSecretCode())) {
                        saveCaptchaInfo(captchaCodes,
                                text,
                                secretCode,
                                date);
                        сaptchaResponse.setImage("data:image/png;base64," + getEncodedString(gcage, text));
                        сaptchaResponse.setSecret(secretCode);
                        return сaptchaResponse;
                    } else {
                        String secretCode1 = secretCode + "1";
                        saveCaptchaInfo(captchaCodes,
                                text,
                                secretCode1,
                                date);
                        сaptchaResponse.setImage("data:image/png;base64," + getEncodedString(gcage, text));
                        сaptchaResponse.setSecret(secretCode1);
                        return сaptchaResponse;
                    }
                } else {
                    String secretCode = getGeneratedSecretCode();
                    if (!secretCode.equals(optionalCaptchaCodes.get().getSecretCode())) {
                        saveCaptchaInfo(captchaCodes,
                                text,
                                secretCode,
                                date);
                        сaptchaResponse.setImage("data:image/png;base64," + getEncodedString(gcage, text));
                        сaptchaResponse.setSecret(secretCode);
                        return сaptchaResponse;
                    } else {
                        String secretCode1 = secretCode + "1";
                        saveCaptchaInfo(captchaCodes,
                                text,
                                secretCode1,
                                date);
                        сaptchaResponse.setImage("data:image/png;base64," + getEncodedString(gcage, text));
                        сaptchaResponse.setSecret(secretCode1);
                        return сaptchaResponse;
                    }
                }
            }
        }
        String secretCode = getGeneratedSecretCode();
        saveCaptchaInfo(captchaCodes,
                text,
                secretCode,
                date);
        сaptchaResponse.setImage("data:image/png;base64," + getEncodedString(gcage, text));
        сaptchaResponse.setSecret(secretCode);
        return сaptchaResponse;
    }

    public String getEncodedString(GCage gcage, String text) throws IOException {
        BufferedImage newImage = Scalr.resize(gcage.drawImage(text), CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newImage, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();

        return Base64.getEncoder().encodeToString(imageInByte);
    }

    public Date getDatePlusHour(Optional<CaptchaCodes> optionalCaptchaCodes) {
        Date dateCheck = optionalCaptchaCodes.get().getTime();
        Calendar calendarPlusHour = new GregorianCalendar();
        calendarPlusHour.setTime(dateCheck);
        calendarPlusHour.add(Calendar.HOUR, 1);
        return calendarPlusHour.getTime();
    }

    public void saveCaptchaInfo(CaptchaCodes captchaCodes, String text, String secretCode, Date date) {
        captchaCodes.setCode(text);
        captchaCodes.setSecretCode(secretCode);
        captchaCodes.setTime(date);
        captchaCodesRepository.save(captchaCodes);
    }

    public String getGeneratedSecretCode() {
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
