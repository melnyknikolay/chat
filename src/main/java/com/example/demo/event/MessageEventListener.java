package com.example.demo.event;

import com.example.demo.model.api.Message;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageEventListener implements ApplicationListener<MessageEvent>{
    private final Map<String, MessageEventPublisher> publisherMap = new ConcurrentHashMap<>();

    public void registerPublisher(String name, MessageEventPublisher publisher){
        publisherMap.put(name, publisher);
    }

    @Override
    public void onApplicationEvent(MessageEvent event) {
        this.publisherMap.forEach((k, v) -> {
            if (k.contains(((Message)event.getSource()).getChat().getId())){
                v.onApplicationEvent(event);
            }
        });
    }


}
