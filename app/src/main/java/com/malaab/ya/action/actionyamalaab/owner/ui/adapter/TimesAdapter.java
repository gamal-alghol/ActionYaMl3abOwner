package com.malaab.ya.action.actionyamalaab.owner.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.ItemAction;
import com.malaab.ya.action.actionyamalaab.owner.data.model.CalendarTime;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventItemClicked;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseAdapter;
import com.malaab.ya.action.actionyamalaab.owner.utils.DateTimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TimesAdapter extends BaseAdapter<CalendarTime> {

    public TimesAdapter(List<CalendarTime> list) {
        super(list);
    }


    public void setIsSelectedByUSer(int pos, boolean isSelectedByUSer) {
//        for (CalendarTime calendarDay : list) {
//            calendarDay.isSelectedByUser = false;
//        }

        CalendarTime day = getItem(pos);
        day.isAvailable = isSelectedByUSer;

        updateItem(day, pos, false);
    }

//    public List<CalendarTime> getSelectedItems() {
//        List<CalendarTime> times = new ArrayList<>();
//        for (CalendarTime calendarTime : list) {
//            if (calendarTime.isSelectedByUser) {
//                times.add(calendarTime);
//            }
//        }
//        return times;
//    }


    @Override
    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_times_item, parent, false);
        return new PlaygroundViewHolder(v);
    }

    @Override
    public void onBindData(RecyclerView.ViewHolder holder, int position, CalendarTime item) {
        if (holder instanceof PlaygroundViewHolder) {
            ((PlaygroundViewHolder) holder).bind(item, position);
        }
    }


    class PlaygroundViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rl_container)
        RelativeLayout rl_container;

        @BindView(R.id.txt_timeName)
        TextView txt_timeName;
        @BindView(R.id.img_icon)
        AppCompatImageView img_icon;

        @BindView(R.id.sw_enable)
        Switch sw_enable;

        private Context mContext;


        PlaygroundViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            mContext = itemView.getContext();
        }

        void bind(final CalendarTime item, final int position) {
            txt_timeName.setText(String.valueOf(DateTimeUtils.getTime12Hour(item.timeStart)));
            img_icon.setImageResource(item.image);

            if (item.isAvailable) {
                img_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
                sw_enable.setChecked(true);
            } else {
                img_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.google_color), android.graphics.PorterDuff.Mode.SRC_IN);
                sw_enable.setChecked(false);
            }


//            if (item.isSelectedByUser) {
//                img_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.google_color), android.graphics.PorterDuff.Mode.SRC_IN);
////                txt_isSelected.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent));
////                txt_isSelected.setVisibility(View.VISIBLE);
//            } else {
//                sw_enable.setChecked(true);
//                img_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
////                txt_isSelected.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
////                txt_isSelected.setVisibility(View.INVISIBLE);
//            }

            rl_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    AppController.getInstance().setQrCode(item.qr_code);
                    EventBus.getDefault().post(new OnEventItemClicked<>(item, ItemAction.PICK, position));
                }
            });
        }
    }
}