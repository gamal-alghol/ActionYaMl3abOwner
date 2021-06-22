package com.malaab.ya.action.actionyamalaab.owner.ui.playground.addedit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.data.model.Day;

import java.util.List;


public class DaysAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Day> mOptions;


    public DaysAdapter(Context context, List<Day> options) {
        this.mOptions = options;
        inflater = (LayoutInflater.from(context));
    }


    @Override
    public int getCount() {
        return mOptions.size();
    }

    @Override
    public Day getItem(int pos) {
        return mOptions.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderItem viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_days_item, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.txt_option = convertView.findViewById(R.id.txt_option);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        viewHolder.txt_option.setText(mOptions.get(position).name);

        return convertView;
    }


    private class ViewHolderItem {
        TextView txt_option;
    }
}