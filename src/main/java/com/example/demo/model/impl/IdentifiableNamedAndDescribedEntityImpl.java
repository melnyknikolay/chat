/**
 * Copyright TowerIQ 2018
 */
package com.example.demo.model.impl;

import com.example.demo.model.api.Entity;
import com.example.demo.model.api.traits.Described;
import com.example.demo.model.api.traits.Identifiable;
import com.example.demo.model.api.traits.Named;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @see Identifiable
 * @see Described
 * @see Named
 * @see Entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentifiableNamedAndDescribedEntityImpl<T, X, Y> extends IdentifiableNamedEntityImpl<T, X>
        implements Described<Y> {
    protected Y description;
}
