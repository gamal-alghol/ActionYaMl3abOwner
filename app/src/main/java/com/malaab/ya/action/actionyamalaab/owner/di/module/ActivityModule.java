package com.malaab.ya.action.actionyamalaab.owner.di.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.malaab.ya.action.actionyamalaab.owner.custom.DialogConfirmation;
import com.malaab.ya.action.actionyamalaab.owner.custom.DialogList;
import com.malaab.ya.action.actionyamalaab.owner.di.ActivityContext;
import com.malaab.ya.action.actionyamalaab.owner.di.PerActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.AttendeesMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.AttendeesMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.AttendeesPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.full.FullAttendeesMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.full.FullAttendeesMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.full.FullAttendeesPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.individual.IndividualAttendeesMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.individual.IndividualAttendeesMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.individual.IndividualAttendeesPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.BookingsMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.BookingsMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.BookingsPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.full.FullBookingsMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.full.FullBookingsMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.full.FullBookingsPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.individual.IndividualBookingsMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.individual.IndividualBookingsMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.individual.IndividualBookingsPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.home.HomeMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.home.HomeMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.home.HomePresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.login.LoginMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.login.LoginMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.login.LoginPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.loginbyphone.LoginByPhoneActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.loginbyphone.LoginByPhoneMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.loginbyphone.LoginByPhoneMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.loginbyphone.LoginByPhonePresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.PlaygroundsMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.PlaygroundsMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.PlaygroundsPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit.PlaygroundAddMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit.PlaygroundAddMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit.PlaygroundAddPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.register.RegisterMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.register.RegisterMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.register.RegisterPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.user.UsersDetailsMvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.user.UsersDetailsMvpView;
import com.malaab.ya.action.actionyamalaab.owner.ui.user.UsersDetailsPresenter;
import com.malaab.ya.action.actionyamalaab.owner.utils.Validator;
import com.malaab.ya.action.actionyamalaab.owner.utils.rx.AppSchedulerProvider;
import com.malaab.ya.action.actionyamalaab.owner.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;


@Module
public class ActivityModule {

    private AppCompatActivity mActivity;


    public ActivityModule(AppCompatActivity activity) {
        this.mActivity = activity;
    }


    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    AppCompatActivity provideActivity() {
        return mActivity;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return new AppSchedulerProvider();
    }


    @Provides
    LinearLayoutManager provideLinearLayoutManager(AppCompatActivity activity) {
        return new LinearLayoutManager(activity);
    }

    @Provides
    @PerActivity
    Validator provideValidator(AppCompatActivity activity) {
        return new Validator(activity);
    }


    @Provides
    @PerActivity
    DialogConfirmation provideDialogConfirmation(AppCompatActivity activity) {
        return new DialogConfirmation().with(activity);
    }

    @Provides
    @PerActivity
    DialogList provideDialogList(AppCompatActivity activity) {
        return new DialogList().with(activity);
    }


    @Provides
    @PerActivity
    LoginMvpPresenter<LoginMvpView> provideLoginPresenter(LoginPresenter<LoginMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    LoginByPhoneMvpPresenter<LoginByPhoneMvpView> provideLoginByPhonePresenter(LoginByPhonePresenter<LoginByPhoneMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    RegisterMvpPresenter<RegisterMvpView> provideRegisterPresenter(RegisterPresenter<RegisterMvpView> presenter) {
        return presenter;
    }


    @Provides
    @PerActivity
    HomeMvpPresenter<HomeMvpView> provideHomePresenter(HomePresenter<HomeMvpView> presenter) {
        return presenter;
    }


    @Provides
    @PerActivity
    PlaygroundsMvpPresenter<PlaygroundsMvpView> providePlaygroundAddPresenter(PlaygroundsPresenter<PlaygroundsMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    PlaygroundAddMvpPresenter<PlaygroundAddMvpView> providePlaygroundsPresenter(PlaygroundAddPresenter<PlaygroundAddMvpView> presenter) {
        return presenter;
    }


    @Provides
    @PerActivity
    BookingsMvpPresenter<BookingsMvpView> provideBookingsPresenter(BookingsPresenter<BookingsMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    FullBookingsMvpPresenter<FullBookingsMvpView> provideFullBookingsPresenter(FullBookingsPresenter<FullBookingsMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    IndividualBookingsMvpPresenter<IndividualBookingsMvpView> provideIndividualBookingsPresenter(IndividualBookingsPresenter<IndividualBookingsMvpView> presenter) {
        return presenter;
    }


    @Provides
    @PerActivity
    AttendeesMvpPresenter<AttendeesMvpView> provideAttendeesPresenter(AttendeesPresenter<AttendeesMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    FullAttendeesMvpPresenter<FullAttendeesMvpView> provideFullAttendeesPresenter(FullAttendeesPresenter<FullAttendeesMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    IndividualAttendeesMvpPresenter<IndividualAttendeesMvpView> provideIndividualAttendeesPresenter(IndividualAttendeesPresenter<IndividualAttendeesMvpView> presenter) {
        return presenter;
    }


    @Provides
    @PerActivity
    UsersDetailsMvpPresenter<UsersDetailsMvpView> provideUsersDetailsPresenter(UsersDetailsPresenter<UsersDetailsMvpView> presenter) {
        return presenter;
    }
}
