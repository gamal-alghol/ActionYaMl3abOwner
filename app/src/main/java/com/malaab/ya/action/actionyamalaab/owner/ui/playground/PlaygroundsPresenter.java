package com.malaab.ya.action.actionyamalaab.owner.ui.playground;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malaab.ya.action.actionyamalaab.owner.data.DataManager;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Playground;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BasePresenter;
import com.malaab.ya.action.actionyamalaab.owner.utils.AppLogger;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.malaab.ya.action.actionyamalaab.owner.utils.analytics.IAnalyticsTracking;
import com.malaab.ya.action.actionyamalaab.owner.utils.firebase.IFirebaseTracking;
import com.malaab.ya.action.actionyamalaab.owner.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


public class PlaygroundsPresenter<V extends PlaygroundsMvpView>
        extends BasePresenter<V>
        implements PlaygroundsMvpPresenter<V> {

    //    private ValueEventListener mValueEventListener;
//    private ChildEventListener mChildEventListener;


    @Inject
    public PlaygroundsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable, IAnalyticsTracking iAnalyticsTracking, IFirebaseTracking iFirebaseTracking) {
        super(dataManager, schedulerProvider, compositeDisposable, iAnalyticsTracking, iFirebaseTracking);

        iAnalyticsTracking.LogEventScreen("Android - Owner - Playgrounds Screen");
        iFirebaseTracking.LogEventScreen("Android - Owner - Playgrounds Screen");
    }


    @Override
    public void getFutsalCourts() {
        getMvpView().showLoading();

        DatabaseReference mDatabasePlaygrounds = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_PLAYGROUNDS_TABLE);
        mDatabasePlaygrounds.orderByChild("ownerId")
                .equalTo(getDataManager().getCurrentUser().uId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Playground playground;
                        List<Playground> playgrounds = new ArrayList<>();

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            playground = child.getValue(Playground.class);
                            if (playground != null) {
                                playground.playgroundId = child.getKey();
                            }

                            playgrounds.add(playground);
                        }

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        Collections.reverse(playgrounds);

                        getMvpView().onGetPlayground(playgrounds);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        AppLogger.e(" Error -> " + databaseError.toException());

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                    }
                });
    }

//    @Override
//    public void listenToFutsalCourtsUpdates() {
//        /* To listen to any changed in database*/
//        mChildEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                AppLogger.d("onChildAdded:" + dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                AppLogger.d("onChildChanged:" + dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                AppLogger.d("onChildRemoved:" + dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                AppLogger.d("onChildMoved:" + dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                AppLogger.d("onCancelled:" + databaseError.toException());
//            }
//        };
//
//        mDatabasePlaygrounds.addChildEventListener(mChildEventListener);
//    }

//    @Override
//    public void removeListeners() {
//        if (mDatabasePlaygrounds != null) {
//            if (mValueEventListener != null) {
//                mDatabasePlaygrounds.removeEventListener(mValueEventListener);
//            }
//            if (mChildEventListener != null) {
//                mDatabasePlaygrounds.removeEventListener(mChildEventListener);
//            }
//        }
//    }
}
