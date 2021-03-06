package com.cutloose.cutloose.ui.chat;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cutloose.cutloose.R;
import com.cutloose.cutloose.databinding.ChatActivityBinding;
import com.cutloose.cutloose.model.Chat;
import com.cutloose.cutloose.model.Event;
import com.cutloose.cutloose.model.Profile;
import com.cutloose.cutloose.ui.chat.chat_users.ChatUsersFragment;
import com.cutloose.cutloose.ui.common.Action.Action;
import com.cutloose.cutloose.ui.common.BaseActivity;

import java.util.ArrayList;

public class ChatActivity extends BaseActivity {

    ChatActivityBinding mChatActivityBinding;
    public ChatActivityViewModel mChatActivityViewModel;
    ChatFragment chatFragment;
    ChatUsersFragment chatUsersFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.chat_activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        chatUsersFragment = (ChatUsersFragment) getSupportFragmentManager().findFragmentById(R.id.chat_users_fragment);

        mChatActivityViewModel = ViewModelProviders.of(this).get(ChatActivityViewModel.class);

        mChatActivityBinding = (ChatActivityBinding) mViewDataBinding;
        mChatActivityBinding.setViewModel(mChatActivityViewModel);

        Event event = getIntent().getParcelableExtra("event");
        setTitle(event.getName());

        if (getIntent().getStringExtra("chatId") != null) {
            String chatId = getIntent().getStringExtra("chatId");
            chatFragment.fetchData(event.getEventId(), chatId);
            mChatActivityViewModel.listenJoiningUsers(event.getEventId(), chatId);
            observeChatUsersChange();
        } else {

            mChatActivityViewModel.mSearching.set(true);
            mChatActivityViewModel.checkExistingLobbies(event);
            observeActions();
        }
    }

    private void observeChatUsersChange() {
        mChatActivityViewModel.getChatUsers().observe(this, new Observer<ArrayList<Profile>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Profile> profiles) {
                chatUsersFragment.getChatUsersRecyclerViewModel().getUsers().setValue(profiles);
            }
        });

    }

    private void observeActions() {
        mChatActivityViewModel.observeAction(this, new Observer<Action<Chat, LobbyAction>>() {
            @Override
            public void onChanged(@Nullable Action<Chat, LobbyAction> action) {

                if (action == null) return;

                switch( action.getActionType() ) {
                    case ON_CHAT_FOUND:
                        chatFragment.fetchData(mChatActivityViewModel.getCurrentChat().getEventId(), mChatActivityViewModel.getCurrentChat().getId());
                        observeChatUsersChange();
                        return;
                }
            }
        });
    }
}
