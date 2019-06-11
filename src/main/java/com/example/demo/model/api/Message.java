package com.example.demo.model.api;

import com.example.demo.model.api.traits.Identifiable;
import com.example.demo.model.api.traits.Typed;
import com.example.demo.model.impl.MessageType;

public interface Message extends Entity,
        Typed<MessageType>, Identifiable<String> {

    Profile getUser();

    Chat getChat();

    String getMsg();
}