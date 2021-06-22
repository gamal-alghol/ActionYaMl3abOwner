package com.malaab.ya.action.actionyamalaab.owner.ui.loginbyphone;

import com.google.firebase.auth.PhoneAuthProvider;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpView;
import com.malaab.ya.action.actionyamalaab.owner.utils.PasswordStrength;


public interface LoginByPhoneMvpView extends MvpView {

    void onGetVerificationCodeSuccess(String phoneVerificationId, PhoneAuthProvider.ForceResendingToken resendToken);
    void onGetVerificationCodeFailed();

    void onUserVerifiedSuccess(String userUId);
    void onUserVerifiedFailed();

    void onUserIsActive(User user);
    void onUserNotActive(String message);

    void onCreateNewUser(String userUId);
    void onUserUniqueIdGenerated(String userUId, long appUserId);
}
