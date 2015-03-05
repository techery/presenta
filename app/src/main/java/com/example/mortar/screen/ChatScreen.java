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
import android.util.Log;

import com.example.mortar.R;
import com.example.mortar.android.ActionBarOwner;
import com.example.mortar.core.ScreenComponent;
import com.example.mortar.model.Chat;
import com.example.mortar.model.Chats;
import com.example.mortar.model.Message;
import com.example.mortar.mortarscreen.BasePresenter;
import com.example.mortar.mortarscreen.WithPresenter;
import com.example.mortar.view.ChatView;
import com.example.mortar.view.Confirmation;

import javax.inject.Inject;

import flow.Flow;
import flow.HasParent;
import flow.Layout;
import flow.Path;
import mortar.PopupPresenter;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

@Layout(R.layout.chat_view) @WithPresenter(ChatScreen.Presenter.class)
public class ChatScreen extends Path implements HasParent {

  private final int conversationIndex; // available in presenter as outer class field

  public ChatScreen(int conversationIndex) {
    this.conversationIndex = conversationIndex;
  }

  @Override public ChatListScreen getParent() {
    return new ChatListScreen();
  }

  ///////////////////////////////////////////////////////////////////////////
  // Presenter
  ///////////////////////////////////////////////////////////////////////////

  public class Presenter extends BasePresenter<ChatView> {
    @Inject Chats chats;
    @Inject ActionBarOwner actionBar;
    private final Chat chat;
    private final PopupPresenter<Confirmation, Boolean> confirmer;
    private Subscription running = Subscriptions.empty();

    public Presenter(ScreenComponent screenComponent) {
      super(screenComponent);
      this.screenComponent.inject(this);
      this.chat = chats.getChat(conversationIndex);
      this.confirmer = new PopupPresenter<Confirmation, Boolean>() {
        @Override protected void onPopupResult(Boolean confirmed) {
          if (confirmed) Presenter.this.getView().toast("Haven't implemented that, friend.");
        }
      };
    }

    @Override public void dropView(ChatView view) {
      confirmer.dropView(view.getConfirmerPopup());
      super.dropView(view);
    }

    @Override public void onLoad(Bundle savedInstanceState) {
      if (!hasView()) return;

      ActionBarOwner.Config actionBarConfig = actionBar.getConfig();

      actionBarConfig =
          actionBarConfig.withAction(new ActionBarOwner.MenuAction("End", new Action0() {
            @Override public void call() {
              confirmer.show(
                  new Confirmation("End Chat", "Do you really want to leave this chat?", "Yes",
                      "I guess not"));
            }
          }));

      actionBar.setConfig(actionBarConfig);

      confirmer.takeView(getView().getConfirmerPopup());

      running = chat.getMessages().subscribe(new Observer<Message>() {
        @Override public void onCompleted() {
          Log.w(getClass().getName(), "That's surprising, never thought this should end.");
          running = null;
        }

        @Override public void onError(Throwable e) {
          Log.w(getClass().getName(), "'sploded, will try again on next config change.");
          Log.w(getClass().getName(), e);
          running = null;
        }

        @Override public void onNext(Message message) {
              if (!hasView()) return;
              getView().getItems().add(message);
        }
      });
    }

    @Override protected void onExitScope() {
      ensureStopped();
    }

    public void onConversationSelected(int position) {
      Flow.get(getView().getContext()).goTo(new MessageScreen(chat.getId(), position));
    }

    public void visibilityChanged(boolean visible) {
      if (!visible) {
        ensureStopped();
      }
    }

    private void ensureStopped() {
      if (running != null) running.unsubscribe();
    }
  }
}
