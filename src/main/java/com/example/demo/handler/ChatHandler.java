package com.example.demo.handler;

import com.example.demo.model.api.Chat;
import com.example.demo.model.impl.ChatImpl;
import com.example.demo.service.api.ChatService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
public class ChatHandler {

    private final ChatService chatService;
    private final HandlerMapping mapping;
    private final WebSocketHandler handler;

    public ChatHandler(ChatService chatService, @Qualifier("webSocketHandlerMapping") HandlerMapping mapping, WebSocketHandler handler) {
        this.chatService = chatService;
        this.mapping = mapping;
        this.handler = handler;
    }

    public Mono<ServerResponse> getById(ServerRequest r) {
        return defaultReadResponse(this.chatService.get(id(r)));
    }

    public Mono<ServerResponse> all(ServerRequest r) {
        return defaultReadResponse(this.chatService.all());
    }

    public Mono<ServerResponse> deleteById(ServerRequest r) {
        return defaultReadResponse(this.chatService.delete(id(r)));
    }

    public Mono<ServerResponse> update(ServerRequest r) {
        Flux<Chat> updated = r.bodyToFlux(ChatImpl.class)
                .flatMap(this.chatService::update);
        return defaultReadResponse(updated);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Flux<Chat> flux = request
                .bodyToFlux(ChatImpl.class)
                .flatMap(chat -> this.chatService.create(chat)
                        .doOnSuccess(cht -> {
                            Map<String, Object> urlMap = (Map<String, Object>) ((SimpleUrlHandlerMapping) mapping).getUrlMap();
                            urlMap.putIfAbsent("/ws/" + cht.getId() + "/message", handler);
                            ((SimpleUrlHandlerMapping) mapping).initApplicationContext();
                        }));
        return defaultWriteResponse(flux);
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<Chat> chats) {
        return Mono
                .from(chats)
                .flatMap(chat -> ServerResponse
                        .created(URI.create("/chat/" + chat.getId()))
                        .header("chatId", chat.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build()
                );
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<Chat> chats) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(chats, Chat.class);
    }

    private static String id(ServerRequest r) {
        return r.pathVariable("id");
    }
}
