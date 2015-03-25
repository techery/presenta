package com.techery.presenta.mortarscreen.presenter;

import android.content.Context;

import com.techery.presenta.mortarscreen.ServiceFactory;
import com.techery.presenta.mortarscreen.ServiceFactoryProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import flow.Path;
import mortar.dagger2support.DaggerService;

import static java.lang.String.format;

/**
 * Provides factory for {@link InjectablePresenter} based on path's {@link WithPresenter} annotation.
 * @see InjectablePresenterFactory
 * @see SimplePresenterFactory
 */
public class PresenterServiceFactoryProvider implements ServiceFactoryProvider<Path> {

  private final Map<Class, ServiceFactory> presenterFactoryCache = new LinkedHashMap<>();

  @Override
  public ServiceFactory getServiceFactory(Path path) {
    Class<?> screenType = path.getClass();
    ServiceFactory presenterFactory = presenterFactoryCache.get(screenType);
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
        Class<? extends ServiceFactory> mfClass = withPresenterFactory.value();

        try {
          presenterFactory = mfClass.newInstance();
        } catch (Exception e) {
          throw new RuntimeException(format("Failed to instantiate module factory %s for path %s",
              withPresenterFactory.value().getName(), path), e);
        }
      }
    }

    if (presenterFactory != null) presenterFactoryCache.put(screenType, presenterFactory);
    return presenterFactory;
  }

  private static class SimplePresenterFactory extends ServiceFactory.BaseServiceFactory {

    private SimplePresenterFactory(Class presenterClass) {
      super(presenterClass);
    }

    @Override
    public Object getService(Context context, Path screen) {
      try {
        Constructor constructor;
        if (Modifier.isStatic(serviceClass.getModifiers())) {
          constructor = serviceClass.getDeclaredConstructor();
          return constructor.newInstance();
        } else {
          constructor = serviceClass.getDeclaredConstructor(screen.getClass());
          return constructor.newInstance(screen);
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
      return null;
    }
  }

  private static class InjectablePresenterFactory extends ServiceFactory.BaseServiceFactory {

    public InjectablePresenterFactory(Class presenterClass) {
      super(presenterClass);
    }

    @Override
    public Object getService(Context context, Path screen) {
      final Object screenComponent = DaggerService.getDaggerComponent(context);
      InjectablePresenter.PresenterInjector injector = new ComponentInjector(serviceClass, screenComponent);
      try {
        Constructor constructor;
        if (Modifier.isStatic(serviceClass.getModifiers())) {
          constructor = serviceClass.getDeclaredConstructor(InjectablePresenter.PresenterInjector.class);
          return constructor.newInstance(injector);
        } else {
          constructor = serviceClass.getDeclaredConstructor(screen.getClass(), InjectablePresenter.PresenterInjector.class);
          return constructor.newInstance(screen, injector);
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
      return null;
    }

    private static class ComponentInjector implements InjectablePresenter.PresenterInjector {

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
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
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
