package s6.postservice.rabbitmq;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import s6.postservice.datalayer.IFriendsRelationshipDal;
import s6.postservice.datalayer.IPostDal;
import s6.postservice.datalayer.IUserDal;
import s6.postservice.datalayer.entities.FriendsRelationship;
import s6.postservice.datalayer.entities.Status;
import s6.postservice.datalayer.entities.User;
import s6.postservice.dto.FriendRequestAcceptedEvent;
import s6.postservice.dto.UserCreatedEvent;
import s6.postservice.dto.UserDeletedEvent;

@Service
@AllArgsConstructor
public class RabbitMQConsumer {
    private IUserDal userDal;
    private IPostDal postDal;
    private IFriendsRelationshipDal friendsRelationshipDal;
    @RabbitListener(queues = "user-create-queue")
    public void consumeUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        // Save user data locally in post-service
        System.out.println("Received User Created Event: " + userCreatedEvent);
        userDal.save(
                User
                        .builder()
                        .id(userCreatedEvent.getId())
                        .email(userCreatedEvent.getEmail())
                        .build()
        );
    }
    @RabbitListener(queues = "user-delete-queue")
    public void consumeUserDeletedEvent(UserDeletedEvent userDeletedEvent){
        System.out.println("Received User Deleted Event: " + userDeletedEvent);
        friendsRelationshipDal.deleteAllFriendRelationshipsOfUserWithId(userDeletedEvent.getId());
        postDal.deletePostsOfUserWithId(userDeletedEvent.getId());
        userDal.deleteById(userDeletedEvent.getId());
    }
    @RabbitListener(queues = "friend-create-queue")
    public void consumeCreatedFriendship(FriendRequestAcceptedEvent friendRequestAcceptedEvent){
        System.out.println("Received Friendship Created Event: " + friendRequestAcceptedEvent);
        FriendsRelationship relationship = FriendsRelationship
                .builder()
                .senderId(friendRequestAcceptedEvent.getSenderId())
                .receiverId(friendRequestAcceptedEvent.getReceiverId())
                .status(Status.ACCEPTED)
                .build();
        friendsRelationshipDal.save(
                relationship
        );
    }

    @RabbitListener(queues = "friend-delete-queue")
    public void consumeRemoveFriendship(Integer friendshipId){
        System.out.println("Received Friendship deleted event id: " + friendshipId);
        friendsRelationshipDal.deleteById(friendshipId);
    }
}
