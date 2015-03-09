package com.techery.presenta.mortarscreen.presenter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a screen as defining a {@link mortar.MortarScope}, with the class of a Dagger module
 * to instantiate via reflection. The module must be a static type with a default
 * constructor. For more flexibility, use {@link WithPresenterFactory}.
 *
 * @see com.example.mortar.mortarscreen.ScreenScoper
 */
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface WithPresenter {
  Class<? extends InjectablePresenter> value();
}
