package s6.postservice.datalayer;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import s6.postservice.datalayer.entities.FriendsRelationship;

@Repository
public interface IFriendsRelationshipDal extends JpaRepository<FriendsRelationship, Integer> {
    @Transactional
    @Modifying
    @Query("delete FriendsRelationship f where f.receiverId=:#{#userId} or f.senderId=:#{#userId}")
    void deleteAllFriendRelationshipsOfUserWithId(Integer userId);
}
