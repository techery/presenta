package com.techery.presenta.mortarscreen;

public interface ServiceFactoryProvider<T> {

  public ServiceFactory getServiceFactory(T object);
}
