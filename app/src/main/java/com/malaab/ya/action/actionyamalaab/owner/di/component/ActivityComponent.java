package com.malaab.ya.action.actionyamalaab.owner.di.component;

import com.malaab.ya.action.actionyamalaab.owner.di.PerActivity;
import com.malaab.ya.action.actionyamalaab.owner.di.module.ActivityModule;
import com.malaab.ya.action.actionyamalaab.owner.ui.SplashActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.AttendeesActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.full.FullAttendeesFragment;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.individual.IndividualAttendeesFragment;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.BookingsActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.full.FullBookingsFragment;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.individual.IndividualBookingsFragment;
import com.malaab.ya.action.actionyamalaab.owner.ui.home.HomeActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.login.LoginActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.loginbyphone.LoginByPhoneActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.PlaygroundsActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit.PlaygroundAddActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.register.RegisterActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.user.UsersDetailsActivity;

import dagger.Component;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(SplashActivity activity);

    void inject(LoginActivity activity);

    void inject(LoginByPhoneActivity activity);

    void inject(RegisterActivity activity);


    void inject(HomeActivity activity);

    void inject(UsersDetailsActivity activity);


    void inject(PlaygroundsActivity activity);

    void inject(PlaygroundAddActivity activity);


    void inject(BookingsActivity activity);

    void inject(FullBookingsFragment fragment);

    void inject(IndividualBookingsFragment fragment);


    void inject(AttendeesActivity activity);

    void inject(FullAttendeesFragment fragment);

    void inject(IndividualAttendeesFragment fragment);

}
