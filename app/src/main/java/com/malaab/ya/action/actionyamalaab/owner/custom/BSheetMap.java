package com.malaab.ya.action.actionyamalaab.owner.custom;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.Button;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.data.model.Location;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BSheetMap extends BottomSheetBehavior {

    @BindView(R.id.mCustomMapView)
    public CustomMapView mCustomMapView;

    @BindView(R.id.bsheet_btn_continue)
    public Button bsheet_btn_continue;

    private Activity mActivity;
    private BottomSheetBehavior mBottomSheetBehavior;
    private OnBottomSheetListener mBottomSheetListener;


    public void attachAndInit(final Activity activity) {
        if (activity != null) {
            mActivity = activity;

            View bottomSheet = mActivity.findViewById(R.id.bSheet_map);
            ButterKnife.bind(this, bottomSheet);

            mCustomMapView.enableOnMapListener();

            mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            mBottomSheetBehavior.setPeekHeight(0);

            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (mBottomSheetListener == null)
                        return;

                    if (newState == STATE_EXPANDED) {
                        if (mBottomSheetBehavior instanceof LockableBottomSheetBehavior) {
                            ((LockableBottomSheetBehavior) mBottomSheetBehavior).setLocked(true);
                        }

                        mBottomSheetListener.onBottomSheetExpanded(R.id.bSheet_map);

                    } else if (newState == STATE_COLLAPSED) {
                        mBottomSheetListener.onBottomSheetCollapsed(R.id.bSheet_map, getSelectedLocation());
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    if (mBottomSheetListener == null)
                        return;

                    mBottomSheetListener.onSlide(slideOffset);
                }
            });
        }
    }


    @OnClick(R.id.bsheet_btn_continue)
    void login() {
        hide();
    }


    public void setOnBottomSheetListener(OnBottomSheetListener listener) {
        mBottomSheetListener = listener;
    }


    public void addMarker(String title, String snippet, double latitude, double longitude, String profileImageUrl) {
        mCustomMapView.addMarker(new Location(title, snippet, latitude, longitude, profileImageUrl), true);
    }



    public Location getSelectedLocation() {
        return mCustomMapView.getSelectedLocation();
    }


    private boolean isShown() {
        return mBottomSheetBehavior != null && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    public void show() {
        if (!isShown()) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void hide() {
//        if (isShown()) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        }
    }


    public boolean onBackPressed() {
        if (isShown()) {
            hide();
            return true;
        }

        return false;
    }

    public void onDetach() {
        mBottomSheetListener = null;
        mActivity = null;
    }
}
