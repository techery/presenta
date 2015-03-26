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
package com.example.presenta.screen;

import android.os.Bundle;

import com.example.presenta.MortarDemoActivity;
import com.example.presenta.R;
import com.example.presenta.model.Chats;
import com.example.presenta.model.User;
import com.example.presenta.view.FriendView;
import io.techery.presenta.di.ScreenScope;
import io.techery.presenta.mortarscreen.component.WithComponent;

import javax.inject.Inject;

import dagger.Provides;
import flow.HasParent;
import flow.Layout;
import flow.Path;
import mortar.ViewPresenter;

@Layout(R.layout.friend_view) @WithComponent(FriendScreen.Component.class)
public class FriendScreen extends Path implements HasParent {
  private final int index;

  public FriendScreen(int index) {
    this.index = index;
  }

  @Override public FriendListScreen getParent() {
    return new FriendListScreen();
  }

  @ScreenScope(FriendScreen.class)
  @dagger.Component(dependencies = MortarDemoActivity.Component.class, modules = Module.class)
  public static interface Component{
    void inject(FriendView view);
  }

  @dagger.Module
  public class Module {
    @Provides User provideFriend(Chats chats) {
      return chats.getFriend(index);
    }
  }

  @ScreenScope(FriendScreen.class)
  public static class Presenter extends ViewPresenter<FriendView> {
    private final User friend;

    @Inject
    public Presenter(User friend) {
      this.friend = friend;
    }

    @Override public void onLoad(Bundle savedInstanceState) {
      super.onLoad(savedInstanceState);
      if (!hasView()) return;
      getView().setFriend(friend.name);
    }
  }
}
