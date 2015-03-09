package com.techery.presenta.mortarscreen;

import android.content.Context;

import flow.Path;

public interface ServiceFactory<T> {

  public Object getService(Context context, T screen);

  static final ServiceFactory NO_FACTORY = new ServiceFactory() {
    @Override
    public Object getService(Context context, Object screen) {
      throw new UnsupportedOperationException();
    }
  };

  static abstract class BaseServiceFactory implements ServiceFactory<Path> {

    protected final Class serviceClass;
    public BaseServiceFactory(Class serviceClass) {
      this.serviceClass = serviceClass;
    }

  }
}
