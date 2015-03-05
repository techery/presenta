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
package com.example.mortar.screen;

import android.os.Bundle;

import com.example.mortar.R;
import com.example.mortar.model.Chats;
import com.example.mortar.model.User;
import com.example.mortar.mortarscreen.ScreenScope;
import com.example.mortar.mortarscreen.WithComponent;
import com.example.mortar.view.FriendListView;

import java.util.List;

import javax.inject.Inject;

import dagger.Provides;
import flow.Flow;
import flow.HasParent;
import flow.Layout;
import flow.Path;
import mortar.ViewPresenter;

import static com.example.mortar.core.MortarDemoApplication.AppComponent;

@Layout(R.layout.friend_list_view) @WithComponent(FriendListScreen.Component.class)
public class FriendListScreen extends Path implements HasParent {

  @ScreenScope
  @dagger.Component(dependencies = AppComponent.class, modules = Module.class)
  public interface Component {
    void inject(FriendListView view);
  }


  @dagger.Module
  public static class Module {
    @Provides List<User> provideFriends(Chats chats) {
      return chats.getFriends();
    }
  }

  @ScreenScope
  public static class Presenter extends ViewPresenter<FriendListView> {
    private final List<User> friends;

    @Inject Presenter(List<User> friends) {
      this.friends = friends;
    }

    @Override public void onLoad(Bundle savedInstanceState) {
      super.onLoad(savedInstanceState);
      if (!hasView()) return;
      getView().showFriends(friends);
    }

    public void onFriendSelected(int position) {
      Flow.get(getView()).goTo(new FriendScreen(position));
    }
  }

  @Override public ChatListScreen getParent() {
    return new ChatListScreen();
  }
}
