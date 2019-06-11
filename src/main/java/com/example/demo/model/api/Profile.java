package com.example.demo.model.api;

import com.example.demo.model.api.traits.Activatable;
import com.example.demo.model.api.traits.Identifiable;
import com.example.demo.model.api.traits.Named;

public interface Profile extends Entity,
        Named<String>, Identifiable<String>, Activatable{
}