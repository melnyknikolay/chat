package com.example.demo.config.socket;

import com.example.demo.event.MessageEvent;
import com.example.demo.event.MessageEventListener;
import com.example.demo.event.MessageEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class CustomWebSocketHandler implements WebSocketHandler {
    private final ObjectMapper objectMapper;
    private final MessageEventListener listener;

    public CustomWebSocketHandler(ObjectMapper objectMapper, MessageEventListener listener) {
        this.objectMapper = objectMapper;
        this.listener = listener;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        MessageEventPublisher eventPublisher = new MessageEventPublisher();
        listener.registerPublisher(UUID.randomUUID().toString() + "_" + session.getHandshakeInfo().getUri().getPath().replace("/ws/", "").replace("/message", ""), eventPublisher);

        Flux<MessageEvent> publish = Flux
                .create(eventPublisher)
                .share();

        Flux<WebSocketMessage> messageFlux = publish
                .map(evt -> {
                    try {
                        return objectMapper.writeValueAsString(evt.getSource());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(session::textMessage);

        return session.send(messageFlux);
    }
}
