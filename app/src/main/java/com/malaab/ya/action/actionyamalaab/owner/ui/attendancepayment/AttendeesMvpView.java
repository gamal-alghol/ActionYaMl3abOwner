package com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpView;

import java.util.List;


public interface AttendeesMvpView extends MvpView {

    void onGetCurrentUser(User user);


    void onGetFullBookings(List<Booking> bookings);

    void onGetFullBookingsEmpty();

    void onGetFullBookingsFines(Booking booking);


    void onTakeAttendanceBookingSuccess(Booking booking);

    void onTakeAbsentBookingSuccess(Booking booking);


    void onFineCreateSuccess(Booking booking);
}
