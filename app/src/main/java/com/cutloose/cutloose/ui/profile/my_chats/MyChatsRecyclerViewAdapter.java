package com.cutloose.cutloose.ui.profile.my_chats;

import android.arch.lifecycle.LifecycleOwner;
import android.databinding.ViewDataBinding;

import com.cutloose.cutloose.R;
import com.cutloose.cutloose.BR;
import com.cutloose.cutloose.model.Chat;
import com.cutloose.cutloose.ui.common.RecyclerView.BaseRecyclerViewAdapter;
import com.cutloose.cutloose.ui.common.RecyclerView.BaseRecyclerViewModel;

/**
 * Created by finge on 5/6/2018.
 */

class MyChatsRecyclerViewAdapter extends BaseRecyclerViewAdapter<Chat> {

    public MyChatsRecyclerViewAdapter( BaseRecyclerViewModel<Chat> baseRecyclerViewModel, LifecycleOwner lifecycleOwner ) {
        super( baseRecyclerViewModel, lifecycleOwner );
    }

    @Override
    protected void setViewHolderBindings( ViewDataBinding binding, Chat chat ) {
        binding.setVariable( BR.viewModel, new MyChatsItemViewModel( chat ) );
    }

    @Override
    protected int getItemViewId() {
        return R.layout.my_chats_item;
    }


}