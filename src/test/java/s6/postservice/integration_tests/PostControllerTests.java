package s6.postservice.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import s6.postservice.datalayer.entities.Post;
import s6.postservice.dto.UpdatePostRequest;
import s6.postservice.dto.UpdatePostResponse;
import s6.postservice.servicelayer.PostService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwaWVsZWFudWFuZHJlaTIwMDFAZ21haWwuY29tIiwiaWF0IjoxNzMwODA3NzY5LCJleHAiOjE3MzA4MTg1NjksInJvbGUiOiJVU0VSIiwidXNlcklkIjoxfQ.cYOEkq0dZNI2x3crwsS7knvnBtSe4GVCpeLySwnPhOA";

    @Test
    public void testCreatePost_Success() throws Exception {
        // Given
        Post post = Post.builder().text("Sample text").userId(1).build();
        Post createdPost = Post.builder().id(1).text("Sample text").userId(1).createdAt(new Date()).build();
        when(postService.createPost(Mockito.any(Post.class))).thenReturn(createdPost);

        // When & Then
        mockMvc.perform(post("/api/posts")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Sample text"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(postService, times(1)).createPost(Mockito.any(Post.class));
    }

    @Test
    public void testUpdatePost_Success() throws Exception {
        // Given
        UpdatePostRequest request = new UpdatePostRequest(1, "Updated text", 1, false);
        UpdatePostResponse response = new UpdatePostResponse(1, "Updated text", 1, false);
        when(postService.updatePost(Mockito.any(UpdatePostRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/api/posts/1")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Updated text"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(postService, times(1)).updatePost(Mockito.any(UpdatePostRequest.class));
    }

    @Test
    public void testGetPostsTimeline_Success() throws Exception {
        // Given
        List<Post> posts = Arrays.asList(
                Post.builder().id(1).text("Timeline post 1").userId(1).createdAt(new Date()).build()
        );
        when(postService.getPostsHistoryForUserWithId(1)).thenReturn(posts);

        // When & Then
        mockMvc.perform(get("/api/posts/timeline/1")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].text").value("Timeline post 1"));

        verify(postService, times(1)).getPostsHistoryForUserWithId(1);
    }

    @Test
    public void testGetPostsOfUserWithId_Success() throws Exception {
        // Given
        List<Post> posts = Arrays.asList(
                Post.builder().id(1).text("User post").userId(1).createdAt(new Date()).build()
        );
        when(postService.getPostsOfUserWithId(1)).thenReturn(posts);

        // When & Then
        mockMvc.perform(get("/api/posts/1")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].text").value("User post"));

        verify(postService, times(1)).getPostsOfUserWithId(1);
    }

    @Test
    public void testDeletePost_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/posts/1")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(postService, times(1)).deletePost(1);
    }
}
