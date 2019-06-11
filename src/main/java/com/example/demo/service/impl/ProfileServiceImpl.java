package com.example.demo.service.impl;

import com.example.demo.model.api.Profile;
import com.example.demo.model.api.traits.Identifiable;
import com.example.demo.model.impl.ChatImpl;
import com.example.demo.model.impl.MessageImpl;
import com.example.demo.model.impl.MessageType;
import com.example.demo.model.impl.ProfileImpl;
import com.example.demo.repo.ProfileRepository;
import com.example.demo.service.api.ChatService;
import com.example.demo.service.api.MessageService;
import com.example.demo.service.api.ProfileService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
        ServiceContext.register(this);
    }

    @Override
    public Flux<Profile> allByChat(String chatId) {
        return this.profileRepository.findAllById(((ChatService) ServiceContext.getService(ChatService.class)).get(chatId)
                .flatMapMany(chat -> Flux.fromStream(chat.getUsers().stream()
                        .map(Identifiable::getId)
                )));
    }

    @Override
    public Mono<Profile> get(String id) {
        return this.profileRepository.findById(id);
    }

    @Override
    public Mono<Profile> update(Profile profile) {
        return this.profileRepository
                .findById(profile.getId())
                .map(p -> new ProfileImpl(profile))
                .flatMap(this.profileRepository::save);
    }

    @Override
    public Mono<Profile> delete(String id, String chatId) {
        return this.profileRepository
                .findById(id)
                .flatMap(p -> this.profileRepository.deleteById(p.getId()).thenReturn(p))
                .doOnSuccess(profile -> {
                    ((ChatService) ServiceContext.getService(ChatService.class)).removeUser(profile, chatId).subscribe();
                    ((MessageService) ServiceContext.getService(MessageService.class)).create(new MessageImpl(MessageType.LEAVE, new ProfileImpl(profile.getId(), profile.getName()), new ChatImpl(chatId), "User leave chat")).subscribe();
                });
    }

    @Override
    public Mono<Profile> create(Profile profile, String chatId) {
        return getByName(profile.getName())
                .doOnNext(getUserJoinedConsumer(chatId))
                .switchIfEmpty(Mono.defer(() -> {
                    ((ProfileImpl) profile).setCreated(new Date());
                    return this.profileRepository
                            .save(profile)
                            .doOnSuccess(getUserJoinedConsumer(chatId));
                }));

    }

    private Consumer<Profile> getUserJoinedConsumer(String chatId) {
        return prof -> {
            ((ChatService) ServiceContext.getService(ChatService.class)).addUser(prof, chatId).subscribe();
            ((MessageService) ServiceContext.getService(MessageService.class)).create(new MessageImpl(MessageType.JOIN, new ProfileImpl(prof.getId(), prof.getName()), new ChatImpl(chatId), "User joined")).subscribe();
        };
    }

    private Mono<Profile> getByName(String name) {
        return this.profileRepository.findByName(name);
    }

}