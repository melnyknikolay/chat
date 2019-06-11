package com.example.demo.config;

import com.example.demo.handler.ChatHandler;
import com.example.demo.handler.MessageHandler;
import com.example.demo.handler.ProfileHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class EndpointConfiguration {

    @Bean
    RouterFunction<ServerResponse> routes(ProfileHandler profileHandler, ChatHandler chatHandler, MessageHandler messageHandler, @Value("classpath:/static/index.html") Resource html) {
        return
//              profile
                route(GET("/profile/chat/{chatId}"), profileHandler::all)
                        .andRoute(GET("/profile/{id}"), profileHandler::getById)
                        .andRoute(DELETE("/profile/{id}/{chatId}"), profileHandler::deleteById)
                        .andRoute(POST("/profile/{chatId}"), profileHandler::create)
                        .andRoute(PUT("/profile"), profileHandler::update)

//              chat
                        .andRoute(GET("/chat"), chatHandler::all)
                        .andRoute(GET("/chat/{id}"), chatHandler::getById)
                        .andRoute(DELETE("/chat/{id}"), chatHandler::deleteById)
                        .andRoute(POST("/chat"), chatHandler::create)
                        .andRoute(PUT("/chat"), chatHandler::update)

//              message
                        .andRoute(POST("/message/all"), messageHandler::all)
                        .andRoute(POST("/message"), messageHandler::create)

//              static
                        .andRoute(GET("/"), request
                                -> ok().contentType(MediaType.TEXT_HTML).syncBody(html));


    }
}