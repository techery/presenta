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
import com.example.presenta.model.Chats;
import com.example.presenta.model.Message;
import com.example.presenta.view.MessageView;
import io.techery.presenta.mortarscreen.presenter.InjectablePresenter;
import io.techery.presenta.mortarscreen.presenter.WithPresenter;

import javax.inject.Inject;

import flow.Flow;
import flow.HasParent;
import flow.Layout;
import flow.Path;
import rx.Observable;
import rx.functions.Action1;

@Layout(R.layout.message_view) @WithPresenter(MessageScreen.Presenter.class)
public class MessageScreen extends Path implements HasParent {
  private final int chatId;
  private final int messageId;

  public MessageScreen(int chatId, int messageId) {
    this.chatId = chatId;
    this.messageId = messageId;
  }

  @Override public ChatScreen getParent() {
    return new ChatScreen(chatId);
  }

  public class Presenter extends InjectablePresenter<MessageView> {
    private final Observable<Message> messageSource;
    private Message message;
    @Inject Chats service;

    public Presenter(PresenterInjector injector) {
      super(injector);
      this.messageSource = service.getChat(chatId).getMessage(messageId);
    }

    @Override public void onLoad(Bundle savedInstanceState) {
      super.onLoad(savedInstanceState);
      if (!hasView()) return;

      messageSource.subscribe(new Action1<Message>() {
        @Override public void call(Message message) {
          if (!hasView()) return;
          Presenter.this.message = message;
          MessageView view = getView();
          view.setUser(message.from.name);
          view.setMessage(message.body);
        }
      });
    }

    public void onUserSelected() {
      if (message == null) return;
      int position = message.from.id;
      if (position != -1) {
        Flow.get(getView()).goTo(new FriendScreen(position));
      }
    }
  }
}


