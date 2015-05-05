package com.example.presenta.flow;

/**
 * Describes the parent of a specific screen which is used to support the up affordance.
 * Implementing screens are required to be able to return an instance of their parent.
 */
public interface HasParent<T> {
  T getParent();
}