package com.malaab.ya.action.actionyamalaab.owner.ui.loginbyphone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.LoginMode;
import com.malaab.ya.action.actionyamalaab.owner.annotations.NotificationType;
import com.malaab.ya.action.actionyamalaab.owner.annotations.UserRole;
import com.malaab.ya.action.actionyamalaab.owner.data.DataManager;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.di.ActivityContext;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BasePresenter;
import com.malaab.ya.action.actionyamalaab.owner.utils.AppLogger;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.malaab.ya.action.actionyamalaab.owner.utils.DateTimeUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.FirebaseUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.analytics.IAnalyticsTracking;
import com.malaab.ya.action.actionyamalaab.owner.utils.firebase.IFirebaseTracking;
import com.malaab.ya.action.actionyamalaab.owner.utils.rx.SchedulerProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


public class LoginByPhonePresenter<V extends LoginByPhoneMvpView>
        extends BasePresenter<V>
        implements LoginByPhoneMvpPresenter<V> {

    @Inject
    @ActivityContext
    public Context mContext;

    @Inject
    public AppCompatActivity mActivity;

    private FirebaseAuth mAuth;


    @Inject
    public LoginByPhonePresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable, IAnalyticsTracking iAnalyticsTracking, IFirebaseTracking iFirebaseTracking) {
        super(dataManager, schedulerProvider, compositeDisposable, iAnalyticsTracking, iFirebaseTracking);

        iAnalyticsTracking.LogEventScreen("Android - Owner - Login Screen");
        iFirebaseTracking.LogEventScreen("Android - Owner - Login Screen");

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode(Constants.LANGUAGE_ARABIC_CODE);
    }


    @Override
    public void getVerificationCode(final String countryCode, String phoneNumber) {
        getMvpView().showLoading();
        String phone = countryCode.trim() + phoneNumber.trim();

        PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                AppLogger.d("onVerificationCompleted - Provider:" + credential.getProvider());
                AppLogger.d("onVerificationCompleted - SignInMethod:" + credential.getSignInMethod());
                AppLogger.d("onVerificationCompleted - SmsCode:" + credential.getSmsCode());

                if (!isViewAttached()) {
                    return;
                }

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("ttt",e.getMessage());
                if (!isViewAttached()) {
                    return;
                }

                getMvpView().hideLoading();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
//                    getMvpView().showMessage("Invalid credential: " + e.getLocalizedMessage());
                    getMvpView().showMessage(R.string.error_phone_no_format);
                    getMvpView().onGetVerificationCodeFailed();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // SMS quota exceeded
//                    getMvpView().showMessage("SMS Quota exceeded");
                    getMvpView().showMessage(R.string.error_many_request);
                    getMvpView().onGetVerificationCodeFailed();
                }
            }

            @Override
            public void onCodeSent(String phoneVerificationId, PhoneAuthProvider.ForceResendingToken token) {
                if (!isViewAttached()) {
                    return;
                }

                getDataManager().updateFirebaseSettings(phoneVerificationId, token);

                getMvpView().hideLoading();
                getMvpView().onGetVerificationCodeSuccess(phoneVerificationId, token);
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                mActivity,               // Activity (for callback binding)
                verificationCallbacks);
    }

    @Override
    public void verifyPhoneNumberWithCode(String phoneVerificationId, String code) {
        getMvpView().showLoading();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    @Override
    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!isViewAttached()) {
                            return;
                        }

                        if (task.isSuccessful()) {
                            // User has been added to firebase users as username is his phone number
                            FirebaseUser firebaseUser = task.getResult().getUser();

                            if (firebaseUser != null) {
//                                getDataManager().updateFirebaseSettings();

                                getMvpView().onUserVerifiedSuccess(firebaseUser.getUid());

                            } else {
                                getMvpView().hideLoading();
                                getMvpView().showMessage(R.string.error);
                                getMvpView().onUserVerifiedFailed();
                            }

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                getMvpView().hideLoading();
//                                getMvpView().showMessage("The verification code entered was invalid");
                                getMvpView().showMessage(R.string.error_verification_code);
                                getMvpView().onUserVerifiedFailed();
                            }
                        }
                    }
                });
    }


    @Override
    public void isUserExistInDB(final String userUId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_USERS_TABLE);
        mDatabase.child(userUId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                user.uId = dataSnapshot.getKey();

                                getDataManager().setCurrentUser(user);

                                if (user.role.equals(UserRole.ROLE_OWNER)) {

                                    if (user.isActive) {
                                        getMvpView().onUserIsActive(user);
                                    } else {
                                        getMvpView().onUserNotActive(mActivity.getString(R.string.error_user_inactive));
//                                        getMvpView().showMessage(R.string.error_user_inactive);
                                    }

                                } else {
                                    getMvpView().showMessage(R.string.error_user_owner);
                                }

                            } else {
                                getMvpView().onCreateNewUser(userUId);
                            }

//                            for (DataSnapshot userDetails : dataSnapshot.getChildren()) {
//                                Log.d("valueName:", userDetails.child("Name").getValue());
//                                Log.d("valueEmail:", userDetails.child("Email").getValue());
//                                Log.d("valueuserid:", userDetails.child("userid").getValue());
//                            }

                        } else {
                            getMvpView().onCreateNewUser(userUId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        AppLogger.d("isUserExistInDB onCancelled = " + databaseError.getMessage());

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onError(R.string.error);
                    }
                });
    }

    @Override
    public void generateUserUniqueId(final String userUId) {
        getMvpView().showLoading();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_META_DATA).child(Constants.FIREBASE_DB_META_DATA_USER_APPUSERID);
        mDatabase.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                // Analyse databaseError for any error during increment
//                AppLogger.d("onComplete - getMessage()" + databaseError.getMessage());

                if (!isViewAttached()) {
                    return;
                }

                if (dataSnapshot.exists()) {
                    Long appUserId = dataSnapshot.getValue(Long.class);
                    if (appUserId != null) {
                        getMvpView().onUserUniqueIdGenerated(userUId, appUserId);
                        return;
                    }
                }

                getMvpView().showMessage(R.string.error);
            }
        });

    }

    @Override
    public void addUserToFirebaseDatabase(final String userUId, long appUserId, final String fName, final String lName, final String email,
                                          final String countryCode, final String phone,
                                          final String password,
                                          final String referredBy) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_USERS_TABLE);
//        final String userUId = mDatabase.child(Constants.FIREBASE_DB_USERS_TABLE).push().getKey();

        final User user = new User(new User.UserBuilder(email, password, LoginMode.LOGGED_IN_MODE_SERVER)
                .withOptionalUID(userUId)
                .withOptionalAppUserId(appUserId)
                .withOptionalRole(UserRole.ROLE_OWNER)
                .withOptionalFirstName(fName)
                .withOptionalLastName(lName)
                .withOptionalMobileNo(countryCode.trim() + phone.trim())
                .withOptionalCreatedDate(DateTimeUtils.getCurrentDatetime())
                .withOptionalReferredBy(referredBy)
                .withOptionalIsActive(false)
        );

        mDatabase.child(userUId)
                .setValue(user)  // pushing user to 'users' node using the userId
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (task.isSuccessful()) {
                            AppLogger.i("Adding User To Database -> Success.");

                            FirebaseUtils.sendNotificationToAdmin(NotificationType.OWNER_NEW, userUId, user.getUserFullName(), user.profileImageUrl);

                            getDataManager().setCurrentUser(user);

                            getMvpView().onUserNotActive(mActivity.getString(R.string.msg_owner_create_success));

                        } else {
                            AppLogger.i("Adding User To Database -> Failed.");
                            getMvpView().showMessage(Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }


//    @Override
//    public void signInWithPhoneAndDoServerRegister(String phoneVerificationId, String code,
//                                                   final String fName, final String lName, final String email, final String phone, final String password,
//                                                   final String referredBy) {
//        getMvpView().showLoading();
//        getMvpView().onHidePasswordStrength();
//
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (!isViewAttached()) {
//                            return;
//                        }
//
//                        if (task.isSuccessful()) {
//                            // User has been added to firebase users as username is his phone number
//                            FirebaseUser firebaseUser = task.getResult().getUser();
//
//                            if (firebaseUser != null) {
//                                // creating user object
//                                User user = new User(new User.UserBuilder(email, password, LoginMode.LOGGED_IN_MODE_SERVER)
//                                        .withOptionalUID(firebaseUser.getUid())
//                                        .withOptionalRole(UserRole.ROLE_OWNER)
//                                        .withOptionalFirstName(fName)
//                                        .withOptionalLastName(lName)
//                                        .withOptionalMobileNo(phone)
//                                        .withOptionalReferredBy(referredBy)
//                                        .withOptionalIsActive(false)
//                                );
//
//                                getMvpView().onUserVerifiedSuccess(user);
//
//                            } else {
//                                getMvpView().hideLoading();
//                                getMvpView().onError(R.string.error);
//                            }
//
//                        } else {
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                getMvpView().hideLoading();
//                                getMvpView().onError("The verification code entered was invalid");
//                            }
//                        }
//                    }
//                });
//    }

}
