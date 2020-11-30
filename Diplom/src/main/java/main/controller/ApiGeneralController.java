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
import main.api.response.tags.TagsResponse;
import main.model.*;
import main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;

@RestController
public class ApiGeneralController {

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostRepository postRepository;

    private static final int SIZE = 5242880;
    private static final String FORMAT_JPG = "jpg";
    private static final String FORMAT_PNG = "png";

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UserRepository userRepository;

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

        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            return new ResponseEntity(settingsService.putGlobalSettings(settingsRequest), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/api/tag")
    public ResponseEntity<TagsResponse> tags() {
        return new ResponseEntity(tagsService.getAllTags(), HttpStatus.OK);
    }

    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProfileWithPhoto(@ModelAttribute ProfileRequestWithPhoto profile) throws IOException {

        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            int q = userLoginService.getIdentifierMap().get(identifier);
            Optional<User> optionalUser = userRepository.findById(q);
            return new ResponseEntity(profileService.updateUserProfileWithPhoto(profile, optionalUser), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

    }

    @PostMapping(value = "/api/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateMyProfile(@RequestBody ProfileRequest profile) {

        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            int q = userLoginService.getIdentifierMap().get(identifier);
            Optional<User> optionalUser = userRepository.findById(q);
            return new ResponseEntity(profileService.updateUserProfile(profile, optionalUser), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

    }

    @PostMapping("/api/image")
    public ResponseEntity image(@ModelAttribute ImageRequest imageP) throws IOException {

        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            String format = imageP.getImage().getOriginalFilename()
                    .replaceAll("(.+)(\\.)(\\w{3})$", "$3");
            if (format.equals(FORMAT_JPG) || format.equals(FORMAT_PNG)) {
                if (imageP.getImage().getSize() > SIZE) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(imageService.getImageError());
                }
                return new ResponseEntity(imageService.writeImage(imageP.getImage()), HttpStatus.OK);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(imageService.getFormatError());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/api/moderation")
    public ResponseEntity<ModerationResponse> moderation(@RequestBody ModerationRequest request) {
        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            int q = userLoginService.getIdentifierMap().get(identifier);
            Optional<User> optionalUser = userRepository.findById(q);
            return new ResponseEntity(moderationService.getMod(
                    request.getPostId(),
                    request.getDecision(),
                    optionalUser), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

    }

    @PostMapping("/api/comment")
    public ResponseEntity<CommentResponse> comment(@RequestBody CommentRequest commentRequest) {
        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            int q = userLoginService.getIdentifierMap().get(identifier);
            Optional<User> optionalUser = userRepository.findById(q);
            return commentService.postComment(commentRequest, optionalUser);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity<StatisticsResponse> allStat() {
        return allStatisticsService.getStatistics(userLoginService.getIdentifierMap());
    }

    @GetMapping("/api/statistics/my")
    public ResponseEntity<StatisticsResponse> userStat() {

        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            int q = userLoginService.getIdentifierMap().get(identifier);
            Optional<User> optionalUser = userRepository.findById(q);
            return new ResponseEntity(allStatisticsService.getUserStat(optionalUser), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

    }

    @GetMapping("/api/calendar")
    public ResponseEntity<CalendarResponse> calendar(@RequestParam int year) {
        if (year == 0) {
            Calendar calendar = Calendar.getInstance();
            return new ResponseEntity<>(calendarService.getCalendar(calendar.get(1)), HttpStatus.OK);
        }
        return new ResponseEntity<>(calendarService.getCalendar(year), HttpStatus.OK);
    }

}
