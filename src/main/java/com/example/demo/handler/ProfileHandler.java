package com.example.demo.handler;

import com.example.demo.model.api.Profile;
import com.example.demo.model.impl.ProfileImpl;
import com.example.demo.service.api.ProfileService;
import com.example.demo.service.impl.ProfileServiceImpl;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class ProfileHandler {

    private final ProfileService profileService;

    public ProfileHandler(ProfileService profileService) {
        this.profileService = profileService;
    }

    public Mono<ServerResponse> getById(ServerRequest r) {
        return defaultReadResponse(this.profileService.get(id(r)));
    }

    public Mono<ServerResponse> all(ServerRequest r) {
        return defaultReadResponse(this.profileService.allByChat(r.pathVariable("chatId")));
    }

    public Mono<ServerResponse> deleteById(ServerRequest r) {
        return defaultReadResponse(this.profileService.delete(id(r), r.pathVariable("chatId")));
    }

    public Mono<ServerResponse> update(ServerRequest r) {
        Flux<Profile> updated = r.bodyToFlux(ProfileImpl.class)
                .flatMap(this.profileService::update);
        return defaultReadResponse(updated);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Flux<Profile> flux = request
                .bodyToFlux(ProfileImpl.class)
                .flatMap(toWrite -> this.profileService.create(toWrite, request.pathVariable("chatId")));
        return defaultWriteResponse(flux);
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<Profile> profiles) {
        return Mono
                .from(profiles)
                .flatMap(p -> ServerResponse
                        .created(URI.create("/profiles/" + p.getId()))
                        .header("profileId", p.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build()
                );
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<Profile> profiles) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(profiles, Profile.class);
    }

    private static String id(ServerRequest r) {
        return r.pathVariable("id");
    }
}
