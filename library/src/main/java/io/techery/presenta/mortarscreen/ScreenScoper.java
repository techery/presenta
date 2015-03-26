package io.techery.presenta.mortarscreen;

import android.content.Context;
import android.util.Log;

import io.techery.presenta.mortar.PresenterService;
import io.techery.presenta.mortarscreen.component.ComponentServiceFactoryProvider;
import io.techery.presenta.mortarscreen.presenter.PresenterServiceFactoryProvider;

import flow.Path;
import io.techery.presenta.mortarscreen.component.WithComponent;
import io.techery.presenta.mortarscreen.component.WithComponentFactory;
import io.techery.presenta.mortarscreen.presenter.WithPresenter;
import io.techery.presenta.mortarscreen.presenter.WithPresenterFactory;
import mortar.MortarScope;
import mortar.dagger2support.DaggerService;

/**
 * Creates {@link MortarScope}s for screens that may be annotated with {@link WithPresenterFactory},
 * {@link WithPresenter}, {@link WithComponent}, {@link WithComponentFactory}.
 */
public class ScreenScoper {

  static final String TAG = ScreenScoper.class.getSimpleName();

  PresenterServiceFactoryProvider presenterServiceFactory = new PresenterServiceFactoryProvider();
  ComponentServiceFactoryProvider componentServiceFactory = new ComponentServiceFactoryProvider();

  /**
   * Finds or creates the scope for the given path, honoring its optional {@link
   * WithPresenterFactory} or {@link WithPresenter}, {@link WithComponent}, {@link WithComponentFactory} annotation.
   * Note that scopes are also created for unannotated screens.
   */
  public MortarScope getScreenScope(Context context, String name, Path path) {
    MortarScope parentScope = MortarScope.getScope(context);
    MortarScope childScope = parentScope.findChild(name);
    if (childScope != null) return childScope;

    MortarScope.Builder builder = parentScope.buildChild();

    ServiceFactory serviceFactory;
    serviceFactory = presenterServiceFactory.getServiceFactory(path);
    if (serviceFactory != null) {
      Object presenter = serviceFactory.getService(context, path);
      return builder.withService(PresenterService.SERVICE_NAME, presenter).build(name);
    }
    serviceFactory = componentServiceFactory.getServiceFactory(path);
    if (serviceFactory != null) {
      Object component = serviceFactory.getService(context, path);
      return builder.withService(DaggerService.SERVICE_NAME, component).build(name);
    }
    Log.w(TAG, "Path " + path + " has no additional service in mortar context");
    return builder.build(name);

  }

}
