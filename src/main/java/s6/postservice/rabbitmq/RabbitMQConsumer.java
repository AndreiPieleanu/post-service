package s6.postservice.rabbitmq;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import s6.postservice.datalayer.IPostDal;
import s6.postservice.datalayer.IUserDal;
import s6.postservice.datalayer.entities.User;
import s6.postservice.dto.UserCreatedEvent;
import s6.postservice.dto.UserDeletedEvent;

@Service
@AllArgsConstructor
public class RabbitMQConsumer {
    private IUserDal userDal;
    private IPostDal postDal;
    @RabbitListener(queues = "user-create-queue")
    public void consumeUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        // Save user data locally in post-service
        System.out.println("Received User Created Event: " + userCreatedEvent);
        userDal.save(User.builder().id(userCreatedEvent.getId()).email(userCreatedEvent.getEmail()).build());
    }
    @RabbitListener(queues = "user-delete-queue")
    public void consumeUserDeletedEvent(UserDeletedEvent userDeletedEvent){
        System.out.println("Received User Deleted Event: " + userDeletedEvent);
        postDal.deletePostsOfUserWithId(userDeletedEvent.getId());
        userDal.deleteById(userDeletedEvent.getId());
    }
}
