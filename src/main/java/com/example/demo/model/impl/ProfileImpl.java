package com.example.demo.model.impl;

import com.example.demo.model.api.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Document
@Data
@NoArgsConstructor
public class ProfileImpl extends IdentifiableNamedEntityImpl<String, String> implements Profile {
    private boolean active;

    public ProfileImpl(Profile profile) {
        this.id = profile.getId();
        this.name = profile.getName();
        this.created = profile.getCreated();
        this.active = profile.isActive();
    }

    public ProfileImpl(String id, String name, Date created, boolean active) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.active = active;
    }

    public ProfileImpl(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProfileImpl(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProfileImpl profile = (ProfileImpl) o;
        return id.equals(profile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
