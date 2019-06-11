package com.example.demo.config;

import com.example.demo.config.EndpointConfiguration;
import com.example.demo.config.socket.WebSocketConfiguration;
import com.example.demo.handler.ChatHandler;
import com.example.demo.handler.MessageHandler;
import com.example.demo.handler.ProfileHandler;
import com.example.demo.service.api.ChatService;
import com.example.demo.service.api.MessageService;
import com.example.demo.service.api.ProfileService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {EndpointConfiguration.class, ProfileHandler.class, ChatHandler.class, MessageHandler.class, WebSocketConfiguration.class})
@WebFluxTest
public abstract class BaseEndpointsTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    protected WebTestClient client;

    @MockBean
    protected ProfileService profileService;
    @MockBean
    protected ChatService chatService;
    @MockBean
    protected MessageService messageService;

    @Before
    public void setUp()
    {
        client = WebTestClient.bindToApplicationContext(context).build();
    }
}
