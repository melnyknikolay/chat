/**
 * Copyright TowerIQ 2018
 */
package com.example.demo.model.api.traits;


import com.example.demo.model.api.Entity;

/**
 * Used to identify all {@link Entity}(s) that can be activated and deactivated.
 */
public interface Activatable
{	
	/**
	 * @return true if active and false otherwise
	 */
	boolean isActive();
}
