package com.example.demo.config.socket;

import com.example.demo.event.MessageEventListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandlerChain implements WebSocketHandler {
    private final ObjectMapper objectMapper;
    private final MessageEventListener listener;
    private final Map<WebSocketSession, WebSocketHandler> handlerMap = new ConcurrentHashMap<>();

    public WebSocketHandlerChain(ObjectMapper objectMapper, MessageEventListener listener) {
        this.objectMapper = objectMapper;
        this.listener = listener;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        if (Objects.isNull(this.handlerMap.get(session))){
            this.handlerMap.put(session, new CustomWebSocketHandler(objectMapper, listener));
        }
        return this.handlerMap.get(session).handle(session);
    }
}
