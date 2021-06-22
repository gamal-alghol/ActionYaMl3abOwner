package com.malaab.ya.action.actionyamalaab.owner.ui.bookings.individual;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.di.PerActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.full.FullBookingsMvpView;


@PerActivity
public interface IndividualBookingsMvpPresenter<V extends IndividualBookingsMvpView> extends MvpPresenter<V> {

    void getCurrentUser();

    void getIndividualBookings(String userUid);

    void receiveBooking(Booking booking, String userUid);
}
