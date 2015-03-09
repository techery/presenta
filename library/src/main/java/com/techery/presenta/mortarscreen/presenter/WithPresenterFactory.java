package com.techery.presenta.mortarscreen.presenter;

import com.techery.presenta.mortarscreen.ServiceFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mortar.MortarScope;

/**
 * Marks a screen as defining a {@link MortarScope}, with a factory class to
 * create its Dagger module.
 *
 * @see com.techery.presenta.mortarscreen.component.WithComponent
 * @see com.techery.presenta.mortarscreen.ScreenScoper
 */
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface WithPresenterFactory {
  Class<? extends ServiceFactory> value();
}
