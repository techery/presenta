package com.techery.presenta.mortar;

import android.content.Context;

import com.techery.presenta.mortarscreen.presenter.InjectablePresenter;


public class PresenterService {
  public static final String SERVICE_NAME = "base_presenter";

  @SuppressWarnings("unchecked")
  public static <T extends InjectablePresenter> T getPresenter(Context context) {
    return (T) context.getSystemService(SERVICE_NAME);
  }
}