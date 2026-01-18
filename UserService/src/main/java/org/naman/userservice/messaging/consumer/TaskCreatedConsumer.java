package org.naman.userservice.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.naman.userservice.config.RabbitMqConfig;
import org.naman.userservice.mappers.UserMapper;
import org.naman.userservice.messaging.events.TaskCreatedEvent;
import org.naman.userservice.messaging.events.UserValidatedEvent;
import org.naman.userservice.messaging.events.UserValidationFailedEvent;
import org.naman.userservice.messaging.publisher.UserEventPublisher;
import org.naman.userservice.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskCreatedConsumer {
    private final UserRepository userRepository;
    private final UserEventPublisher publisher;
    @RabbitListener(queues = RabbitMqConfig.TASK_CREATED_QUEUE)
    public void handle(TaskCreatedEvent event){
        boolean userExists = userRepository.existsById(event.getUserId());
        if (userExists) {
            UserValidatedEvent validatedEvent = UserMapper.TaskToUserEvent(event);
            log.info("User exists: {}", validatedEvent);
            publisher.publishUserValidated(validatedEvent);
            log.info("Successful Published from the UserService with the event with projectId {} and CorrelationId {}",event.getProjectId(), event.getCorrelationId());
        } else {
            UserValidationFailedEvent failedEvent = UserMapper.TaskToUserFailedEvent(event);
            log.info("User does not exist: {}", failedEvent);   
            publisher.publishUserValidationFailed(failedEvent);
            log.info("Failed to Publish the event from UserService ");
        }
    }

}
