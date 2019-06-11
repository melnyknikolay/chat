/**
 * Copyright Copyright TowerIQ 2018
 */
package com.example.demo.model.api.traits;



/**
 * Defines all entities that have types
 */
public interface Typed<T>
{	
	/**
	 * @return T type
	 */
	T getType();
}
