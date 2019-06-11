package com.example.demo.config;

import com.example.demo.model.api.Chat;
import com.example.demo.model.api.Profile;
import com.example.demo.model.impl.ChatImpl;
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


public class ChatEndpointsTest extends BaseEndpointsTest {
    private final static String CHAT_ID_1 = "1";
    private final static String CHAT_ID_2 = "2";

    private final static Chat CHAT_1 = new ChatImpl();
    private final static Chat CHAT_2 = new ChatImpl();

    private final static String BASE_URL = "/chat/";

    static {
        ((ChatImpl)CHAT_1).setId(CHAT_ID_1);
        ((ChatImpl)CHAT_1).setName(UUID.randomUUID().toString());

        ((ChatImpl)CHAT_2).setId(CHAT_ID_2);
        ((ChatImpl)CHAT_2).setName(UUID.randomUUID().toString());
    }

    @Test
    public void getAll() throws Exception {

        when(this.chatService.all())
                .thenReturn(Flux.fromStream(Stream.of(CHAT_1, CHAT_2)));


        this.client
                .get()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(CHAT_1.getId())
                .jsonPath("$.[0].name").isEqualTo(CHAT_1.getName())
                .jsonPath("$.[1].id").isEqualTo(CHAT_2.getId())
                .jsonPath("$.[1].name").isEqualTo(CHAT_2.getName());
    }

    @Test
    public void getById() throws Exception {

        when(this.chatService.get(eq(CHAT_ID_1)))
                .thenReturn(Mono.just(CHAT_1));


        this.client
                .get()
                .uri(BASE_URL + CHAT_ID_1)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isEqualTo(CHAT_1.getId())
                .jsonPath("$.name").isEqualTo(CHAT_1.getName());
    }

    @Test
    public void save() {
        when(this.chatService.create(any(Chat.class)))
                .thenReturn(Mono.just(CHAT_1));

        this.client
                .post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(CHAT_1), Chat.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Test
    public void delete() {

        when(this.chatService.delete(eq(CHAT_ID_1)))
                .thenReturn(Mono.just(CHAT_1));
        this
                .client
                .delete()
                .uri(BASE_URL + CHAT_ID_1)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isEqualTo(CHAT_1.getId())
                .jsonPath("$.name").isEqualTo(CHAT_1.getName());
    }

    @Test
    public void update() {

        when(this.chatService.update(any(Chat.class)))
                .thenReturn(Mono.just(CHAT_1));

        this
                .client
                .put()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(CHAT_1), Chat.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
    }

}