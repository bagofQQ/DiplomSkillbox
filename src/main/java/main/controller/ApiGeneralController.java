package main.controller;

import main.api.request.SettingsRequest;
import main.api.request.comment.CommentRequest;
import main.api.request.image.ImageRequest;
import main.api.request.moderation.ModerationRequest;
import main.api.request.profile.ProfileRequest;
import main.api.request.profile.ProfileRequestWithPhoto;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.StatisticsResponse;
import main.api.response.calendar.CalendarResponse;
import main.api.response.comment.CommentResponse;
import main.api.response.moderation.ModerationResponse;
import main.api.response.profile.ProfileResponse;
import main.api.response.tags.TagsResponse;
import main.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Calendar;

@RestController
public class ApiGeneralController {

    @Value("${blog.constants.size}")
    private int SIZE;
    @Value("${blog.constants.formatJpg}")
    private String FORMAT_JPG;
    @Value("${blog.constants.formatPng}")
    private String FORMAT_PNG;

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagsService tagsService;
    private final ProfileService profileService;
    private final ImageService imageService;
    private final ModerationService moderationService;
    private final UserLoginService userLoginService;
    private final CommentService commentService;
    private final AllStatisticsService allStatisticsService;
    private final CalendarService calendarService;

    public ApiGeneralController(InitResponse initResponse,
                                SettingsService settingsService,
                                TagsService tagsService,
                                ProfileService profileService,
                                ImageService imageService,
                                ModerationService moderationService,
                                UserLoginService userLoginService,
                                CommentService commentService,
                                AllStatisticsService allStatisticsService,
                                CalendarService calendarService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.tagsService = tagsService;
        this.profileService = profileService;
        this.imageService = imageService;
        this.moderationService = moderationService;
        this.userLoginService = userLoginService;
        this.commentService = commentService;
        this.allStatisticsService = allStatisticsService;
        this.calendarService = calendarService;
    }

    @GetMapping("/api/init")
    private InitResponse init() {
        return initResponse;
    }

    @GetMapping("/api/settings")
    private SettingsResponse getSettings() {
        return settingsService.getGlobalSettings();
    }

    @PutMapping("/api/settings")
    public ResponseEntity<SettingsResponse> putSettings(@RequestBody SettingsRequest settingsRequest) {
        if(userLoginService.idUserAuthorized()){
            return new ResponseEntity(settingsService.putGlobalSettings(settingsRequest), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/api/tag")
    public ResponseEntity<TagsResponse> tags() {
        return new ResponseEntity(tagsService.getAllTags(), HttpStatus.OK);
    }

    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> updateProfileWithPhoto(@ModelAttribute ProfileRequestWithPhoto profile) throws IOException {
        if(userLoginService.idUserAuthorized()){
            return new ResponseEntity(profileService.updateUserProfileWithPhoto(profile), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping(value = "/api/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> updateMyProfile(@RequestBody ProfileRequest profile) {
        if(userLoginService.idUserAuthorized()){
            return new ResponseEntity(profileService.updateUserProfile(profile), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }


    @PostMapping("/api/image")
    public ResponseEntity<?> image(@ModelAttribute ImageRequest imageP) throws IOException {
        if (!userLoginService.idUserAuthorized()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String format = imageService.parseDimension(imageP.getImage().getOriginalFilename());
        if (imageP.getImage().getSize() > SIZE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(imageService.getImageError());
        }

        if (!(format.equals(FORMAT_JPG) || format.equals(FORMAT_PNG))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(imageService.getFormatError());
        }

        return new ResponseEntity(imageService.writeImage(imageP.getImage()), HttpStatus.OK);
    }

    @PostMapping("/api/moderation")
    public ResponseEntity<ModerationResponse> moderation(@RequestBody ModerationRequest request) {
        if(userLoginService.idUserAuthorized()){
            return new ResponseEntity(moderationService.getMod(
                    request.getPostId(),
                    request.getDecision()), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/api/comment")
    public ResponseEntity<CommentResponse> comment(@RequestBody CommentRequest commentRequest) {
        if(userLoginService.idUserAuthorized()){
            return commentService.postComment(commentRequest);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity<StatisticsResponse> allStat() {
        return allStatisticsService.getStatistics();
    }

    @GetMapping("/api/statistics/my")
    public ResponseEntity<StatisticsResponse> userStat() {
        if(userLoginService.idUserAuthorized()){
            return new ResponseEntity(allStatisticsService.getUserStat(), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

    }

    @GetMapping("/api/calendar")
    public ResponseEntity<CalendarResponse> calendar(@RequestParam(defaultValue = "0") int year) {
        if (year == 0) {
            return new ResponseEntity<CalendarResponse>(calendarService.getCalendar(Calendar.getInstance().get(1)), HttpStatus.OK);
        }
        return new ResponseEntity<CalendarResponse>(calendarService.getCalendar(year), HttpStatus.OK);
    }

}
