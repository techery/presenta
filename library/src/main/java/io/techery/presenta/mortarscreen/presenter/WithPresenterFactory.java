package io.techery.presenta.mortarscreen.presenter;

import io.techery.presenta.mortarscreen.ScreenScoper;
import io.techery.presenta.mortarscreen.ServiceFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.techery.presenta.mortarscreen.component.WithComponent;
import mortar.MortarScope;

/**
 * Marks a screen as defining a {@link MortarScope}, with a factory class to
 * create its Dagger module.
 *
 * @see WithComponent
 * @see ScreenScoper
 */
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface WithPresenterFactory {
  Class<? extends ServiceFactory> value();
}
