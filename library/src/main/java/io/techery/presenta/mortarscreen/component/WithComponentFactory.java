package io.techery.presenta.mortarscreen.component;

import io.techery.presenta.mortarscreen.ScreenScoper;
import io.techery.presenta.mortarscreen.ServiceFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a screen as defining a {@link mortar.MortarScope}, with a factory class to
 * create its Dagger module.
 *
 * @see WithComponent
 * @see ScreenScoper
 */
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface WithComponentFactory {
  Class<? extends ServiceFactory> value();
}
