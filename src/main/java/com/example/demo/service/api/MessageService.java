package com.example.demo.service.api;

import com.example.demo.model.api.Chat;
import com.example.demo.model.api.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageService extends Service {
    Flux<Message> all(Chat chat);

    Mono<Message> create(Message message);
}
