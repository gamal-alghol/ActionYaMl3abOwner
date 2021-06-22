package com.malaab.ya.action.actionyamalaab.owner.ui.playground;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Playground;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpView;

import java.util.List;


public interface PlaygroundsMvpView extends MvpView {

    void onGetPlayground(List<Playground> playgrounds);
}
