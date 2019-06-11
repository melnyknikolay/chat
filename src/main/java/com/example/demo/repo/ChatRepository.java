package com.example.demo.repo;

import com.example.demo.model.api.Chat;
import com.example.demo.model.api.Profile;
import com.example.demo.model.impl.ChatImpl;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {
    Mono<Chat> findByName(String name);
}
