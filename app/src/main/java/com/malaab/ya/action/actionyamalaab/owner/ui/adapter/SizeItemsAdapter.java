package com.malaab.ya.action.actionyamalaab.owner.ui.adapter;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.ItemAction;
import com.malaab.ya.action.actionyamalaab.owner.data.model.GenericListItem;
import com.malaab.ya.action.actionyamalaab.owner.data.model.SizeListItem;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Region;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventItemClicked;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SizeItemsAdapter extends BaseAdapter<SizeListItem> {


    public SizeItemsAdapter(List<SizeListItem> list) {
        super(list);
    }


    public void unSelectAll() {
        for (SizeListItem item : list) {
            item.isSelected = false;
        }

        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_generic_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindData(RecyclerView.ViewHolder holder, int position, SizeListItem item) {
        ((ItemViewHolder) holder).bind(item, position);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cv_container)
        CardView cv_container;
        @BindView(R.id.txt_name)
        TextView txt_name;
        @BindView(R.id.img_selected)
        AppCompatImageView img_selected;


        ItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(final SizeListItem item, final int position) {
            txt_name.setText(item.name);

            img_selected.setVisibility(item.isSelected ? View.VISIBLE : View.GONE);

            cv_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.isSelected) {
                        EventBus.getDefault().post(new OnEventItemClicked<>(item, ItemAction.REMOVE, position));
                    } else {
                        EventBus.getDefault().post(new OnEventItemClicked<>(item, ItemAction.PICK, position));
                    }
                }
            });
        }
    }
}