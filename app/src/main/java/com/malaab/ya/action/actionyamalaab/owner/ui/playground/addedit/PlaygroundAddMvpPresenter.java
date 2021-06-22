package com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit;

import com.malaab.ya.action.actionyamalaab.owner.data.model.Location;
import com.malaab.ya.action.actionyamalaab.owner.data.model.PlaygroundImage;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Playground;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSchedule;
import com.malaab.ya.action.actionyamalaab.owner.di.PerActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpPresenter;

import java.util.List;


@PerActivity
public interface PlaygroundAddMvpPresenter<V extends PlaygroundAddMvpView> extends MvpPresenter<V> {

    void getCurrentUser();

    void getRegions();

    void getPlaygroundSchedules(String playgroundUid);


    void uploadPlaygroundImages(List<PlaygroundImage> playgroundImages);

    void deletePlaygroundImages(Playground playground, String imageUrl, int position);


    void addPlayground(String name,String nameOwner, String mobileOwner, String nameGuard, String mobileGuard, boolean isPromotion, String note, String region, String city, String direction, Location location,
                       List<PlaygroundImage> images,
                       boolean hasShower, boolean hasWC, boolean hasGrass, boolean hasWater, boolean hasBall,boolean isHide);

    void updatePlayground(String playgroundId, String name, String nameOwner, String mobileOwner, String nameGuard, String mobileGuard, boolean isPromotion, String note, String region, String city, String direction, Location location,
                          List<PlaygroundImage> images,
                              boolean hasShower, boolean hasWC, boolean hasGrass, boolean hasWater, boolean hasBall,
                              boolean isActive,boolean isHide);

    void addPlaygroundSchedules(Playground playground, List<PlaygroundSchedule> playgroundSchedules);

    void addPlaygroundSearch(Playground playground, PlaygroundSchedule playgroundSchedule);


//    void addPlaygroundSearch(String playgroundId, String name, String region, String city, String direction, double latitude, double longitude,
//                             List<String> images,
//                             boolean hasAgeYoung, boolean hasAgeMiddle, boolean hasAgeOld,
//                             float price, int size,
//                             boolean hasIndividuals, boolean isActive,
//                             boolean isContinue);
}
