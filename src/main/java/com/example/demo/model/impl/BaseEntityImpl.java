/**
 * Copyright TowerIQ 2018
 */
package com.example.demo.model.impl;

import com.example.demo.model.api.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntityImpl implements Entity {
    protected Date created;
}
