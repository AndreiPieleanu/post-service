package s6.postservice.rabbitmq;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import s6.postservice.dto.PostCreatedEvent;
import s6.postservice.dto.PostUpdatedEvent;

@Service
@AllArgsConstructor
public class RabbitMQProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendPostCreatedEvent(PostCreatedEvent postCreatedEvent) {
        rabbitTemplate.convertAndSend("post-exchange", "post.created", postCreatedEvent);
        System.out.println("Post Created Event Published: " + postCreatedEvent);
    }

    public void sendPostUpdatedEvent(PostUpdatedEvent postUpdatedEvent) {
        // Send post content to "post-exchange" with "post.update" routing key
        rabbitTemplate.convertAndSend("post-exchange", "post.updated", postUpdatedEvent);
        System.out.println("Post Updated Event Published: " + postUpdatedEvent);
    }
}
