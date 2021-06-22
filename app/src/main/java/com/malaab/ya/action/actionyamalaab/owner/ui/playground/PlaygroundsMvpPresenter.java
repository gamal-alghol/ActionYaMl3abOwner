package com.malaab.ya.action.actionyamalaab.owner.ui.playground;

import com.malaab.ya.action.actionyamalaab.owner.di.PerActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpPresenter;


@PerActivity
public interface PlaygroundsMvpPresenter<V extends PlaygroundsMvpView> extends MvpPresenter<V> {

    void getFutsalCourts();

//    void listenToFutsalCourtsUpdates();
//
//    void removeListeners();
}
