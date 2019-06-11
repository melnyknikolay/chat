package com.example.demo.repo;

import com.example.demo.model.api.Profile;
import com.example.demo.model.impl.ProfileImpl;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProfileRepository extends ReactiveMongoRepository<Profile, String> {
    Mono<Profile> findByName(String name);
}
