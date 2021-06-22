package com.malaab.ya.action.actionyamalaab.owner.ui;

import android.os.Bundle;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.LoginMode;
import com.malaab.ya.action.actionyamalaab.owner.data.DataManager;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.home.HomeActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.login.LoginActivity;
import com.malaab.ya.action.actionyamalaab.owner.utils.ActivityUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;


public class SplashActivity extends BaseActivity {

    @Inject
    public DataManager mDataManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        User user = mDataManager.getCurrentUser();


        if (user != null && user.isActive && user.loggedInMode != LoginMode.LOGGED_IN_MODE_LOGGED_OUT) {
            ActivityUtils.goTo(SplashActivity.this, HomeActivity.class, true);
        } else {
            ActivityUtils.goTo(SplashActivity.this, LoginActivity.class, true);
        }

        setUnBinder(ButterKnife.bind(this));
    }

    @Override
    protected void initUI() {
    }


    @Override
    public void onNoInternetConnection() {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
