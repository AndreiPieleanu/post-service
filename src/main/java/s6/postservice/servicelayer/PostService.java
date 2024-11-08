package s6.postservice.servicelayer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import s6.postservice.datalayer.IFriendsRelationshipDal;
import s6.postservice.datalayer.IPostDal;
import s6.postservice.datalayer.entities.FriendsRelationship;
import s6.postservice.datalayer.entities.Post;
import s6.postservice.dto.PostCreatedEvent;
import s6.postservice.dto.PostUpdatedEvent;
import s6.postservice.dto.UpdatePostRequest;
import s6.postservice.dto.UpdatePostResponse;
import s6.postservice.rabbitmq.RabbitMQProducer;
import s6.postservice.servicelayer.customexceptions.PostNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {
    private final IPostDal postDal;
    private final IFriendsRelationshipDal friendsRelationshipDal;
    private final RabbitMQProducer rabbitMQProducer;

    public Post createPost(Post post) {
        Post createdPost = postDal.save(post);
        PostCreatedEvent event = PostCreatedEvent
                .builder()
                .id(createdPost.getId())
                .text(createdPost.getText())
                .createdAt(createdPost.getCreatedAt())
                .userId(createdPost.getUserId())
                .isBlocked(createdPost.getIsBlocked())
                .build();
        rabbitMQProducer.sendPostCreatedEvent(event);
        return createdPost;
    }
    public UpdatePostResponse updatePost(UpdatePostRequest request){
        Post postToUpdate = Post.builder().id(request.getId()).text(request.getText()).userId(request.getUserId()).isBlocked(request.getIsBlocked()).build();
        postDal.updatePost(postToUpdate);
        UpdatePostResponse response = new UpdatePostResponse(postToUpdate.getId(), postToUpdate.getText(), postToUpdate.getUserId(), postToUpdate.getIsBlocked());
        PostUpdatedEvent event = PostUpdatedEvent.builder().id(response.getId()).text(response.getText()).userId(response.getUserId()).build();
        rabbitMQProducer.sendPostUpdatedEvent(event);
        return response;
    }
    public List<Post> getPosts(){
        return postDal.findAll();
    }
    public List<Post> getPostsOfUserWithId(Integer userId){
        return postDal.getAllPostsOfUser(userId);
    }
    public void deletePost(Integer postId) {
        postDal.deleteById(postId);
    }
    public List<Post> getPostsHistoryForUserWithId(Integer userId){
        List<Post> postsOrdered = new ArrayList<>(postDal.findAll().stream().filter(p -> p.getUserId().equals(userId)).toList());
        postsOrdered.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
        return postsOrdered;
    }
    public List<Post> getPostsOfAllFriendsOfUserWithId(Integer userId){
        List<FriendsRelationship> friendsOfUser = friendsRelationshipDal
                .findAll()
                .stream()
                .filter(r -> Objects.equals(r.getReceiverId(), userId) || Objects.equals(r.getSenderId(), userId))
                .toList();
        List<Post> posts = postDal
                .findAll()
                .stream()
                .filter(p -> friendsOfUser.stream().anyMatch(f -> Objects.equals(f.getSenderId(), p.getUserId()) || Objects.equals(f.getReceiverId(), p.getUserId())))
                .toList();
        return posts;
    }
    public Post getPostById(Integer postId){
        Optional<Post> post = postDal.findById(postId);
        if(post.isEmpty()){
            throw new PostNotFoundException();
        }
        return post.get();
    }

}
