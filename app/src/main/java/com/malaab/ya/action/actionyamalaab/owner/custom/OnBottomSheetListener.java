package com.malaab.ya.action.actionyamalaab.owner.custom;

import android.support.annotation.IdRes;

public interface OnBottomSheetListener {

    void onPreClose();

    void onSlide(float slideOffset);

    void onBottomSheetExpanded(@IdRes int bsheetId);

    void onBottomSheetCollapsed(@IdRes int bsheetId, Object... args);
}
