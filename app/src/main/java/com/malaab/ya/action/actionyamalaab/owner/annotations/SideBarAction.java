package com.malaab.ya.action.actionyamalaab.owner.annotations;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@StringDef({SideBarAction.MY_FAVOURITE, SideBarAction.MAPS, SideBarAction.CHANGE_LOCATION, SideBarAction.CONTACT_US, SideBarAction.ABOUT_US, SideBarAction.APP_POLICY, SideBarAction.LOG_OUT,
        SideBarAction.MY_FAVOURITE_AR, SideBarAction.MAPS_AR, SideBarAction.CHANGE_LOCATION_AR, SideBarAction.CONTACT_US_AR, SideBarAction.ABOUT_US_AR, SideBarAction.APP_POLICY_AR, SideBarAction.LOG_OUT_AR})
@Retention(RetentionPolicy.SOURCE)
public @interface SideBarAction {
    String MY_FAVOURITE = "My Favourite";
    String MAPS = "Maps";
    String CHANGE_LOCATION = "Change Location";
    String CONTACT_US = "Contact Us";
    String ABOUT_US = "About Us";
    String APP_POLICY = "App Policy";
    String LOG_OUT = "Log out";

    String MY_FAVOURITE_AR = "المفضلة";
    String MAPS_AR = "الخرائط";
    String CHANGE_LOCATION_AR = "تغير الموقع";
    String CONTACT_US_AR = "راسلنا";
    String ABOUT_US_AR = "عن التطبيق";
    String APP_POLICY_AR = "سياسة التطبيق";
    String LOG_OUT_AR = "تسجيل الخروج";
}
