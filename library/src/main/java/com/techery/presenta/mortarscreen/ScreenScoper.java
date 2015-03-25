package com.techery.presenta.mortarscreen;

import android.content.Context;
import android.util.Log;

import com.techery.presenta.mortar.PresenterService;
import com.techery.presenta.mortarscreen.component.ComponentServiceFactoryProvider;
import com.techery.presenta.mortarscreen.presenter.PresenterServiceFactoryProvider;

import flow.Path;
import mortar.MortarScope;
import mortar.dagger2support.DaggerService;

/**
 * Creates {@link MortarScope}s for screens that may be annotated with {@link com.techery.presenta.mortarscreen.presenter.WithPresenterFactory},
 * {@link com.techery.presenta.mortarscreen.presenter.WithPresenter}, {@link com.techery.presenta.mortarscreen.component.WithComponent}, {@link com.techery.presenta.mortarscreen.component.WithComponentFactory}.
 */
public class ScreenScoper {

  static final String TAG = ScreenScoper.class.getSimpleName();

  PresenterServiceFactoryProvider presenterServiceFactory = new PresenterServiceFactoryProvider();
  ComponentServiceFactoryProvider componentServiceFactory = new ComponentServiceFactoryProvider();

  /**
   * Finds or creates the scope for the given path, honoring its optional {@link
   * com.techery.presenta.mortarscreen.presenter.WithPresenterFactory} or {@link com.techery.presenta.mortarscreen.presenter.WithPresenter}, {@link com.techery.presenta.mortarscreen.component.WithComponent}, {@link com.techery.presenta.mortarscreen.component.WithComponentFactory} annotation.
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
