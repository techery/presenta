package com.example.mortar.core;

import com.example.mortar.screen.ChatListScreen;
import com.example.mortar.screen.ChatScreen;
import com.example.mortar.screen.FriendListScreen;
import com.example.mortar.screen.FriendScreen;
import com.example.mortar.screen.MessageScreen;

public interface ScreenComponent {
  void inject(ChatScreen.Presenter presenter);

  void inject(ChatListScreen.Presenter presenter);

  void inject(FriendListScreen.Presenter presenter);

  void inject(FriendScreen.Presenter presenter);

  void inject(MessageScreen.Presenter presenter);
}
