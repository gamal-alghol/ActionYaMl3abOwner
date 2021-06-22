package com.malaab.ya.action.actionyamalaab.owner.ui.register;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.LoginMode;
import com.malaab.ya.action.actionyamalaab.owner.annotations.UserRole;
import com.malaab.ya.action.actionyamalaab.owner.data.DataManager;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.MetaData;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.di.ActivityContext;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BasePresenter;
import com.malaab.ya.action.actionyamalaab.owner.utils.AppLogger;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.malaab.ya.action.actionyamalaab.owner.utils.DateTimeUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.PasswordStrength;
import com.malaab.ya.action.actionyamalaab.owner.utils.StringUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.analytics.IAnalyticsTracking;
import com.malaab.ya.action.actionyamalaab.owner.utils.firebase.IFirebaseTracking;
import com.malaab.ya.action.actionyamalaab.owner.utils.rx.SchedulerProvider;

import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


public class RegisterPresenter<V extends RegisterMvpView>
        extends BasePresenter<V>
        implements RegisterMvpPresenter<V> {

    @Inject
    @ActivityContext
    public Context mContext;

    @Inject
    public AppCompatActivity mActivity;

    private FirebaseAuth mAuth;


    @Inject
    public RegisterPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable, IAnalyticsTracking iAnalyticsTracking, IFirebaseTracking iFirebaseTracking) {
        super(dataManager, schedulerProvider, compositeDisposable, iAnalyticsTracking, iFirebaseTracking);

        iAnalyticsTracking.LogEventScreen("Android - Owner - Register Screen");
        iFirebaseTracking.LogEventScreen("Android - Owner - Register Screen");

        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void getPasswordStrength(String password) {
        PasswordStrength str = PasswordStrength.calculateStrength(mContext, password);

        if (!isViewAttached()) {
            return;
        }

        if (StringUtils.isEmpty(password)) {
            getMvpView().onHidePasswordStrength();
            return;
        }

        if (str.getText(mContext).equals(Constants.PASSWORD_STRENGTH_WEAK)) {
            getMvpView().onShowPasswordWeak(str);

        } else if (str.getText(mContext).equals(Constants.PASSWORD_STRENGTH_MEDIUM)) {
            getMvpView().onShowPasswordMedium(str);

        } else if (str.getText(mContext).equals(Constants.PASSWORD_STRENGTH_STRONG)) {
            getMvpView().onShowPasswordStrong(str);

        } else {
            getMvpView().onShowPasswordVeryStrong(str);
        }
    }

    @Override
    public void doServerRegister(final String fName, final String lName, final String email, final String phone, final String password, final String referredBy) {
//        if (!StringUtils.isWordsMatched(password, referralCode)) {
//            getMvpView().onError(R.string.error_password_not_matched);
//            return;
//        }

        getMvpView().showLoading();
        getMvpView().onHidePasswordStrength();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (task.isSuccessful()) {
                            AppLogger.i("Authentication Success.");

                            // creating user object
                            User user = new User(new User.UserBuilder(email, password, LoginMode.LOGGED_IN_MODE_SERVER)
                                    .withOptionalRole(UserRole.ROLE_OWNER)
                                    .withOptionalIsActive(false)
                                    .withOptionalFirstName(fName)
                                    .withOptionalLastName(lName)
                                    .withOptionalMobileNo(phone)
                                    .withOptionalCreatedDate(DateTimeUtils.getCurrentDatetime())
                                    .withOptionalReferredBy(referredBy)
                            );

                            getMvpView().onServerRegisteredSuccess(user);

                        } else {
                            AppLogger.e("Authentication Failed." + task.getException());

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                getMvpView().onError(R.string.error_email_already_registered);
                            } else {
                                if (task.getException() != null) {
                                    getMvpView().onError(task.getException().getLocalizedMessage());
                                } else {
                                    getMvpView().onError(R.string.error);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void generateUserUniqueId(final User user) {
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

                getMvpView().showLoading();

                if (dataSnapshot.exists()) {
                    Long appUserId = dataSnapshot.getValue(Long.class);
                    if (appUserId != null) {
                        user.appUserId = appUserId;
                        getMvpView().onUserUniqueIdGenerated(user);
                        return;
                    }
                }

                getMvpView().onError(R.string.error);
            }
        });

    }

    @Override
    public void addUserToFirebaseDatabase(final User user) {
        getMvpView().showLoading();

        mAuth.signInWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (task.isSuccessful()) {

                            user.uId = task.getResult().getUser().getUid();

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_USERS_TABLE);
                            mDatabase.child(user.uId)
                                    .setValue(user)  // pushing user to 'users' node using the userId
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                AppLogger.i("Adding User To Database -> Success.");

                                                getDataManager().setCurrentUser(user);

                                                getMvpView().onAddedToFirebaseDatabase();

                                            } else {
                                                AppLogger.i("Adding User To Database -> Failed.");
                                                getMvpView().onError(Objects.requireNonNull(task.getException()).getMessage());
                                            }
                                        }
                                    });

                        } else {
                            AppLogger.e("Sign In -> Failed" + task.getException());
                            getMvpView().onError("Sign In -> Failed \n" + task.getException());
                        }
                    }
                });
    }

}
