package com.example.presenta.di;

import io.techery.presenta.addition.ActionBarOwner;
import io.techery.presenta.di.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class ActionBarModule {
  @Provides
  @ApplicationScope
  ActionBarOwner provideActionBarOwner() {
    return new ActionBarOwner();
  }
}
