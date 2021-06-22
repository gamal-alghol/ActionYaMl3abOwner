package com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.individual;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.BookingPlayer;
import com.malaab.ya.action.actionyamalaab.owner.di.PerActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.full.FullAttendeesMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpPresenter;


@PerActivity
public interface IndividualAttendeesMvpPresenter<V extends IndividualAttendeesMvpView> extends MvpPresenter<V> {

    void getCurrentUser();

    void getIndividualBookings(String userUid);


    void takeAttendanceBooking(Booking booking, String userUid);

    void takeAttendanceBooking(Booking booking, BookingPlayer player, String userUid);

    void payBooking(Booking booking, String userUid);

}
