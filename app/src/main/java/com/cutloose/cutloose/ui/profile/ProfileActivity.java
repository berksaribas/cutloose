package com.cutloose.cutloose.ui.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cutloose.cutloose.R;
import com.cutloose.cutloose.databinding.ProfileActivityBinding;
import com.cutloose.cutloose.ui.common.BaseActivity;

public class ProfileActivity extends BaseActivity {
    private ProfileViewModel profileViewModel;
    private ProfileActivityBinding profileActivityBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileActivityBinding = (ProfileActivityBinding) this.mViewDataBinding;
        profileActivityBinding.setViewModel(profileViewModel);
        profileViewModel.fetchData();

        setTitle(R.string.profile);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.profile_activity;
    }
}
