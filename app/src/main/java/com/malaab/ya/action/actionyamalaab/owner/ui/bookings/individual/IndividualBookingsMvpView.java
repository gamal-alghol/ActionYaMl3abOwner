package com.malaab.ya.action.actionyamalaab.owner.ui.bookings.individual;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpView;

import java.util.List;


public interface IndividualBookingsMvpView extends MvpView {

    void onGetCurrentUser(User user);

    void onGetIndividualBookings(List<Booking> bookings);

    void onReceiveBookingSuccess(Booking booking);
}
