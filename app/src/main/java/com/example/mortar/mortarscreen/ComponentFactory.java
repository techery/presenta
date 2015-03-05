package com.example.mortar.mortarscreen;

import android.content.Context;

/** @see WithComponentFactory */
public abstract class ComponentFactory<T> {
  protected abstract Object createDaggerComponent(Context context, T screen);
}
