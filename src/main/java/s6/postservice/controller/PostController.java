package s6.postservice.controller;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import s6.postservice.configuration.security.isauthenticated.IsAuthenticated;
import s6.postservice.datalayer.entities.Post;
import s6.postservice.dto.UpdatePostRequest;
import s6.postservice.dto.UpdatePostResponse;
import s6.postservice.servicelayer.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @IsAuthenticated
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"})
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }
    @PutMapping("{postId}")
    @IsAuthenticated
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"})
    public ResponseEntity<UpdatePostResponse> updatePost(@PathVariable(name =
            "postId")int postId, @RequestBody UpdatePostRequest request){
        request.setId(postId);
        return ResponseEntity.ok(postService.updatePost(request));
    }

    @GetMapping
    @IsAuthenticated
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MODERATOR"})
    public ResponseEntity<List<Post>> getPosts() {
        List<Post> posts = postService.getPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
    @GetMapping("/timeline/{userId}")
    @IsAuthenticated
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"})
    public ResponseEntity<List<Post>> getPostsTimeline(@PathVariable Integer userId){
        return new ResponseEntity<>(postService.getPostsHistoryForUserWithId(userId), HttpStatus.OK);
    }
    @GetMapping("/{userId}")
    @IsAuthenticated
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"})
    public ResponseEntity<List<Post>> getPostsOfUserWithId(@PathVariable Integer userId){
        return new ResponseEntity<>(postService.getPostsOfUserWithId(userId), HttpStatus.OK);
    }
    @GetMapping("/myposts/{postId}")
    @IsAuthenticated
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"})
    public ResponseEntity<Post> getPostById(@PathVariable Integer postId){
        return new ResponseEntity<>(postService.getPostById(postId), HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    @IsAuthenticated
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"})
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId) {
        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
