package com.example.demo.service.impl;

import com.example.demo.event.MessageEvent;
import com.example.demo.model.api.Chat;
import com.example.demo.model.api.Message;
import com.example.demo.model.impl.MessageImpl;
import com.example.demo.repo.MessageRepository;
import com.example.demo.service.api.ChatService;
import com.example.demo.service.api.MessageService;
import com.example.demo.service.api.ProfileService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;


@Service
public class MessageServiceImpl implements MessageService {

    private final ApplicationEventPublisher publisher;
    private final MessageRepository messageRepository;

    public MessageServiceImpl(ApplicationEventPublisher publisher, MessageRepository messageRepository) {
        this.publisher = publisher;
        this.messageRepository = messageRepository;
        ServiceContext.register(this);
    }

    @Override
    public Flux<Message> all(Chat chat) {
        return this.messageRepository.findAllByChatId(chat.getId());
    }


    @Override
    public Mono<Message> create(Message message) {
        ((MessageImpl) message).setCreated(new Date());
        return this.messageRepository
                .save(message)
                .doOnSuccess(msg -> this.publisher.publishEvent(new MessageEvent(msg)));
    }

    private void fillDependency(Message msg) {
        MessageImpl mImpl = (MessageImpl) msg;
        ((ChatService) ServiceContext.getService(ChatService.class))
                .get(mImpl.getChat().getId())
                .doOnSuccess(chat -> {
                    ((ProfileService) ServiceContext.getService(ProfileService.class))
                            .get(mImpl.getUser().getId())
                            .doOnSuccess(profile -> {
                                mImpl.setChat(chat);
                                mImpl.setUser(profile);
                                this.publisher.publishEvent(new MessageEvent(msg));
                            });
                }).then();


    }
}