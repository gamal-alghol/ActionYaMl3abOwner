package com.malaab.ya.action.actionyamalaab.owner;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.malaab.ya.action.actionyamalaab.owner.di.component.ApplicationComponent;
import com.malaab.ya.action.actionyamalaab.owner.di.component.DaggerApplicationComponent;
import com.malaab.ya.action.actionyamalaab.owner.di.module.ApplicationModule;
import com.malaab.ya.action.actionyamalaab.owner.utils.AppLogger;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.malaab.ya.action.actionyamalaab.owner.utils.LocaleHelper;
import com.yayandroid.theactivitymanager.TheActivityManager;

import javax.inject.Inject;

import pl.aprilapps.easyphotopicker.EasyImage;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class AppController extends Application {

    private static AppController mInstance;
    private ApplicationComponent mApplicationComponent;

    @Inject
    CalligraphyConfig mCalligraphyConfig;


    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: 31-Oct-17 remove this for release version
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);


        mInstance = this;

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        mApplicationComponent.inject(this);

        TheActivityManager.getInstance().setLogEnabled(true);

        AppLogger.init();
//        Traceur.enableLogging();

        CalligraphyConfig.initDefault(mCalligraphyConfig);

        EasyImage.configuration(this)
//                .setImagesFolderName(getString(R.string.app_name)) // images folder name, default is "EasyImage"
                .setCopyPickedImagesToPublicGalleryAppFolder(false)    // because we are saving the compressed the image anyway
                .setCopyTakenPhotosToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(true); // allows multiple picking in galleries that handle it. Also only for phones with API 18+ but it won't crash lower APIs. False by default
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, Constants.LANGUAGE_ARABIC_CODE));
//        super.attachBaseContext(base);

        MultiDex.install(this);
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }


    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    // Needed to replace the component with a layout_summary specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }

}