package main.controller;

import main.api.request.addingpost.AddingPostRequest;
import main.api.request.dislike.DislikeRequest;
import main.api.request.like.LikeRequest;
import main.api.response.addingpost.AddingPostResponse;
import main.api.response.posts.CountPostsResponse;
import main.api.response.singlepost.SinglePostResponse;
import main.api.response.vote.VoteResponse;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiPostController {

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
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "recent") String mode) {
        return new ResponseEntity(postsService.getPosts(offset, limit, mode), HttpStatus.OK);
    }

    @PostMapping("/api/post")
    public ResponseEntity<AddingPostResponse> addPost(@RequestBody AddingPostRequest addingPostRequest) {
        if (userLoginService.idUserAuthorized()) {
            return addingPostService.addPost(addingPostRequest);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/api/post/moderation")
    public ResponseEntity<CountPostsResponse> moderation(@RequestParam int offset, @RequestParam int limit, @RequestParam String status) {
        if (userLoginService.idUserAuthorized()) {
            return new ResponseEntity(postsService.getModerationPosts(offset, limit, status), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/api/post/my")
    public ResponseEntity<CountPostsResponse> myPost(@RequestParam int offset, @RequestParam int limit, @RequestParam String status) {
        if (userLoginService.idUserAuthorized()) {
            return new ResponseEntity(postsService.getMyPosts(offset, limit, status), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping(value = "/api/post/like")
    public ResponseEntity<VoteResponse> like(@RequestBody LikeRequest likeRequest) {
        if (userLoginService.idUserAuthorized()) {
            return new ResponseEntity(likeDislikeService.postVote(likeRequest.getPostId(), 1), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/api/post/dislike")
    public ResponseEntity<VoteResponse> dislike(@RequestBody DislikeRequest dislikeRequest) {
        if (userLoginService.idUserAuthorized()) {
            return new ResponseEntity(likeDislikeService.postVote(dislikeRequest.getPostId(), -1), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity<SinglePostResponse> getSingle(@PathVariable int id) {
        return singlePostService.getSinglePost(id);
    }

    @PutMapping("/api/post/{id}")
    public ResponseEntity<AddingPostResponse> editPost(@RequestBody AddingPostRequest editPostRequest, @PathVariable int id) {
        if (userLoginService.idUserAuthorized()) {
            return new ResponseEntity(editingPostService.editPost(editPostRequest, id), HttpStatus.OK);
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
