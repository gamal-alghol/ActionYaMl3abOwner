package com.malaab.ya.action.actionyamalaab.owner.ui.user;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.data.DataManager;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BasePresenter;
import com.malaab.ya.action.actionyamalaab.owner.utils.AppLogger;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.malaab.ya.action.actionyamalaab.owner.utils.StringUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.analytics.IAnalyticsTracking;
import com.malaab.ya.action.actionyamalaab.owner.utils.firebase.IFirebaseTracking;
import com.malaab.ya.action.actionyamalaab.owner.utils.rx.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


public class UsersDetailsPresenter<V extends UsersDetailsMvpView>
        extends BasePresenter<V>
        implements UsersDetailsMvpPresenter<V> {

    @Inject
    public AppCompatActivity mActivity;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ValueEventListener mValueEventListener;


    @Inject
    public UsersDetailsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable, IAnalyticsTracking iAnalyticsTracking, IFirebaseTracking iFirebaseTracking) {
        super(dataManager, schedulerProvider, compositeDisposable, iAnalyticsTracking, iFirebaseTracking);

        iAnalyticsTracking.LogEventScreen("Android - Owner - Users Details Screen");
        iFirebaseTracking.LogEventScreen("Android - Owner - Users Details Screen");

        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void getUserDetails(String userUid) {
        if (StringUtils.isEmpty(userUid)) {
            getMvpView().onError(R.string.error_no_data);
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_USERS_TABLE);
        mDatabase.child(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);

                            if (user != null) {
                                getMvpView().onGetUserDetails(user);
                            }

                        } else {
                            getMvpView().showMessage(R.string.error_no_data);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        AppLogger.d("onCancelled = " + databaseError.getMessage());

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().showMessage(R.string.error);
                    }
                });
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
