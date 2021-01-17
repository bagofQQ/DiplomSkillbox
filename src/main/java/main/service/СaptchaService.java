package main.service;

import com.github.cage.GCage;
import main.api.response.СaptchaResponse;
import main.model.CaptchaCodes;
import main.model.CaptchaCodesRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class СaptchaService {


    @Value("${blog.constants.captchaWidth}")
    private int CAPTCHA_WIDTH;
    @Value("${blog.constants.captchaHeight}")
    private int CAPTCHA_HEIGHT;

    private final CaptchaCodesRepository captchaCodesRepository;

    @Autowired
    public СaptchaService(CaptchaCodesRepository captchaCodesRepository) {
        this.captchaCodesRepository = captchaCodesRepository;
    }

    public СaptchaResponse generateCaptcha() throws IOException {

        GCage gcage = new GCage();
        String text = gcage.getTokenGenerator().next();
        Date date = Calendar.getInstance().getTime();

        Iterable<CaptchaCodes> captchaCodesIterable = captchaCodesRepository.findAll();
        if (captchaCodesIterable.iterator().hasNext()) {
            for (CaptchaCodes captcha : captchaCodesIterable) {
                if (date.after(getDatePlusHour(captcha))) {
                    captchaCodesRepository.delete(captcha);
                    return uploadCaptcha(text, date, gcage);
                } else {
                    return uploadCaptcha(text, date, gcage);
                }
            }
        }
        return uploadCaptcha(text, date, gcage);
    }

    private СaptchaResponse uploadCaptcha(String text, Date date, GCage gcage) throws IOException {
        String secretCode = getGeneratedSecretCode();
        setCaptcha(text, secretCode, date);
        return getCaptchaResponse(text, secretCode, gcage);
    }

    private СaptchaResponse getCaptchaResponse(String text, String secretCode, GCage gcage) throws IOException {
        СaptchaResponse сaptchaResponse = new СaptchaResponse();
        сaptchaResponse.setImage("data:image/png;base64," + encodedString(gcage, text));
        сaptchaResponse.setSecret(secretCode);
        return сaptchaResponse;
    }

    private String encodedString(GCage gcage, String text) throws IOException {
        BufferedImage newImage = Scalr.resize(gcage.drawImage(text), CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newImage, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();

        return Base64.getEncoder().encodeToString(imageInByte);
    }

    private Date getDatePlusHour(CaptchaCodes captchaCodes) {
        Date dateCheck = captchaCodes.getTime();
        Calendar calendarPlusHour = new GregorianCalendar();
        calendarPlusHour.setTime(dateCheck);
        calendarPlusHour.add(Calendar.HOUR, 1);
        return calendarPlusHour.getTime();
    }

    private void setCaptcha(String text, String secretCode, Date date) {
        CaptchaCodes captchaCodes = new CaptchaCodes();
        captchaCodes.setCode(text);
        captchaCodes.setSecretCode(secretCode);
        captchaCodes.setTime(date);
        captchaCodesRepository.save(captchaCodes);
    }

    private String getGeneratedSecretCode() {
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
