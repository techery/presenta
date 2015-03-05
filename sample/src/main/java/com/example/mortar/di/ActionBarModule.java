package com.example.mortar.di;

import com.techery.addition.ActionBarOwner;
import com.techery.presenta.di.ApplicationScope;

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
