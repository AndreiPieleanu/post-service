package s6.postservice.unit_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import s6.postservice.datalayer.IFriendsRelationshipDal;
import s6.postservice.datalayer.IPostDal;
import s6.postservice.datalayer.entities.FriendsRelationship;
import s6.postservice.datalayer.entities.Post;
import s6.postservice.datalayer.entities.Status;
import s6.postservice.dto.UpdatePostRequest;
import s6.postservice.dto.UpdatePostResponse;
import s6.postservice.rabbitmq.RabbitMQProducer;
import s6.postservice.servicelayer.PostService;
import s6.postservice.servicelayer.customexceptions.PostNotFoundException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    private PostService postService;
    private IPostDal postDal;
    private IFriendsRelationshipDal friendsRelationshipDal;
    private RabbitMQProducer rabbitMQProducer;


    @BeforeEach
    void setUp() {
        postDal = mock(IPostDal.class);
        friendsRelationshipDal = mock(IFriendsRelationshipDal.class);
        rabbitMQProducer = mock(RabbitMQProducer.class);
        postService =new PostService(postDal, friendsRelationshipDal, rabbitMQProducer);
    }

    @Test
    public void testCreatePost_Success() {
        // Given
        Post post = Post.builder()
                .text("Sample text")
                .userId(1)
                .build();

        Post savedPost = Post.builder()
                .id(1)
                .text("Sample text")
                .userId(1)
                .createdAt(new Date())
                .isBlocked(false)
                .build();

        when(postDal.save(post)).thenReturn(savedPost);

        // When
        Post result = postService.createPost(post);

        // Then
        assertNotNull(result);
        assertEquals(savedPost.getId(), result.getId());
        verify(postDal, times(1)).save(post);
        verify(rabbitMQProducer, times(1)).sendPostCreatedEvent(any());
    }

    @Test
    public void testCreatePost_Failure() {
        // Given
        Post post = Post.builder().text("Sample text").userId(1).build();
        when(postDal.save(post)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> postService.createPost(post));
        verify(postDal, times(1)).save(post);
        verifyNoInteractions(rabbitMQProducer);
    }

    @Test
    public void testUpdatePost_Success() {
        // Given
        UpdatePostRequest request = new UpdatePostRequest(1, "Updated text", 1, false);

        Post postToUpdate = Post.builder()
                .id(1)
                .text("Updated text")
                .userId(1)
                .isBlocked(false)
                .build();

        doNothing().when(postDal).updatePost(postToUpdate);

        // When
        UpdatePostResponse response = postService.updatePost(request);

        // Then
        assertNotNull(response);
        assertEquals(request.getText(), response.getText());
        verify(postDal, times(1)).updatePost(postToUpdate);
        verify(rabbitMQProducer, times(1)).sendPostUpdatedEvent(any());
    }

    @Test
    public void testUpdatePost_Failure() {
        // Given
        UpdatePostRequest request = new UpdatePostRequest(1, "Updated text", 1, false);

        Post postToUpdate = Post.builder()
                .id(1)
                .text("Updated text")
                .userId(1)
                .isBlocked(false)
                .build();

        doThrow(new RuntimeException("Update failed")).when(postDal).updatePost(postToUpdate);

        // When & Then
        assertThrows(RuntimeException.class, () -> postService.updatePost(request));
        verify(postDal, times(1)).updatePost(postToUpdate);
        verifyNoInteractions(rabbitMQProducer);
    }

    @Test
    public void testGetPosts_Success() {
        // Given
        List<Post> posts = new ArrayList<>();
        posts.add(Post.builder().id(1).text("Text 1").userId(1).createdAt(new Date()).build());
        when(postDal.findAll()).thenReturn(posts);

        // When
        List<Post> result = postService.getPosts();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postDal, times(1)).findAll();
    }

    @Test
    public void testGetPostsOfUserWithId_Success() {
        // Given
        List<Post> posts = new ArrayList<>();
        posts.add(Post.builder().id(1).text("Text 1").userId(1).createdAt(new Date()).build());
        when(postDal.getAllPostsOfUser(1)).thenReturn(posts);

        // When
        List<Post> result = postService.getPostsOfUserWithId(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postDal, times(1)).getAllPostsOfUser(1);
    }

    @Test
    public void testDeletePost_Success() {
        // When
        postService.deletePost(1);

        // Then
        verify(postDal, times(1)).deleteById(1);
    }

    @Test
    public void testGetPostsHistoryForUserWithId_Success() {
        // Given
        List<Post> posts = new ArrayList<>();
        Post post1 = Post.builder().id(1).text("Text 1").userId(1).createdAt(new Date()).build();
        Post post2 = Post.builder().id(2).text("Text 2").userId(1).createdAt(new Date(System.currentTimeMillis() - 1000)).build();
        posts.add(post1);
        posts.add(post2);
        when(postDal.findAll()).thenReturn(posts);

        // When
        List<Post> result = postService.getPostsHistoryForUserWithId(1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(post2.getId(), result.get(1).getId()); // Check order
        verify(postDal, times(1)).findAll();
    }

    @Test
    public void testGetPostsOfAllFriendsOfUserWithId_Success() {
        // Given
        List<FriendsRelationship> friends = new ArrayList<>();
        friends.add(new FriendsRelationship(1, 1, 2, Status.ACCEPTED));
        when(friendsRelationshipDal.findAll()).thenReturn(friends);

        List<Post> posts = new ArrayList<>();
        posts.add(Post.builder().id(1).text("Friend's post").userId(2).createdAt(new Date()).build());
        when(postDal.findAll()).thenReturn(posts);

        // When
        List<Post> result = postService.getPostsOfAllFriendsOfUserWithId(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(friendsRelationshipDal, times(1)).findAll();
        verify(postDal, times(1)).findAll();
    }

    @Test
    public void testGetPost_Success(){
        Post post = Post.builder().id(1).text("My post").userId(1).createdAt(new Date()).build();
        when(postDal.findById(post.getId())).thenReturn(Optional.of(post));

        Post retrievedPost = postService.getPostById(1);

        assertEquals(post, retrievedPost);
    }

    @Test
    public void testGetPost_Exception(){
        Post post = Post.builder().id(1).text("My post").userId(1).createdAt(new Date()).build();
        when(postDal.findById(post.getId())).thenReturn(Optional.empty());
        Exception ex = null;

        try{
            postService.getPostById(1);
        }
        catch (Exception e){
            ex = e;
        }

        assertNotNull(ex);
        assertInstanceOf(PostNotFoundException.class, ex);
    }
}
