package com.techery.presenta.mortarscreen;

import android.content.Context;

/** @see WithPresenterFactory */
public abstract class PresenterFactory<T> {
  protected abstract Object createPresenter(Context context, T screen);
}
