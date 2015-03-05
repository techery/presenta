package com.example.mortar.mortarscreen;

import android.content.Context;

import com.example.mortar.core.ScreenComponent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import flow.Path;
import mortar.MortarScope;
import mortar.dagger2support.DaggerService;

import static java.lang.String.format;

/**
 * Creates {@link MortarScope}s for screens that may be annotated with {@link WithPresenterFactory},
 * {@link WithComponent}.
 */
public class ScreenScoper {

  private static final PresenterFactory NO_FACTORY = new PresenterFactory() {
    @Override protected Object createPresenter(Context context, Object screen) {
      throw new UnsupportedOperationException();
    }
  };

  private final Map<Class, PresenterFactory> componentFactoryCache = new LinkedHashMap<>();

  public MortarScope getScreenScope(Context context, String name, Object screen) {
    MortarScope parentScope = MortarScope.getScope(context);
    return getScreenScope(context, parentScope, name, screen);
  }

  /**
   * Finds or creates the scope for the given screen, honoring its optional {@link
   * WithPresenterFactory} or {@link WithPresenter} annotation. Note that scopes are also created
   * for unannotated screens.
   */
  public MortarScope getScreenScope(Context context, MortarScope parentScope, final String name, final Object screen) {
    MortarScope childScope = parentScope.findChild(name);
    if (childScope != null) return childScope;

    PresenterFactory presenterFactory = getPresenterFactory(screen);
    Object presenter;
    if (presenterFactory != NO_FACTORY) {
      presenter = presenterFactory.createPresenter(context, screen);
    } else {
      // We need every screen to have a scope, so that anything it injects is scoped.  We need
      // this even if the screen doesn't declare a module, because Dagger allows injection of
      // objects that are annotated even if they don't appear in a module.
      presenter = null;
    }

    childScope = parentScope
        .buildChild(name)
        .withService(BasePresenter.SERVICE_NAME, presenter)
        .build();

    return childScope;
  }

  private PresenterFactory getPresenterFactory(Object screen) {
    Class<?> screenType = screen.getClass();
    PresenterFactory presenterFactory = componentFactoryCache.get(screenType);
    if (presenterFactory != null) return presenterFactory;

    WithPresenter withPresenter = screenType.getAnnotation(WithPresenter.class);
    if (withPresenter != null) {
      Class<?> presenterCalss = withPresenter.value();
      presenterFactory = new SimplePresenterFactory(presenterCalss);
    }

    if (presenterFactory == null) {
      WithPresenterFactory withPresenterFactory = screenType.getAnnotation(WithPresenterFactory.class);
      if (withPresenterFactory != null) {
        Class<? extends PresenterFactory> mfClass = withPresenterFactory.value();

        try {
          presenterFactory = mfClass.newInstance();
        } catch (Exception e) {
          throw new RuntimeException(format("Failed to instantiate module factory %s for screen %s",
              withPresenterFactory.value().getName(), screen), e);
        }
      }
    }

    if (presenterFactory == null) presenterFactory = NO_FACTORY;

    componentFactoryCache.put(screenType, presenterFactory);

    return presenterFactory;
  }

  private static class SimplePresenterFactory extends PresenterFactory<Path> {
    private final Class presenterClass;

    private SimplePresenterFactory(Class presenterClass) {
      this.presenterClass = presenterClass;
    }

    @Override protected Object createPresenter(Context context, Path screen) {
      ScreenComponent screenComponent = DaggerService.getDaggerComponent(context);
          try {
            Constructor constructor;
            if (Modifier.isStatic(presenterClass.getModifiers())) {
              constructor = presenterClass.getDeclaredConstructor(ScreenComponent.class);
              return constructor.newInstance(screenComponent);
            } else{
              constructor = presenterClass.getDeclaredConstructor(screen.getClass(), ScreenComponent.class);
              return constructor.newInstance(screen, screenComponent);
            }
          } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
          }
        return null;
    }
  }

}
