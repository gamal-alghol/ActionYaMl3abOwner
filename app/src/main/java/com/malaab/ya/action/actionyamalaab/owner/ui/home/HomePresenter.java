package com.malaab.ya.action.actionyamalaab.owner.ui.home;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malaab.ya.action.actionyamalaab.owner.data.DataManager;
import com.malaab.ya.action.actionyamalaab.owner.data.model.Counter;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Playground;
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


public class HomePresenter<V extends HomeMvpView>
        extends BasePresenter<V>
        implements HomeMvpPresenter<V> {

    @Inject
    public AppCompatActivity mActivity;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ValueEventListener mValueEventListener;


    @Inject
    public HomePresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable, IAnalyticsTracking iAnalyticsTracking, IFirebaseTracking iFirebaseTracking) {
        super(dataManager, schedulerProvider, compositeDisposable, iAnalyticsTracking, iFirebaseTracking);

        iAnalyticsTracking.LogEventScreen("Android - Owner - Home Screen");
        iFirebaseTracking.LogEventScreen("Android - Owner - Home Screen");

        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void isUserAuthenticated() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    AppLogger.d("onAuthStateChanged:signed_in:" + user.getUid() + " " + user.getEmail() + " " + user.getPhoneNumber());
                    getMvpView().onUserAuthenticationSuccess(user.getUid());
                } else {
                    // User is signed out
                    AppLogger.d("onAuthStateChanged:signed_out");
                    getMvpView().onUserAuthenticationFailed();
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void refreshUserInfo(final String userUid) {
        if (StringUtils.isEmpty(userUid)) {
            getMvpView().onRefreshUserInfo(null);
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_USERS_TABLE).child(userUid);

        /* To load the list once only*/
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isViewAttached()) {
                    return;
                }

                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        getDataManager().updateCurrentUserInfo(userUid, user.appUserId,
                                user.email, user.password, user.fName, user.lName, user.dob, user.age, user.mobileNo,
                                user.modifyDate, user.createdDate,
                                user.profileImageUrl,
                                user.referral_code, user.referred_by,
                                user.role,
                                user.fcmToken,
                                user.address_city, user.address_direction, user.address_region,
                                user.latitude, user.longitude,
                                user.loggedInMode, user.isActive);

                        getMvpView().onRefreshUserInfo(user);
                        return;
                    }
                }

                getMvpView().onRefreshUserInfo(null);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                AppLogger.e(" Error -> " + error.toException());

                if (!isViewAttached()) {
                    return;
                }

                getMvpView().onRefreshUserInfo(null);
            }
        };

        mDatabase.addListenerForSingleValueEvent(mValueEventListener);
    }


    @Override
    public void updateCounters() {
        Counter counter = getDataManager().getCounters();
        if (counter != null) {
            getMvpView().onUpdateNotificationsCounter(counter.notificationsCounter);
            getMvpView().onUpdateMessagesCounter(counter.messagesCounter);
        }
    }


    @Override
    public void isDeviceRegisteredForNotifications(User user) {
        if (getDataManager().getNotificationsSettings() == null
                || !getDataManager().getNotificationsSettings().isRegistered)

            getMvpView().onRegisterDeviceForNotification(user);
    }

    @Override
    public void registerForFirebaseNotifications(User user) {
        if (user == null) {
            user = getDataManager().getCurrentUser();
        }

        if (user == null) {
            return;
        }

        final String token = getDataManager().getNotificationsSettings() == null ? "" : getDataManager().getNotificationsSettings().firebaseToken;
        if (StringUtils.isEmpty(token)) {
            return;
        }

        user.fcmToken = token;

        final User finalUser = user;

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_USERS_TABLE);
        mDatabase.child(finalUser.uId)
                .setValue(finalUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AppLogger.i(" onSuccess");

                        if (!isViewAttached()) {
                            return;
                        }

                        getDataManager().updateCurrentUserInfo(finalUser.uId, finalUser.appUserId,
                                finalUser.email, finalUser.password, finalUser.fName, finalUser.lName, finalUser.dob, finalUser.age, finalUser.mobileNo,
                                finalUser.modifyDate, finalUser.createdDate,
                                finalUser.profileImageUrl,
                                finalUser.referral_code, finalUser.referred_by,
                                finalUser.role,
                                finalUser.fcmToken,
                                finalUser.address_city, finalUser.address_direction, finalUser.address_region,
                                finalUser.latitude, finalUser.longitude,
                                finalUser.loggedInMode, finalUser.isActive);

                        getDataManager().updateNotificationsSettings(token, true);

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AppLogger.i(" onComplete");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AppLogger.e(" Error -> " + e.getLocalizedMessage());

                        if (!isViewAttached()) {
                            return;
                        }

                        getDataManager().updateNotificationsSettings(token, false);
                    }
                });
    }


    @Override
    public void isPlaygroundInFavouriteList(Playground playground) {
        String userUid = getDataManager().getCurrentUser().uId;
        getMvpView().onPlaygroundInFavouriteList(getDataManager().isPlaygroundInFavouriteList(userUid, playground));
    }

    @Override
    public void addPlaygroundToFavouriteList(Playground playground) {
        String userUid = getDataManager().getCurrentUser().uId;
        getDataManager().addPlaygroundToFavouriteList(userUid, playground);

        getMvpView().onAddPlaygroundToFavouriteList(true);
    }

    @Override
    public void removePlaygroundFromFavouriteList(Playground playground) {
        String userUid = getDataManager().getCurrentUser().uId;
        getDataManager().removePlaygroundTFromFavouriteList(userUid, playground);

        getMvpView().onRemovePlaygroundFromFavouriteList(true);
    }


    @Override
    public void pushFutsalCourts() {
//        getMvpView().showLoading();
//
//        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_PLAYGROUNDS_TABLE);
//
//        Playground playground;
//        Amenity amenity;
////        List<Playground> playgrounds = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            String uid = mDatabase.child("playgrounds").push().getKey();
//
//            amenity = new Amenity.AmenityBuilder(uid)
//                    .withOptionalBall(true)
//                    .withOptionalGrass(true)
//                    .withOptionalShower(true)
//                    .withOptionalWater(true)
//                    .withOptionalWC(true)
//                    .build();
//
//            playground = new Playground("ملعب الابطال", "منطقة مكة المكرمة - جدة - شمال جدة - حي الصفا", "https://www.fccincinnati.com/news_article/show/789260-fcc-community-fund-crc-to-build-futsal-courts-", amenity);
//
////            playgrounds.add(playground);
//
//
//            mDatabase.child(uid)
//                    .setValue(playground)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            AppLogger.w(" onSuccess");
//                        }
//                    })
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            AppLogger.w(" onComplete");
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            AppLogger.e(" Error -> " + e.getLocalizedMessage());
//                        }
//                    });
//        }


//        String playgroundId = mDatabase.child("playgrounds").push().getKey();
//        mDatabase.child("users").child(amenity.uid).setValue(playground);


//
//        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                AppLogger.d("onDataChange");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        getMvpView().hideLoading();
    }


    @Override
    public void signOut() {
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }

        getDataManager().setUserAsLoggedOut();

        mAuth.signOut();

//        getMvpView().onUserSignOut();
    }


    @Override
    public void onDetach() {
        super.onDetach();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
