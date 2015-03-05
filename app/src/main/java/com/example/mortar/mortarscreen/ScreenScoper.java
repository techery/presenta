package com.example.mortar.mortarscreen;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import dagger.Module;
import flow.Path;
import mortar.MortarScope;
import mortar.dagger2support.DaggerService;

import static java.lang.String.format;

/**
 * Creates {@link MortarScope}s for screens that may be annotated with {@link WithComponentFactory},
 * {@link WithComponent}.
 */
public class ScreenScoper {

  private static final ComponentFactory NO_FACTORY = new ComponentFactory() {
    @Override protected Object createDaggerComponent(Context context, Object screen) {
      throw new UnsupportedOperationException();
    }
  };

  private final Map<Class, ComponentFactory> componentFactoryCache = new LinkedHashMap<>();

  public MortarScope getScreenScope(Context context, String name, Object screen) {
    MortarScope parentScope = MortarScope.getScope(context);
    return getScreenScope(context, parentScope, name, screen);
  }

  /**
   * Finds or creates the scope for the given screen, honoring its optional {@link
   * WithComponentFactory} or {@link WithComponent} annotation. Note that scopes are also created
   * for unannotated screens.
   */
  public MortarScope getScreenScope(Context context, MortarScope parentScope, final String name, final Object screen) {
    MortarScope childScope = parentScope.findChild(name);
    if (childScope != null) return childScope;

    ComponentFactory componentFactory = getComponentFactory(screen);
    Object childComponent;
    if (componentFactory != NO_FACTORY) {
      childComponent = componentFactory.createDaggerComponent(context, screen);
    } else {
      // We need every screen to have a scope, so that anything it injects is scoped.  We need
      // this even if the screen doesn't declare a module, because Dagger allows injection of
      // objects that are annotated even if they don't appear in a module.
      childComponent = null;
    }

    childScope = parentScope
        .buildChild(name)
        .withService(DaggerService.SERVICE_NAME, childComponent)
        .build();

    return childScope;
  }

  private ComponentFactory getComponentFactory(Object screen) {
    Class<?> screenType = screen.getClass();
    ComponentFactory componentFactory = componentFactoryCache.get(screenType);
    if (componentFactory != null) return componentFactory;

    WithComponent withComponent = screenType.getAnnotation(WithComponent.class);
    if (withComponent != null) {
      Class<?> componentClass = withComponent.value();
      componentFactory = new SimpleComponentFactory(componentClass);
    }

    if (componentFactory == null) {
      WithComponentFactory withComponentFactory = screenType.getAnnotation(WithComponentFactory.class);
      if (withComponentFactory != null) {
        Class<? extends ComponentFactory> mfClass = withComponentFactory.value();

        try {
          componentFactory = mfClass.newInstance();
        } catch (Exception e) {
          throw new RuntimeException(format("Failed to instantiate module factory %s for screen %s",
              withComponentFactory.value().getName(), screen), e);
        }
      }
    }

    if (componentFactory == null) componentFactory = NO_FACTORY;

    componentFactoryCache.put(screenType, componentFactory);

    return componentFactory;
  }

  private static class SimpleComponentFactory extends ComponentFactory<Path> {
    private final Class compClass;

    private SimpleComponentFactory(Class compClass) {
      this.compClass = compClass;
    }

    @Override protected Object createDaggerComponent(Context context, Path screen) {
      Object depComponent = DaggerService.getDaggerComponent(context);
      Object depModule = null;
      // Find and instantiate inner module if any
      for (Class innerClass : screen.getClass().getClasses()) {
        if (Modifier.isStatic(screen.getClass().getDeclaredClasses()[1].getModifiers())) continue;
        if (innerClass.getAnnotation(Module.class) != null) {
          try {
            Constructor constructor = innerClass.getDeclaredConstructor(screen.getClass());
            depModule = constructor.newInstance(screen);
          } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
          }
          break;
        }
      }
      if (depModule == null) {
        return DaggerService.createComponent(compClass, depComponent);
      } else {
        return DaggerService.createComponent(compClass, depComponent, depModule);
      }
    }
  }

}
