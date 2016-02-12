package com.example.presenta.vm;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.example.presenta.model.Chat;

public class ChatItemViewModel extends BaseObservable {
    private Chat chat;

    public ChatItemViewModel(Chat chat) {
        this.chat = chat;
    }

    public View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    @Bindable
    public String getId() {
        return String.valueOf(chat.getId());
    }

    @Bindable
    public String getName() {
        return chat.toString();
    }

    @Bindable
    public String getFirstMessageBody() {
        return chat.toString();
    }
}
