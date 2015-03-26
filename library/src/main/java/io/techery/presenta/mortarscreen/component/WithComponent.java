package io.techery.presenta.mortarscreen.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a screen as defining a {@link mortar.MortarScope}, with the class of a Dagger module
 * to instantiate via reflection. The module must be a static type with a default
 * constructor. For more flexibility, use {@link WithComponentFactory}.
 *
 * @see ComponentServiceFactoryProvider
 */
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface WithComponent {
  Class<?> value();
}
