package com.example.mortar.mortarscreen;

import android.content.Context;
import android.view.View;

import com.example.mortar.core.ScreenComponent;

import mortar.ViewPresenter;

public class BasePresenter<V extends View> extends ViewPresenter<V> {

  public static final String SERVICE_NAME = "base_presenter";
  protected ScreenComponent screenComponent;

  public BasePresenter(ScreenComponent screenComponent) {
    this.screenComponent = screenComponent;
  }

  @SuppressWarnings("unchecked")
  public static <T extends BasePresenter> T getPresenter(Context context) {
    return (T) context.getSystemService(SERVICE_NAME);
  }
}
