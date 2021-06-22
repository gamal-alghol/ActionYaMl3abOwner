package com.malaab.ya.action.actionyamalaab.owner.ui.bookings.full;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.di.PerActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpPresenter;


@PerActivity
public interface FullBookingsMvpPresenter<V extends FullBookingsMvpView> extends MvpPresenter<V> {

    void getCurrentUser();

    void getFullBookings(String userUid);

    void receiveBooking(Booking booking, String userUid);
}
