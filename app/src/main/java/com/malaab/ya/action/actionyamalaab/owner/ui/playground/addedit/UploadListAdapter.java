package com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.ItemAction;
import com.malaab.ya.action.actionyamalaab.owner.data.model.PlaygroundImage;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventItemClicked;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.BaseAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UploadListAdapter extends BaseAdapter<PlaygroundImage> {


    public UploadListAdapter(List<PlaygroundImage> list) {
        super(list);
    }


    public boolean isPhotosUploaded() {
        for (PlaygroundImage image : list) {
            if (!image.isUploaded) {
                return false;
            }
        }
        return true;
    }


    @Override
    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_upload_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindData(RecyclerView.ViewHolder holder, int position, PlaygroundImage item) {
        ((ItemViewHolder) holder).bind(item, position);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_image)
        ImageView img_image;
        @BindView(R.id.img_loading)
        ImageView img_loading;
        @BindView(R.id.btn_delete)
        AppCompatImageButton btn_delete;


        ItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(final PlaygroundImage item, final int position) {
            if (item.isUploaded) {
                btn_delete.setVisibility(View.VISIBLE);
            } else {
                btn_delete.setVisibility(View.GONE);
            }

            Glide.with(img_image.getContext())
                    .load(item.localPath)
                    .placeholder(R.drawable.img_profile_default)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(img_image);

            if (item.isUploaded) {
                Glide.with(img_loading.getContext())
                        .load(R.mipmap.checked)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(img_loading);
            } else {
                Glide.with(img_loading.getContext())
                        .load(R.mipmap.progress)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(img_loading);
            }

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new OnEventItemClicked<>(item, ItemAction.REMOVE, position));
                }
            });
        }
    }
}
