package com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.data.model.DurationListItem;
import com.malaab.ya.action.actionyamalaab.owner.data.model.SizeListItem;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.PlaygroundSchedule;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseAdapter;
import com.malaab.ya.action.actionyamalaab.owner.utils.DateTimeUtils;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlaygroundSchedulesAdapter extends BaseAdapter<PlaygroundSchedule> {

    public OnPlaygroundScheduleClicked mListener;


    public PlaygroundSchedulesAdapter(List<PlaygroundSchedule> list) {
        super(list);
    }

    public void setOnItemClickedListener(OnPlaygroundScheduleClicked listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_schedules_item, parent, false);
        return new PlaygroundSchedulesViewHolder(v);
    }

    @Override
    public void onBindData(RecyclerView.ViewHolder holder, int position, PlaygroundSchedule item) {
        if (holder instanceof PlaygroundSchedulesViewHolder) {
            ((PlaygroundSchedulesViewHolder) holder).bind(item, position);
        }
    }


    class PlaygroundSchedulesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cv_container)
        CardView cv_container;

        @BindView(R.id.txt_day)
        TextView txt_day;
        @BindView(R.id.txt_price)
        TextView txt_price;

        @BindView(R.id.txt_size)
        TextView txt_size;
        @BindView(R.id.txt_duration)
        TextView txt_duration;
        @BindView(R.id.txt_timeStart)
        TextView txt_timeStart;
        @BindView(R.id.txt_timeEnd)
        TextView txt_timeEnd;

        @BindView(R.id.sw_enable)
        Switch sw_enable;


        PlaygroundSchedulesViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(final PlaygroundSchedule item, final int position) {

            txt_day.setText(DateTimeUtils.getArabicDayName(item.day));

            txt_price.setText(String.format(Locale.getDefault(), "%.0f ر.س", item.price));

            StringBuilder size = new StringBuilder();
            for (SizeListItem sizeItem : item.size) {
                size.append(", ").append(sizeItem.name);
            }
            txt_size.setText(size.toString().replaceFirst(", ", ""));

            StringBuilder duration = new StringBuilder();
            for (DurationListItem durationItem : item.duration) {
                duration.append(", ").append(durationItem.name);
            }
            txt_duration.setText(duration.toString().replaceFirst(", ", "").replaceAll("min", "دقيقة"));

            txt_timeStart.setText(DateTimeUtils.changeDateFormat(item.timeStart, DateTimeUtils.PATTERN_TIME_1, DateTimeUtils.PATTERN_TIME));
            txt_timeEnd.setText(DateTimeUtils.changeDateFormat(item.timeEnd, DateTimeUtils.PATTERN_TIME_1, DateTimeUtils.PATTERN_TIME));

            sw_enable.setChecked(!item.isActive);

            sw_enable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        item.isActive = !sw_enable.isChecked();
                        mListener.setOnPlaygroundScheduleEnable(item, position);
                    }
                }
            });

            cv_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.setOnPlaygroundScheduleClicked(item, position);
                    }
                }
            });

            cv_container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListener != null) {
                        mListener.setOnPlaygroundScheduleLongClicked(item, position);
                    }
                    return true;
                }
            });
        }
    }


    public interface OnPlaygroundScheduleClicked {
        void setOnPlaygroundScheduleClicked(PlaygroundSchedule schedule, int position);

        void setOnPlaygroundScheduleLongClicked(PlaygroundSchedule schedule, int position);

        void setOnPlaygroundScheduleEnable(PlaygroundSchedule schedule, int position);
    }
}