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
import com.example.mortar.core.MortarDemoApplication;
import com.example.mortar.model.Chat;
import com.example.mortar.model.Chats;
import com.example.mortar.mortarscreen.ScreenScope;
import com.example.mortar.mortarscreen.WithComponent;
import com.example.mortar.view.ChatListView;

import java.util.List;

import javax.inject.Inject;

import dagger.Provides;
import flow.Flow;
import flow.Layout;
import flow.Path;
import mortar.ViewPresenter;

import static com.example.mortar.screen.ChatListScreen.Component.Module;

@Layout(R.layout.chat_list_view) @WithComponent(ChatListScreen.Component.class)
public class ChatListScreen extends Path {

  @ScreenScope
  @dagger.Component(dependencies = MortarDemoApplication.AppComponent.class, modules = Module.class)
  public interface Component {

    void inject(ChatListView view);

    @dagger.Module(library = true, complete = false)
    public static class Module {
      @Provides
      @ScreenScope List<Chat> provideConversations(Chats chats) {
        return chats.getAll();
      }
    }

  }

  @ScreenScope
  public static class Presenter extends ViewPresenter<ChatListView> {
    private final List<Chat> chats;

    @Inject Presenter(List<Chat> chats) {
      this.chats = chats;
    }

    @Override public void onLoad(Bundle savedInstanceState) {
      super.onLoad(savedInstanceState);
      if (!hasView()) return;
      getView().showConversations(chats);
    }

    public void onConversationSelected(int position) {
      Flow.get(getView()).goTo(new ChatScreen(position));
    }
  }
}
