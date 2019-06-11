package com.example.demo.model.impl;

import com.example.demo.model.api.Chat;
import com.example.demo.model.api.Message;
import com.example.demo.model.api.Profile;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlElement;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageImpl extends IdentifiableEntityImpl<String> implements Message {
    private MessageType type;
    @JsonDeserialize(as = ProfileImpl.class)
    private Profile user;
    @JsonDeserialize(as = ChatImpl.class)
    private Chat chat;
    private String msg;
}
