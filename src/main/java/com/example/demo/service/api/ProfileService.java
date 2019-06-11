package com.example.demo.service.api;

import com.example.demo.model.api.Profile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProfileService extends Service {
    Flux<Profile> allByChat(String chatId);

    Mono<Profile> get(String id);

    Mono<Profile> update(Profile profile);

    Mono<Profile> delete(String id, String chatId);

    Mono<Profile> create(Profile profile, String chatId);
}
