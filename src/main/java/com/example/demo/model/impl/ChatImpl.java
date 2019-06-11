package com.example.demo.model.impl;

import com.example.demo.model.api.Chat;
import com.example.demo.model.api.Message;
import com.example.demo.model.api.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatImpl extends IdentifiableNamedAndDescribedEntityImpl<String, String, String> implements Chat {
    @Transient
    private int userCount;
    private List<Profile> users = new ArrayList<>();

    public ChatImpl(Chat chat) {
        this.id = chat.getId();
        this.description = chat.getDescription();
        this.name = chat.getName();
        this.userCount = chat.getUserCount();
        this.users = chat.getUsers();
    }

    public ChatImpl(String id){
        this.id = id;
    }
    public int getUserCount() {
        return this.users.size();
    }


}
