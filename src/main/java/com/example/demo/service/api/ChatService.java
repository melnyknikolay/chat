package com.example.demo.service.api;

import com.example.demo.model.api.Chat;
import com.example.demo.model.api.Profile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatService extends Service {
    Flux<Chat> all();

    Mono<Chat> get(String id);

    Mono<Chat> update(Chat chat);

    Mono<Chat> delete(String id);

    Mono<Chat> create(Chat chat);

    Mono<Void> addUser(Profile prof, String chatId);

    Mono<Void> removeUser(Profile prof, String chatId);
}
