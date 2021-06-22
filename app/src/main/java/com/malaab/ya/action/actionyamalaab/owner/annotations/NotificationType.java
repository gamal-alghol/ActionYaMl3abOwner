package com.malaab.ya.action.actionyamalaab.owner.annotations;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@StringDef({NotificationType.BOOKING_FULL_NEW, NotificationType.BOOKING_INDIVIDUAL_NEW, NotificationType.BOOKING_ADMIN_APPROVED, NotificationType.BOOKING_ADMIN_REJECTED,
        NotificationType.BOOKING_USER_CANCELLED, NotificationType.BOOKING_OWNER_RECEIVED, NotificationType.BOOKING_INDIVIDUAL_COMPLETED,
        NotificationType.CAPTAIN_NEW, NotificationType.OWNER_NEW,
        NotificationType.OWNER_PLAYGROUND_NEW,
        NotificationType.USER_ADMIN_APPROVED,
        NotificationType.MESSAGE_CONTACT_US,
        NotificationType.FINE_ISSUED})
@Retention(RetentionPolicy.SOURCE)
public @interface NotificationType {

    String BOOKING_FULL_NEW = "0";
    String BOOKING_INDIVIDUAL_NEW = "1";
    String BOOKING_ADMIN_APPROVED = "2";
    String BOOKING_ADMIN_REJECTED = "3";
    String BOOKING_USER_CANCELLED = "4";
    String BOOKING_OWNER_RECEIVED = "5";
    String BOOKING_INDIVIDUAL_COMPLETED = "6";
    String CAPTAIN_NEW = "7";
    String OWNER_NEW = "8";
    String OWNER_PLAYGROUND_NEW = "9";
    String USER_ADMIN_APPROVED = "10";
    String MESSAGE_CONTACT_US = "11";
    String FINE_ISSUED = "13";
}
