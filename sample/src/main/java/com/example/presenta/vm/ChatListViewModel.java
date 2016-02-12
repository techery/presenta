package com.example.presenta.vm;

import android.content.Context;
import android.databinding.BaseObservable;

import com.example.presenta.DaggerMortarDemoActivity_Component;
import com.example.presenta.MortarDemoApplication;
import com.example.presenta.model.Chat;
import com.example.presenta.model.Chats;

import java.util.List;

import javax.inject.Inject;

public class ChatListViewModel extends BaseObservable {
    @Inject
    Chats chats;

    private List<Chat> chatList;

    private Callback callback;

    public ChatListViewModel(Context context, Callback callback) {
        this.callback = callback;

        //init di
        DaggerMortarDemoActivity_Component.builder()
                .appComponent(((MortarDemoApplication) context.getApplicationContext()).getAppComponent())
                .build()
                .inject(this);
    }

    public void onLoad() {
        chatList = chats.getAll();
        callback.onDataReceived(chatList);
    }

    public interface Callback {
        void onDataReceived(List<Chat> chatList);

        void onError(Throwable throwable);
    }
}
