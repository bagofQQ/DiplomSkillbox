package main.controller;

import main.api.request.addingpost.AddingPostRequest;
import main.api.request.dislike.DislikeRequest;
import main.api.request.like.LikeRequest;
import main.api.response.addingpost.AddingPostResponse;
import main.api.response.dislike.DislikeResponse;
import main.api.response.like.LikeResponse;
import main.api.response.posts.CountPostsResponse;
import main.api.response.singlepost.SinglePostResponse;
import main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class ApiPostController {

    @Autowired
    private HttpSession httpSession;

    private final AddingPostService addingPostService;
    private final UserLoginService userLoginService;
    private final LikeDislikeService likeDislikeService;
    private final PostsService postsService;
    private final SinglePostService singlePostService;
    private final EditingPostService editingPostService;


    public ApiPostController(AddingPostService addingPostService,
                             UserLoginService userLoginService,
                             LikeDislikeService likeDislikeService,
                             PostsService postsService,
                             SinglePostService singlePostService,
                             EditingPostService editingPostService) {
        this.addingPostService = addingPostService;
        this.userLoginService = userLoginService;
        this.likeDislikeService = likeDislikeService;
        this.postsService = postsService;
        this.singlePostService = singlePostService;
        this.editingPostService = editingPostService;
    }

    @GetMapping("/api/post")
    public ResponseEntity<CountPostsResponse> post(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String mode) {
        return new ResponseEntity(postsService.getPosts(offset, limit, mode), HttpStatus.OK);
    }

    @PostMapping("/api/post")
    public ResponseEntity<AddingPostResponse> addPost(@RequestBody AddingPostRequest addingPostRequest) {

        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            int idUser = userLoginService.getIdentifierMap().get(identifier);
            return addingPostService.add(addingPostRequest, idUser);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/api/post/moderation")
    public ResponseEntity<CountPostsResponse> moderation(@RequestParam int offset, @RequestParam int limit, @RequestParam String status) {

        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            int idModer = userLoginService.getIdentifierMap().get(identifier);
            return new ResponseEntity(postsService.getModerationPosts(idModer, offset, limit, status), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/api/post/my")
    public ResponseEntity<CountPostsResponse> myPost(@RequestParam int offset, @RequestParam int limit, @RequestParam String status) {
        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            int idUser = userLoginService.getIdentifierMap().get(identifier);
            return new ResponseEntity(postsService.getMyPosts(idUser, offset, limit, status), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping(value = "/api/post/like")
    public ResponseEntity<LikeResponse> like(@RequestBody LikeRequest likeRequest) {
        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            int idUser = userLoginService.getIdentifierMap().get(identifier);
            return new ResponseEntity(likeDislikeService.postLike(idUser, likeRequest.getPostId()), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

    }

    @PostMapping("/api/post/dislike")
    public ResponseEntity<DislikeResponse> dislike(@RequestBody DislikeRequest dislikeRequest) {
        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            int idUser = userLoginService.getIdentifierMap().get(identifier);
            return new ResponseEntity(likeDislikeService.postDislike(idUser, dislikeRequest.getPostId()), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity<SinglePostResponse> getSingle(@PathVariable int id) {
        String identifier = httpSession.getId();
        return singlePostService.getSinglePost(id,identifier, userLoginService.getIdentifierMap());
    }

    @PutMapping("/api/post/{id}")
    public ResponseEntity<AddingPostResponse> putSingle(@RequestBody AddingPostRequest editPostRequest, @PathVariable int id) {
        String identifier = httpSession.getId();
        if (userLoginService.getIdentifierMap().containsKey(identifier)) {
            int idUser = userLoginService.getIdentifierMap().get(identifier);
            return new ResponseEntity(editingPostService.editPost(
                    editPostRequest,
                    idUser,
                    id), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/api/post/search")
    public ResponseEntity<CountPostsResponse> search(@RequestParam int offset, @RequestParam int limit, @RequestParam String query) {
        return new ResponseEntity(postsService.searchPosts(offset, limit, query), HttpStatus.OK);
    }

    @GetMapping("/api/post/byTag")
    public ResponseEntity<CountPostsResponse> searchTag(@RequestParam int offset, @RequestParam int limit, @RequestParam String tag) {
        return new ResponseEntity(postsService.searchTags(offset, limit, tag), HttpStatus.OK);
    }

    @GetMapping("/api/post/byDate")
    public ResponseEntity<CountPostsResponse> searchByDate(@RequestParam int offset, @RequestParam int limit, @RequestParam String date) {
        return new ResponseEntity(postsService.searchByDate(offset, limit, date), HttpStatus.OK);
    }
}
