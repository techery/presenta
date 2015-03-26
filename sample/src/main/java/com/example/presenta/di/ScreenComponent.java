package com.example.presenta.di;

import com.example.presenta.screen.ChatListScreen;
import com.example.presenta.screen.ChatScreen;
import com.example.presenta.screen.FriendListScreen;
import com.example.presenta.screen.FriendScreen;
import com.example.presenta.screen.MessageScreen;

public interface ScreenComponent {
  void inject(ChatScreen.Presenter presenter);

  void inject(ChatListScreen.Presenter presenter);

  void inject(FriendListScreen.Presenter presenter);

  void inject(FriendScreen.Presenter presenter);

  void inject(MessageScreen.Presenter presenter);
}
