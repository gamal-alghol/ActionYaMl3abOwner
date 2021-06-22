package com.malaab.ya.action.actionyamalaab.owner.ui.bookings;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.di.PerActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpPresenter;


@PerActivity
public interface BookingsMvpPresenter<V extends BookingsMvpView> extends MvpPresenter<V> {

    void getCurrentUser();

    void getFullBookings(String userUid);

    void receiveBooking(Booking booking, String userUid);
}
