package com.example.demo.config.socket;

import com.example.demo.event.MessageEventListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class WebSocketConfiguration {

    @Bean
    Executor executor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean("webSocketHandlerMapping")
    HandlerMapping handlerMapping() {
        return new SimpleUrlHandlerMapping() {
            {
                setLazyInitHandlers(true);
                setOrder(10);
            }
        };
    }

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    WebSocketHandler webSocketHandler(ObjectMapper mapper, MessageEventListener listener){
        return new WebSocketHandlerChain(mapper, listener);
    }

    @Bean
    MessageEventListener messageEventListener(){
        return new MessageEventListener();
    }
}