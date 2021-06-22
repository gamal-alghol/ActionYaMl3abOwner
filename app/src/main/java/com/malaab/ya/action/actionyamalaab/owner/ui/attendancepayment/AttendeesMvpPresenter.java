package com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.di.PerActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.BookingsMvpView;

import java.util.List;


@PerActivity
public interface AttendeesMvpPresenter<V extends AttendeesMvpView> extends MvpPresenter<V> {

    void getCurrentUser();


    void getFullBookings(String userUid);

    void getFullBookingsFines(List<Booking> bookings);


    void takeAttendanceBooking(Booking booking, String userUid);

    void takeAbsentBooking(Booking booking);

    void createFine(final Booking booking);
}
