/**
 * Copyright TowerIQ 2018
 */
package com.example.demo.model.impl;

import com.example.demo.model.api.Entity;
import com.example.demo.model.api.traits.Identifiable;
import com.example.demo.model.api.traits.Named;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @see Identifiable
 * @see Named
 * @see Entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentifiableNamedEntityImpl<T, X> extends BaseEntityImpl implements Entity, Identifiable<T>, Named<X> {
    protected T id;
    protected X name;
}
