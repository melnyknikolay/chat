package com.example.demo.service.impl;

import com.example.demo.model.api.Chat;
import com.example.demo.model.api.Profile;
import com.example.demo.model.impl.ChatImpl;
import com.example.demo.model.impl.ProfileImpl;
import com.example.demo.repo.ChatRepository;
import com.example.demo.service.api.ChatService;
import com.example.demo.service.api.ProfileService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;


@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    public ChatServiceImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
        ServiceContext.register(this);
    }

    @Override
    public Flux<Chat> all() {
        return this.chatRepository.findAll();
    }

    @Override
    public Mono<Chat> get(String id) {
        return this.chatRepository.findById(id)
                .doOnSuccess(chat -> chat.getUsers()
                        .forEach(profile -> ((ProfileService)ServiceContext.getService(ProfileService.class)).get(profile.getId())));
    }

    @Override
    public Mono<Chat> update(Chat chat) {
        return this.chatRepository
                .findById(chat.getId())
                .map(ct -> new ChatImpl(chat))
                .flatMap(this.chatRepository::save);
    }

    @Override
    public Mono<Chat> delete(String id) {
        return this.chatRepository
                .findById(id)
                .flatMap(chat -> this.chatRepository.deleteById(chat.getId()).thenReturn(chat));
    }

    @Override
    public Mono<Chat> create(Chat chat) {
        return getByName(chat.getName())
                .switchIfEmpty(Mono.defer(() -> {
                    ((ChatImpl) chat).setCreated(new Date());
                    return this.chatRepository
                            .save(chat);
                }));
    }

    @Override
    public Mono<Void> addUser(Profile prof, String chatId) {
        return this.get(chatId)
                .doOnSuccess(chat -> {
                    chat.getUsers().add(new ProfileImpl(prof.getId(), prof.getName()));
                    this.chatRepository
                            .save(chat).subscribe();
                }).then();
    }

    @Override
    public Mono<Void> removeUser(Profile prof, String chatId) {
        return this.get(chatId)
                .doOnSuccess(chat -> {
                    chat.getUsers().remove(new ProfileImpl(prof.getId(), prof.getName()));
                    this.chatRepository
                            .save(chat).subscribe();
                })
                .then();
    }

    private Mono<Chat> getByName(String name) {
        return this.chatRepository.findByName(name);
    }
}