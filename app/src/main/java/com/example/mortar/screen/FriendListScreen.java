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
import com.example.mortar.core.ScreenComponent;
import com.example.mortar.model.Chats;
import com.example.mortar.model.User;
import com.example.mortar.mortarscreen.BasePresenter;
import com.example.mortar.mortarscreen.WithPresenter;
import com.example.mortar.view.FriendListView;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.HasParent;
import flow.Layout;
import flow.Path;

@Layout(R.layout.friend_list_view) @WithPresenter(FriendListScreen.Presenter.class)
public class FriendListScreen extends Path implements HasParent {

  public static class Presenter extends BasePresenter<FriendListView> {
    List<User> friends;
    @Inject Chats service;

    public Presenter(ScreenComponent component) {
      super(component);
      component.inject(this);
      this.friends = service.getFriends();
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
