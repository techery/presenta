package com.techery.presenta.mortarscreen;

import android.content.Context;

import com.techery.presenta.mortar.PresenterService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import flow.Path;
import mortar.MortarScope;
import mortar.dagger2support.DaggerService;

import static com.techery.presenta.mortarscreen.InjectablePresenter.PresenterInjector;
import static java.lang.String.format;

/**
 * Creates {@link MortarScope}s for screens that may be annotated with {@link WithPresenterFactory},
 * {@link WithPresenter}.
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
        .withService(PresenterService.SERVICE_NAME, presenter)
        .build();

    return childScope;
  }

  private PresenterFactory getPresenterFactory(Object screen) {
    Class<?> screenType = screen.getClass();
    PresenterFactory presenterFactory = componentFactoryCache.get(screenType);
    if (presenterFactory != null) return presenterFactory;

    WithPresenter withPresenter = screenType.getAnnotation(WithPresenter.class);
    if (withPresenter != null) {
      Class<?> presenterClass = withPresenter.value();
      if (InjectablePresenter.class.isAssignableFrom(presenterClass)) {
        presenterFactory = new InjectablePresenterFactory(presenterClass);
      } else {
        presenterFactory = new SimplePresenterFactory(presenterClass);
      }
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

  ///////////////////////////////////////////////////////////////////////////
  // Presenter factories
  ///////////////////////////////////////////////////////////////////////////

  private static abstract class BasePresenterFactory extends PresenterFactory<Path> {
    protected final Class presenterClass;

    public BasePresenterFactory(Class presenterClass) {
      this.presenterClass = presenterClass;
    }
  }

  private static class SimplePresenterFactory extends BasePresenterFactory {

    private SimplePresenterFactory(Class presenterClass) {
      super(presenterClass);
    }

    @Override
    protected Object createPresenter(Context context, Path screen) {
      try {
        Constructor constructor;
        if (Modifier.isStatic(presenterClass.getModifiers())) {
          constructor = presenterClass.getDeclaredConstructor();
          return constructor.newInstance();
        } else {
          constructor = presenterClass.getDeclaredConstructor(screen.getClass());
          return constructor.newInstance(screen);
        }
      } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
        e.printStackTrace();
      }
      return null;
    }
  }

  private static class InjectablePresenterFactory extends BasePresenterFactory {

    public InjectablePresenterFactory(Class presenterClass) {
      super(presenterClass);
    }

    @Override protected Object createPresenter(Context context, Path screen) {
      final Object screenComponent = DaggerService.getDaggerComponent(context);
      PresenterInjector injector = new ComponentInjector(presenterClass, screenComponent);
      try {
        Constructor constructor;
        if (Modifier.isStatic(presenterClass.getModifiers())) {
          constructor = presenterClass.getDeclaredConstructor(PresenterInjector.class);
          return constructor.newInstance(injector);
        } else{
          constructor = presenterClass.getDeclaredConstructor(screen.getClass(), PresenterInjector.class);
          return constructor.newInstance(screen, injector);
        }
      } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
        e.printStackTrace();
      }
      return null;
    }

    private static class ComponentInjector implements PresenterInjector {

      private static final Map<Class<InjectablePresenter>, Method> cache = new HashMap<>();
      private Class presenterClass;
      private Object screenComponent;

      private ComponentInjector(Class presenterClass, Object screenComponent) {
        this.presenterClass = presenterClass;
        this.screenComponent = screenComponent;
      }

      @Override
      public void inject(InjectablePresenter presenter) {
        try {
          Method injectableMethod = findInjectableMethod();
          injectableMethod.invoke(screenComponent, presenter);
        } catch (IllegalAccessException | InvocationTargetException e) {
          e.printStackTrace();
        } catch (NoSuchMethodException | NullPointerException e) {
          String detailMessage = "No graph method found to inject " + presenterClass.getSimpleName() + ". Check your component";
          NullPointerException exception = new NullPointerException(detailMessage);
          exception.setStackTrace(e.getStackTrace());
          throw exception;
        }
      }

      private Method findInjectableMethod() throws NoSuchMethodException {
        Method cachedMethod = cache.get(presenterClass);
        if (cachedMethod != null) {
          return screenComponent.getClass().getDeclaredMethod(cachedMethod.getName(), presenterClass);
        }
        // Find proper injectable method of component to inject presenter instance
        for (Method m : screenComponent.getClass().getDeclaredMethods()) {
          for (Class pClass : m.getParameterTypes()) {
            if (pClass.equals(presenterClass)) {
              cache.put(presenterClass, m);
              return m;
            }
          }
        }
        return null;
      }
    }
  }

}
