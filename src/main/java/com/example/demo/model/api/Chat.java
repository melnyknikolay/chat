package com.example.demo.model.api;

import com.example.demo.model.api.traits.Described;
import com.example.demo.model.api.traits.Identifiable;
import com.example.demo.model.api.traits.Named;

import java.util.List;

public interface Chat extends Entity,
        Named<String>, Identifiable<String>, Described<String> {
    int getUserCount();

    List<Profile> getUsers();
}