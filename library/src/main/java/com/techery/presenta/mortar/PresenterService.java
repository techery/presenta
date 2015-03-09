package com.techery.presenta.mortar;

import android.content.Context;

import com.techery.presenta.mortarscreen.presenter.InjectablePresenter;

/**
 * Mortar service to find an {@link InjectablePresenter} for a context scope.
 */
public class PresenterService {
  public static final String SERVICE_NAME = "base_presenter";

  @SuppressWarnings("unchecked")
  public static <T extends InjectablePresenter> T getPresenter(Context context) {
    return (T) context.getSystemService(SERVICE_NAME);
  }
}