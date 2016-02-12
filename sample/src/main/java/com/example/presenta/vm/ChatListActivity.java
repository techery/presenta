package com.example.presenta.vm;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import com.example.presenta.R;
import com.example.presenta.databinding.ActivityChatListBinding;
import com.example.presenta.model.Chat;

import java.util.List;

public class ChatListActivity extends ActionBarActivity implements ChatListViewModel.Callback {
    private ChatListViewModel viewModel;

    private ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ChatListViewModel(this, this);
        adapter = new ChatListAdapter();

        ActivityChatListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_list);
        binding.setViewModel(viewModel);

        binding.chatList.setLayoutManager(new LinearLayoutManager(this));
        binding.chatList.setHasFixedSize(true);
        binding.chatList.setAdapter(adapter);
        binding.chatList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        viewModel.onLoad();
    }

    @Override
    public void onDataReceived(List<Chat> chatList) {
        adapter.setItems(chatList);
    }

    @Override
    public void onError(Throwable throwable) {

    }
}