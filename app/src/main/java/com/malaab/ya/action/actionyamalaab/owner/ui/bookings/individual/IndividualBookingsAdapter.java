package com.malaab.ya.action.actionyamalaab.owner.ui.bookings.individual;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.BookingStatus;
import com.malaab.ya.action.actionyamalaab.owner.annotations.ItemAction;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Booking;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.BookingAgeCategory;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.BookingPlayer;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventItemClicked;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseAdapter;
import com.malaab.ya.action.actionyamalaab.owner.utils.DateTimeUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.ListUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IndividualBookingsAdapter extends BaseAdapter<Booking> {


    public IndividualBookingsAdapter(List<Booking> list) {
        super(list);
    }


    @Override
    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_individual_bookings_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindData(RecyclerView.ViewHolder holder, int position, Booking item) {
        ((ItemViewHolder) holder).bind(item, position);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_players)
        TextView txt_players;
        @BindView(R.id.txt_viewPlayers)
        TextView txt_viewPlayers;
        @BindView(R.id.txt_day)
        TextView txt_day;
        @BindView(R.id.txt_time)
        TextView txt_time;
        @BindView(R.id.txt_playground)
        TextView txt_playground;
        @BindView(R.id.txt_type)
        TextView txt_type;
        @BindView(R.id.txt_age)
        TextView txt_age;
        @BindView(R.id.txt_size)
        TextView txt_size;
        //        @BindView(R.id.txt_duration)
//        TextView txt_duration;
        @BindView(R.id.txt_price)
        TextView txt_price;

        @BindView(R.id.btn_receive)
        Button btn_receive;


        ItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(final Booking item, final int position) {

//            if (!ListUtils.isEmpty(item.ageCategories)) {
//                for (BookingAgeCategory category : item.ageCategories) {
//                    if (!ListUtils.isEmpty(category.players)) {
//                        int players = category.players.size();
//                        for (BookingPlayer player : category.players) {
//                            players += player.invitees;
//                        }
//
//                        if (item.size == players) {
//                            txt_players.setText(String.valueOf(players));
//                            txt_age.setText(String.valueOf(category.name));
//                            break;
//                        }
//                    }
//                }
//            }

            if (!ListUtils.isEmpty(item.ageCategories)) {
                for (BookingAgeCategory category : item.ageCategories) {
                    if (category.isConfirmed) {
                        int players = category.players.size();
                        for (BookingPlayer player : category.players) {
                            players += player.invitees;
                        }
                        txt_players.setText(String.valueOf(players));
                        txt_age.setText(String.valueOf(category.name));
                        break;
                    }
                }
            }

            txt_day.setText(DateTimeUtils.getDatetime(item.timeStart, DateTimeUtils.PATTERN_DATE_3, Locale.getDefault()));
            txt_time.setText(String.format("%s - %s", DateTimeUtils.getTime12Hour(item.timeStart), DateTimeUtils.getTime12Hour(item.timeEnd)));

            txt_playground.setText(item.playground.name);
            txt_type.setText(item.isIndividuals ? "حجز فردي" : "ملعب كامل");
            txt_size.setText(getSize(item.size));
//            txt_duration.setText(getDuration(item.duration));
            txt_price.setText(String.format(Locale.ENGLISH, "%.0f ر.س", item.priceIndividual));

            if (item.status == BookingStatus.ADMIN_APPROVED) {
                btn_receive.setVisibility(View.VISIBLE);
            } else {
                btn_receive.setVisibility(View.GONE);
            }

            txt_viewPlayers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new OnEventItemClicked<>(item, ItemAction.VIEW_LIST, position));
                }
            });

            btn_receive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new OnEventItemClicked<>(item, ItemAction.RECEIVE, position));
                }
            });
        }
    }


    private String getSize(int size) {
        int x = size / 2;
        return String.valueOf(x + " x " + x);
    }

    private String getDuration(int duration) {
        if (duration == 60) {
            return "ساعة واحدة";

        } else if (duration == 90) {
            return "ساعة ونصف";

        } else if (duration == 120) {
            return "ساعتين";
        }

        return "";
    }
}