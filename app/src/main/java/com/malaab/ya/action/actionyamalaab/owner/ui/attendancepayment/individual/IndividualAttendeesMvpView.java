package com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.individual;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.BookingPlayer;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpView;

import java.util.List;


public interface IndividualAttendeesMvpView extends MvpView {

    void onGetCurrentUser(User user);

    void onGetIndividualBookings(List<Booking> bookings);


    void onTakeAttendanceBookingSuccess(Booking booking);

    void onTakeAttendanceBookingSuccess(BookingPlayer player);

    void onPayBookingSuccess(Booking booking);
}
