/**
 * Copyright TowerIQ 2018
 */
package com.example.demo.model.impl;

import com.example.demo.model.api.Entity;
import com.example.demo.model.api.traits.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentifiableEntityImpl<T> extends BaseEntityImpl implements Entity, Identifiable<T> {
    private static final long serialVersionUID = 2895933414446455756L;
    protected T id;

}
