package com.example.demo.handler;

import com.example.demo.model.api.Message;
import com.example.demo.model.impl.ChatImpl;
import com.example.demo.model.impl.MessageImpl;
import com.example.demo.service.api.MessageService;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class MessageHandler {

    private final MessageService messageService;

    public MessageHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    public Mono<ServerResponse> all(ServerRequest request) {
        Flux<Message> flux = request
                .bodyToFlux(ChatImpl.class)
                .flatMap(this.messageService::all);
        return defaultReadResponse(flux);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Flux<Message> flux = request
                .bodyToFlux(MessageImpl.class)
                .flatMap(this.messageService::create);
        return defaultWriteResponse(flux);
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<Message> messagePublisher) {
        return Mono
                .from(messagePublisher)
                .flatMap(p -> ServerResponse
                        .created(URI.create("/message/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build()
                );
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<Message> messagePublisher) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(messagePublisher, Message.class);
    }
}
