package com.malaab.ya.action.actionyamalaab.owner.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.ItemAction;
import com.malaab.ya.action.actionyamalaab.owner.custom.CircularTextView;
import com.malaab.ya.action.actionyamalaab.owner.custom.DialogConfirmation;
import com.malaab.ya.action.actionyamalaab.owner.data.model.CalendarDay;
import com.malaab.ya.action.actionyamalaab.owner.data.model.CalendarTime;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Playground;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventItemClicked;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventSendFirebaseTokenToServer;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventToolbarItemClicked;
import com.malaab.ya.action.actionyamalaab.owner.ui.attendancepayment.AttendeesActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.BookingsActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.PlaygroundsActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit.PlaygroundAddActivity;
import com.malaab.ya.action.actionyamalaab.owner.utils.ActivityUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.AppLogger;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.yayandroid.theactivitymanager.TheActivityManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeActivity extends BaseActivity implements HomeMvpView {

    @BindView(R.id.ln_add)
    public LinearLayout ln_add;
    @BindView(R.id.ln_manage_bookings)
    public LinearLayout ln_manage_bookings;
    @BindView(R.id.txt_notificationCount)
    public CircularTextView txt_notificationCount;
    @BindView(R.id.ln_manage_playgrounds)
    public LinearLayout ln_manage_playgrounds;
    @BindView(R.id.ln_attendance)
    public LinearLayout ln_attendance;
    @BindView(R.id.ln_logout)
    public LinearLayout ln_logout;

    @Inject
    HomeMvpPresenter<HomeMvpView> mPresenter;
    @Inject
    DialogConfirmation mDialogConfirmation;

    private boolean isLoaded = false;
    private boolean doubleBackToExit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(this);
    }

    @Override
    protected void initUI() {
    }


    @OnClick(R.id.ln_manage_playgrounds)
    public void openPlaygrounds() {
        ActivityUtils.goTo(HomeActivity.this, PlaygroundsActivity.class, false);
    }

    @OnClick(R.id.ln_add)
    public void openAddPlayground() {
        ActivityUtils.goTo(HomeActivity.this, PlaygroundAddActivity.class, false);
    }

    @OnClick(R.id.ln_manage_bookings)
    public void openManageBookings() {
        ActivityUtils.goTo(HomeActivity.this, BookingsActivity.class, false);
    }

    @OnClick(R.id.ln_attendance)
    public void openManageAttendance() {
        ActivityUtils.goTo(HomeActivity.this, AttendeesActivity.class, false);
    }

    @OnClick(R.id.ln_logout)
    public void logout() {
        mDialogConfirmation
                .withTitle(getString(R.string.dialog_title_logout))
                .withMessage("")
                .withPositiveButton(getString(R.string.yes))
                .withNegativeButton(getString(R.string.no))
                .setOnDialogConfirmationListener(new DialogConfirmation.OnDialogConfirmationListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        mPresenter.signOut();
                    }

                    @Override
                    public void onNegativeButtonClick() {
                    }
                })
                .build()
                .show();
    }


    @Override
    public void onUserAuthenticationSuccess(String userUid) {
        mPresenter.refreshUserInfo(userUid);
    }

    @Override
    public void onRefreshUserInfo(User user) {
        if (!isLoaded) {
            isLoaded = true;

            mPresenter.isDeviceRegisteredForNotifications(user);

//            EventBus.getDefault().post(new OnEventStartCallingAPIs());
        }
    }


    @Override
    public void onUpdateNotificationsCounter(int count) {
        if (count > 0) {
            txt_notificationCount.setText(String.valueOf(count));
            txt_notificationCount.setVisibility(View.VISIBLE);
        } else {
            txt_notificationCount.setText("");
            txt_notificationCount.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUpdateMessagesCounter(int count) {

    }


    @Override
    public void onRegisterDeviceForNotification(User user) {
        mPresenter.registerForFirebaseNotifications(user);
    }


    @Override
    public void onPlaygroundInFavouriteList(boolean isFavourite) {
    }

    @Override
    public void onAddPlaygroundToFavouriteList(boolean isSuccess) {
    }

    @Override
    public void onRemovePlaygroundFromFavouriteList(boolean isSuccess) {
    }


    @Override
    public void onNoInternetConnection() {
        onError(R.string.error_no_connection);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventItemClicked(OnEventItemClicked event) {
        if (TheActivityManager.getInstance().getCurrentActivity() instanceof HomeActivity) {

            Object item = event.getItem();
            int action = event.getAction();

            if (item instanceof Playground) {
                if (action == ItemAction.DETAILS) {
                    mPresenter.isPlaygroundInFavouriteList((Playground) item);

                    //                mBSheetFragmentBook.setPlayground((Playground) item);
//                mBSheetFragmentBook.show(getSupportFragmentManager(), "BSheetFragmentShare");

                } else if (action == ItemAction.LOCATION) {
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelable(Constants.INTENT_KEY_PLAYGROUND, (Playground) item);
//                    ActivityUtils.goTo(HomeActivity.this, MapActivity.class, false, bundle);

                } else if (action == ItemAction.FAVOURITE_ADD) {
                    mPresenter.addPlaygroundToFavouriteList((Playground) item);

                } else if (action == ItemAction.FAVOURITE_REMOVE) {
                    mPresenter.removePlaygroundFromFavouriteList((Playground) item);

                }

            } else if (item instanceof CalendarDay && action == ItemAction.PICK) {

            } else if (item instanceof CalendarTime && action == ItemAction.PICK) {
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventToolbarItemClicked(OnEventToolbarItemClicked event) {
//        switch (event.getAction()) {
//            case ToolbarOption.MENU:
//                mSidebarDrawer.openDrawer();
//                break;
//
//            case ToolbarOption.SEARCH:
//                ActivityUtils.goTo(HomeActivity.this, SearchActivity.class, false);
//                break;
//
//            case ToolbarOption.MAP:
//                ActivityUtils.goTo(HomeActivity.this, MapActivity.class, false);
//                break;
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventSendFirebaseTokenToServer(OnEventSendFirebaseTokenToServer event) {
        mPresenter.registerForFirebaseNotifications(null);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        mPresenter.updateCounters();
        mPresenter.isUserAuthenticated();

//        AppLogger.d("FCM = " + FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    protected void onPause() {
        super.onPause();

        hideKeyboard();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExit) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExit = true;
        showMessage(getString(R.string.msg_exit));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExit = false;
            }
        }, Constants.EXIT_DELAY);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mPresenter.onDetach();
        super.onDestroy();
    }
}
