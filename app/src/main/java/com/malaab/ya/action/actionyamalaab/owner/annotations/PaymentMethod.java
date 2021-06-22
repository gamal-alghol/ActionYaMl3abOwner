package com.malaab.ya.action.actionyamalaab.owner.annotations;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@StringDef({PaymentMethod.CASH_EN, PaymentMethod.CASH_AR})
@Retention(RetentionPolicy.SOURCE)
public @interface PaymentMethod {

    String CASH_EN = "Cash On Arrival";
    String CASH_AR = "نقدا في الملعب";
}
