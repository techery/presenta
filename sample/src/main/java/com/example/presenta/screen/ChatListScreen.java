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
import com.example.presenta.R;
import com.example.presenta.model.Chat;
import com.example.presenta.model.Chats;
import com.example.presenta.view.ChatListView;
import flow.Flow;
import flow.path.Path;
import io.techery.presenta.addition.flow.path.Layout;
import io.techery.presenta.mortarscreen.presenter.InjectablePresenter;
import io.techery.presenta.mortarscreen.presenter.WithPresenter;
import java.util.List;
import javax.inject.Inject;

@Layout(R.layout.chat_list_view) @WithPresenter(ChatListScreen.Presenter.class)
public class ChatListScreen extends Path {

  public static class Presenter extends InjectablePresenter<ChatListView> {

    @Inject Chats chats;
    List<Chat> chatList;

    public Presenter(PresenterInjector injector) {
      super(injector);
      this.chatList = chats.getAll();
    }

    @Override public void onLoad(Bundle savedInstanceState) {
      super.onLoad(savedInstanceState);
      if (!hasView()) return;
      getView().showConversations(chatList);
    }

    public void onConversationSelected(int position) {
      Flow.get(getView()).set(new ChatScreen(position));
    }
  }
}
