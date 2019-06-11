package com.example.demo.event;

import com.example.demo.model.api.Message;
import org.springframework.context.ApplicationEvent;

public class MessageEvent extends ApplicationEvent {

    public MessageEvent(Message source) {
        super(source);
    }
}
