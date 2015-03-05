package com.example.mortar.mortarscreen;

import android.content.Context;

/** @see WithPresenterFactory */
public abstract class PresenterFactory<T> {
  protected abstract Object createPresenter(Context context, T screen);
}
