package com.malaab.ya.action.actionyamalaab.owner.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.ToolbarOption;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventToolbarItemClicked;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CustomToolbar extends Toolbar {

    @BindView(R.id.txt_title)
    public TextView txt_title;

    @BindView(R.id.btn_menu)
    public AppCompatImageButton btn_menu;
    @BindView(R.id.btn_map)
    public AppCompatImageButton btn_map;


    public CustomToolbar(Context context) {
        super(context);
    }

    public CustomToolbar(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View view = inflater.inflate(R.layout.custom_toolbar, this, true);
            ButterKnife.bind(this, view);

            init();
        }
    }


    private void init() {
    }


    @OnClick(R.id.btn_menu)
    public void openMenu() {
        EventBus.getDefault().post(new OnEventToolbarItemClicked(ToolbarOption.MENU));
    }

    @OnClick(R.id.btn_map)
    public void openMap() {
        EventBus.getDefault().post(new OnEventToolbarItemClicked(ToolbarOption.MAP));
    }
}

