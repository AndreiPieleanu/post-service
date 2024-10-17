package s6.postservice.datalayer;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import s6.postservice.datalayer.entities.Post;

import java.util.List;

@Repository
public interface IPostDal extends JpaRepository<Post, Integer> {
    @Query("select p from Post p where p.userId=:#{#userId}")
    List<Post> getAllPostsOfUser(Integer userId);
    @Transactional
    @Modifying
    @Query("update Post p set p.text=:#{#post.text}, p.isBlocked=:#{#post.isBlocked}" +
            " where p.id=:#{#post.id}")
    void updatePost(Post post);
    @Transactional
    @Modifying
    @Query("delete Post p where p.userId=:#{#userId}")
    void deletePostsOfUserWithId(Integer userId);
}
