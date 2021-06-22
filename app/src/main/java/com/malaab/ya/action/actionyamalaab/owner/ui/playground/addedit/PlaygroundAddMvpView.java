package com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit;

import com.malaab.ya.action.actionyamalaab.owner.data.model.PlaygroundImage;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Playground;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSchedule;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSchedules;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Region;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpView;

import java.util.List;


public interface PlaygroundAddMvpView extends MvpView {

    void onGetCurrentUser(User user);


    void onGetRegionsSuccess(List<Region> regions);

    void onGetRegionsFailed();

    void onGetPlaygroundSchedules(PlaygroundSchedules playgroundSchedules);


    void onPlaygroundImageUploaded(PlaygroundImage playgroundImage);

    void onPlaygroundImageDeleted(int position);


    void onPlaygroundAdded(Playground playground);

    void onPlaygroundUpdated(Playground playground);

    void onPlaygroundSchedulesAdded(Playground playground, List<PlaygroundSchedule> playgroundSchedules);

    void onPlaygroundSearchAdded();

}
