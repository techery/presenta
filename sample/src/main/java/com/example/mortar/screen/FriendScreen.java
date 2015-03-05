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
import com.example.mortar.view.FriendView;
import com.techery.presenta.mortarscreen.InjectablePresenter;
import com.techery.presenta.mortarscreen.WithPresenter;

import javax.inject.Inject;

import flow.HasParent;
import flow.Layout;
import flow.Path;

@Layout(R.layout.friend_view) @WithPresenter(FriendScreen.Presenter.class)
public class FriendScreen extends Path implements HasParent {
  private final int index;

  public FriendScreen(int index) {
    this.index = index;
  }

  @Override public FriendListScreen getParent() {
    return new FriendListScreen();
  }

  public class Presenter extends InjectablePresenter<FriendView> {
    @Inject Chats service;
    private final User friend;

    public Presenter(PresenterInjector injector) {
      super(injector);
      this.friend = service.getFriend(index);
    }

    @Override public void onLoad(Bundle savedInstanceState) {
      super.onLoad(savedInstanceState);
      if (!hasView()) return;
      getView().setFriend(friend.name);
    }
  }
}
