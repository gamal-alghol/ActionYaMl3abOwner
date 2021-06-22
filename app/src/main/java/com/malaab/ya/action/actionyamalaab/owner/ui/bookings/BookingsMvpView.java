package com.malaab.ya.action.actionyamalaab.owner.ui.bookings;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpView;

import java.util.List;


public interface BookingsMvpView extends MvpView {

    void onGetCurrentUser(User user);

    void onGetFullBookings(List<Booking> bookings);

    void onReceiveBookingSuccess(Booking booking);
}
