package io.techery.presenta.mortarscreen;

/**
 * Provides factory of service factories to be linked with mortar scope.
 * @param <T>
 */
public interface ServiceFactoryProvider<T> {

  public ServiceFactory getServiceFactory(T object);
}
