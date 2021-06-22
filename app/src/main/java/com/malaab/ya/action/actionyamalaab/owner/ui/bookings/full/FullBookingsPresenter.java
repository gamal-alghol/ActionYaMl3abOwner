package com.malaab.ya.action.actionyamalaab.owner.ui.bookings.full;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.BookingStatus;
import com.malaab.ya.action.actionyamalaab.owner.annotations.NotificationType;
import com.malaab.ya.action.actionyamalaab.owner.data.DataManager;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BasePresenter;
import com.malaab.ya.action.actionyamalaab.owner.utils.AppLogger;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.malaab.ya.action.actionyamalaab.owner.utils.DateTimeUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.FirebaseUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.analytics.IAnalyticsTracking;
import com.malaab.ya.action.actionyamalaab.owner.utils.firebase.IFirebaseTracking;
import com.malaab.ya.action.actionyamalaab.owner.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


public class FullBookingsPresenter<V extends FullBookingsMvpView>
        extends BasePresenter<V>
        implements FullBookingsMvpPresenter<V> {

    private DatabaseReference mDatabase;
    private ValueEventListener mValueEventListener;


    @Inject
    public FullBookingsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable, IAnalyticsTracking iAnalyticsTracking, IFirebaseTracking iFirebaseTracking) {
        super(dataManager, schedulerProvider, compositeDisposable, iAnalyticsTracking, iFirebaseTracking);

        iAnalyticsTracking.LogEventScreen("Android - Owner - Full Bookings Screen");
        iFirebaseTracking.LogEventScreen("Android - Owner - Full Bookings Screen");

        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_PLAYGROUNDS_SCHEDULES_BOOKING_TABLE);
    }


    @Override
    public void getCurrentUser() {
        if (!isViewAttached()) {
            return;
        }

        getMvpView().onGetCurrentUser(getDataManager().getCurrentUser());
    }

    @Override
    public void getFullBookings(String userUid) {
        getMvpView().showLoading();

        getDataManager().resetNotificationsCounters();

        String criteria = userUid + "_" + BookingStatus.ADMIN_APPROVED;

        mDatabase.orderByChild("ownerId_status").equalTo(criteria)
//        mDatabase.orderByChild("ownerId").equalTo(userUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (dataSnapshot.exists()) {
                            Booking booking;
                            List<Booking> fullBookings = new ArrayList<>();
//                            List<Booking> individualBookings = new ArrayList<>();

                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                booking = child.getValue(Booking.class);

                                if (booking != null) {

                                    booking.bookingUId = child.getKey();

                                    if (booking.isIndividuals) {
//                                        individualBookings.add(booking);
                                    } else {
                                        if (DateTimeUtils.isDateAfterCurrentDate(booking.timeStart)) {
                                            fullBookings.add(booking);
                                        }
                                    }
                                }
                            }

                            getMvpView().onGetFullBookings(fullBookings);

                        } else {
                            getMvpView().onError(R.string.error_no_data);
                        }
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

    @Override
    public void receiveBooking(final Booking booking, String userUid) {
        getMvpView().showLoading();

        booking.status = BookingStatus.OWNER_RECEIVED;
        booking.ownerId_status = userUid + "_" + BookingStatus.OWNER_RECEIVED;

        mDatabase.child(booking.bookingUId)
                .setValue(booking)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onReceiveBookingSuccess(booking);
                        getMvpView().showMessage(R.string.msg_success);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AppLogger.w(" onComplete");

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        FirebaseUtils.sendNotificationToAdmin(NotificationType.BOOKING_OWNER_RECEIVED,
                                getDataManager().getCurrentUser().uId, getDataManager().getCurrentUser().getUserFullName(), getDataManager().getCurrentUser().profileImageUrl);

                        FirebaseUtils.sendNotificationToUser(NotificationType.BOOKING_OWNER_RECEIVED,
                                getDataManager().getCurrentUser().uId, getDataManager().getCurrentUser().getUserFullName(), getDataManager().getCurrentUser().profileImageUrl,
                                booking.user.uId, booking.user.name, booking.user.profileImageUrl);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AppLogger.e(" Error -> " + e.getLocalizedMessage());

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onError(e.getLocalizedMessage());
                    }
                });

    }
}
