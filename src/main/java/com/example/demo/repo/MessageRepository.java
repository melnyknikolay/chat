package com.example.demo.repo;

import com.example.demo.model.api.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
    Flux<Message> findAllByChatId(String chatId);
}
