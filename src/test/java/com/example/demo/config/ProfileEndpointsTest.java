package com.example.demo.config;

import com.example.demo.model.api.Profile;
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


public class ProfileEndpointsTest extends BaseEndpointsTest {
    private final static String CHAT_ID = "5";
    private final static String USER_ID = "123";
    private final static ProfileImpl PROFILE = new ProfileImpl(USER_ID, UUID.randomUUID().toString());

    private final static String BASE_URL = "/profile/";

    @Test
    public void getAll() throws Exception {

        when(this.profileService.allByChat(eq(CHAT_ID)))
                .thenReturn(Flux.fromStream(Stream.of(new ProfileImpl("1", "A"), new ProfileImpl("2", "B"))));


        this.client
                .get()
                .uri(BASE_URL + "chat/" + CHAT_ID)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo("1")
                .jsonPath("$.[0].name").isEqualTo("A")
                .jsonPath("$.[1].id").isEqualTo("2")
                .jsonPath("$.[1].name").isEqualTo("B");
    }

    @Test
    public void getById() throws Exception {


        when(this.profileService.get(eq(USER_ID)))
                .thenReturn(Mono.just(new ProfileImpl("1", "A")));


        this.client
                .get()
                .uri(BASE_URL + USER_ID)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.name").isEqualTo("A");
    }

    @Test
    public void save() {
        when(this.profileService.create(any(Profile.class), eq(CHAT_ID)))
                .thenReturn(Mono.just(PROFILE));

        this.client
                .post()
                .uri(BASE_URL + CHAT_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(PROFILE), Profile.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Test
    public void delete() {

        when(this.profileService.delete(eq(USER_ID), eq(CHAT_ID)))
                .thenReturn(Mono.just(PROFILE));
        this
                .client
                .delete()
                .uri(BASE_URL + USER_ID + "/" + CHAT_ID)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Test
    public void update() {

        when(this.profileService.update(any(Profile.class)))
                .thenReturn(Mono.just(PROFILE));

        this
                .client
                .put()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(PROFILE), Profile.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
    }

}