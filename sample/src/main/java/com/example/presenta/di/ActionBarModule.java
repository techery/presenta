package com.example.presenta.di;

import io.techery.presenta.addition.ActionBarOwner;
import io.techery.presenta.di.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ActionBarModule {
  @Provides
  @ApplicationScope
  ActionBarOwner provideActionBarOwner() {
    return new ActionBarOwner();
  }
}
