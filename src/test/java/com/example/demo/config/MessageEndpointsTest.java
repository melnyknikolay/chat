package com.example.demo.config;

import com.example.demo.model.api.Chat;
import com.example.demo.model.api.Message;
import com.example.demo.model.api.Profile;
import com.example.demo.model.impl.ChatImpl;
import com.example.demo.model.impl.MessageImpl;
import com.example.demo.model.impl.MessageType;
import com.example.demo.model.impl.ProfileImpl;
import org.junit.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


public class MessageEndpointsTest extends BaseEndpointsTest {
    private final static String BASE_URL = "/message/";

    private final static String CHAT_ID = "1";
    private final static Chat CHAT = new ChatImpl();
    static {
        ((ChatImpl)CHAT).setId(CHAT_ID);
        ((ChatImpl)CHAT).setName(UUID.randomUUID().toString());
    }

    private final static String USER_ID_1 = "1";
    private final static String USER_ID_2 = "2";
    private final static ProfileImpl PROFILE_1 = new ProfileImpl(USER_ID_1, UUID.randomUUID().toString());
    private final static ProfileImpl PROFILE_2 = new ProfileImpl(USER_ID_2, UUID.randomUUID().toString());

    @Test
    public void getAll() throws Exception {

        when(this.messageService.all(eq(CHAT)))
                .thenReturn(Flux.fromStream(Stream.of(new MessageImpl(MessageType.CHAT, PROFILE_1, CHAT, "test message"), new MessageImpl(MessageType.CHAT, PROFILE_2, CHAT, "test message2"))));


        this.client
                .post()
                .uri(BASE_URL + "all")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(CHAT), Chat.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.[0].chat.id").isEqualTo(CHAT_ID)
                .jsonPath("$.[0].user.id").isEqualTo(USER_ID_1)
                .jsonPath("$.[1].chat.id").isEqualTo(CHAT_ID)
                .jsonPath("$.[1].user.id").isEqualTo(USER_ID_2);
    }


    @Test
    public void save() {
        when(this.messageService.create(any(Message.class)))
                .thenReturn(Mono.just(new MessageImpl(MessageType.CHAT, PROFILE_1, CHAT, "test message")));

        this.client
                .post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(new MessageImpl(MessageType.CHAT, PROFILE_1, CHAT, "test message")), Message.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
    }



}