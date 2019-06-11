package com.example.demo;

import com.example.demo.model.api.Chat;
import com.example.demo.model.api.Message;
import com.example.demo.model.api.Profile;
import com.example.demo.model.impl.ChatImpl;
import com.example.demo.model.impl.ProfileImpl;
import com.example.demo.repo.ChatRepository;
import com.example.demo.repo.MessageRepository;
import com.example.demo.repo.ProfileRepository;
import com.example.demo.service.api.ProfileService;
import com.example.demo.service.impl.ChatServiceImpl;
import com.example.demo.service.impl.MessageServiceImpl;
import com.example.demo.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Predicate;

import static org.mockito.Mockito.*;

@DirtiesContext
@DataMongoTest
@Import(ProfileServiceImpl.class)
@Disabled
public class ProfileImplServiceTest {

    public final static Profile PROFILE_1 = new ProfileImpl("1", "Josh");
    public final static Profile PROFILE_2 = new ProfileImpl("2", "Matt");
    public final static Profile PROFILE_3 = new ProfileImpl("3", "Jane");

    public final static Chat CHAT = new ChatImpl();

    static {
        ((ChatImpl) CHAT).setId("123");
        CHAT.getUsers().addAll(Arrays.asList(PROFILE_1, PROFILE_2, PROFILE_3));
    }

    @Autowired
    private final ProfileService service;
    @Autowired
    private final ProfileRepository repository;
    @MockBean
    private ChatRepository chatRepository;

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private ApplicationEventPublisher publisher;

    @SpyBean
    private ChatServiceImpl chatService;

    @SpyBean
    private MessageServiceImpl messageService;


    public ProfileImplServiceTest(@Autowired ProfileService service,
                                  @Autowired ProfileRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @BeforeEach
    public void init() {
        chatService = new ChatServiceImpl(chatRepository);
        messageService = new MessageServiceImpl(publisher, messageRepository);

        doReturn(Mono.just(CHAT))
                .when(chatRepository).findById(CHAT.getId());

        when(chatService.addUser(any(Profile.class), CHAT.getId()))
                .thenReturn(Mono.empty());
        when(chatService.removeUser(any(Profile.class), CHAT.getId()))
                .thenReturn(Mono.empty());

        when(messageService.create(any(Message.class)))
                .thenReturn(Mono.empty());

    }

    @Test
    public void getAll() {

        Flux<Profile> saved = repository.saveAll(Flux.just(PROFILE_1, PROFILE_2, PROFILE_3));

        Flux<Profile> composite = service.allByChat(CHAT.getId()).thenMany(saved);

        Predicate<Profile> match = profile -> saved.any(saveItem -> saveItem.equals(profile)).block();

        StepVerifier
                .create(composite)
                .expectNextMatches(match)
                .expectNextMatches(match)
                .expectNextMatches(match)
                .expectComplete()
                .verify();
    }

    @Test
    public void save() {

        Mono<Profile> profileMono = this.service.create(new ProfileImpl("john"), CHAT.getId());
        StepVerifier
                .create(profileMono)
                .expectNextMatches(saved -> StringUtils.hasText(saved.getId()))
                .verifyComplete();
    }

    @Test
    public void delete() {
        Mono<Profile> deleted = this.service
            .create(new ProfileImpl("john"), CHAT.getId())
            .flatMap(saved -> this.service.delete(saved.getId(), CHAT.getId()));
        StepVerifier
            .create(deleted)
            .expectNextMatches(profile -> profile.getName().equalsIgnoreCase("john"))
            .verifyComplete();
    }

    @Test
    public void update() throws Exception {
        Mono<Profile> saved = this.service
            .create(new ProfileImpl("john"), CHAT.getId())
            .flatMap(p -> {
                ((ProfileImpl)p).setName("Nick");
                return this.service.update(p);
            });
        StepVerifier
            .create(saved)
            .expectNextMatches(p -> p.getName().equalsIgnoreCase("nick"))
            .verifyComplete();
    }

    @Test
    public void getById() {
        String id = UUID.randomUUID().toString();
        Mono<Profile> deleted = this.service
            .create(new ProfileImpl(id, "john"), CHAT.getId())
            .flatMap(saved -> this.service.get(saved.getId()));
        StepVerifier
            .create(deleted)
            .expectNextMatches(profile -> StringUtils.hasText(profile.getId()) && id.equalsIgnoreCase(profile.getId()))
            .verifyComplete();
    }
}