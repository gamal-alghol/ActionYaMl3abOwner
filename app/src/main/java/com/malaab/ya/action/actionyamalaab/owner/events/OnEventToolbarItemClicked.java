package com.malaab.ya.action.actionyamalaab.owner.events;

import com.malaab.ya.action.actionyamalaab.owner.annotations.ToolbarOption;

public class OnEventToolbarItemClicked {

    @ToolbarOption
    private int mEnumToolbarOption;


    public OnEventToolbarItemClicked(@ToolbarOption int enumToolbarOption) {
        mEnumToolbarOption = enumToolbarOption;
    }

    @ToolbarOption
    public int getAction() {
        return mEnumToolbarOption;
    }
}
