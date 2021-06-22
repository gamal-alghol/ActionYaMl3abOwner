package com.malaab.ya.action.actionyamalaab.owner.custom;

import android.content.Context;
import android.util.AttributeSet;


public class CustomSpinner extends android.support.v7.widget.AppCompatSpinner {

    public CustomSpinner(Context context) {
        super(context);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setSelection(int position, boolean animate) {
        boolean sameSelected = position == getSelectedItemPosition();

        if (!sameSelected && getOnItemSelectedListener() != null) {
            super.setSelection(position, animate);

            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
//            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();

//        if (!sameSelected && getOnItemSelectedListener() != null) {
        if (getOnItemSelectedListener() != null) {
            super.setSelection(position);

            if (sameSelected) {
                // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
                getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
            }

            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
//            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    // set the ceaseFireOnItemClickEvent argument to true to avoid firing an event
    public void setSelection(int position, boolean animate, boolean ceaseFireOnItemClickEvent) {
        OnItemSelectedListener l = getOnItemSelectedListener();
        if (ceaseFireOnItemClickEvent) {
            setOnItemSelectedListener(null);
        }

        super.setSelection(position, animate);

        if (ceaseFireOnItemClickEvent) {
            setOnItemSelectedListener(l);
        }
    }
}