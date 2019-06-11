/**
 * Copyright TowerIQ 2018
 */
package com.example.demo.model.api;

import java.io.Serializable;
import java.util.Date;

/**
 * Super interface for all this entities
 */
public interface Entity extends Serializable {
    Date getCreated();
}
