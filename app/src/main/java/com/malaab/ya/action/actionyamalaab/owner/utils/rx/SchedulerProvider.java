package com.malaab.ya.action.actionyamalaab.owner.utils.rx;

import io.reactivex.Scheduler;


public interface SchedulerProvider {

    Scheduler ui();

    Scheduler computation();

    Scheduler io();
}
