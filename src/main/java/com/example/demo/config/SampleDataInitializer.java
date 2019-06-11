package com.example.demo.config;

import com.example.demo.model.impl.ChatImpl;
import com.example.demo.repo.ChatRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Log4j2
@Component
public class SampleDataInitializer
    implements ApplicationListener<ApplicationReadyEvent> {

    private final ChatRepository repository;

    public SampleDataInitializer(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Flux
                .just("A", "B", "C", "D")
                .map(name -> {
                    ChatImpl chat = new ChatImpl();
                    chat.setName("Chat " + name);
                    return chat;
                })
                .flatMap(chat -> repository.findByName(chat.getName()))
                .switchIfEmpty(repository
                                .saveAll(Flux
                                        .just("A", "B", "C", "D")
                                        .map(name -> {
                                            ChatImpl chat = new ChatImpl();
                                            chat.setName("Chat " + name);
                                            return chat;
                                        })))
                .subscribe();

//        repository
//                .saveAll()
//            .subscribe(chat -> log.info("saving " + chat.toString()));
    }
}