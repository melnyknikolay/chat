/**
 * Copyright Copyright TowerIQ 2018
 */
package com.example.demo.model.api.traits;


import org.springframework.data.annotation.Id;

/**
 * Defines all entities that have addresses
 */
public interface Identifiable<T> {

    @Id
    T getId();
}
