/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.mortar;

import android.app.Application;

import com.example.mortar.di.RootModule;
import com.example.mortar.model.Chats;
import com.techery.addition.ActionBarOwner;
import com.techery.presenta.di.ApplicationScope;

import dagger.Component;
import mortar.MortarScope;
import mortar.dagger2support.DaggerService;

public class MortarDemoApplication extends Application {

  @ApplicationScope
  @Component(modules =  RootModule.class)
  public interface AppComponent {
    void inject(MortarDemoApplication application);
    ActionBarOwner actionBarOwner();
    Chats chats();
  }

  private MortarScope rootScope;

  @Override public void onCreate() {
    super.onCreate();
    instance = this;

    AppComponent component = DaggerService.createComponent(AppComponent.class);
    rootScope = MortarScope.buildRootScope()
        .withService(DaggerService.SERVICE_NAME, component)
        .build();
    component.inject(this);
  }

  public static MortarDemoApplication instance() {
    return instance;
  }

  private static MortarDemoApplication instance;

  @Override public Object getSystemService(String name) {
    Object mortarService = rootScope.getService(name);
    if (mortarService != null) return mortarService;

    return super.getSystemService(name);
  }
}
