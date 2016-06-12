package com.bilalbaloch.countries;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

/**
 * @author bilalbaloch
 */
public class CountryListAdapter extends BaseAdapter {

    private List<Country> mData;
    private LayoutInflater inflater;
    private final static String TAG = CountryListAdapter.class.getSimpleName();

    public CountryListAdapter(Context context, List<Country> data) {
        super();
        this.mData = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    public void updateDataAndRefreshList(List<Country> items) {
        this.mData = items;
        refresh();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Country getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder viewHolder;
        final Country c = getItem(position);
        final String name = c.getName();
        final String code = c.getCode().toLowerCase(Locale.ENGLISH);

        if(convertView == null) {

            convertView = inflater.inflate( R.layout.country_list_item, parent,false);
            viewHolder = new ViewHolder();

            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.flag = (ImageView) convertView.findViewById(R.id.flag);

            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.position = position;
        viewHolder.name.setText(name);

        int flagId = -1;
        if(code.equalsIgnoreCase("do"))
            flagId = (CountryActivity.instance().findResourceByName(code + "_"));
        else
            flagId = CountryActivity.instance().findResourceByName(code);

        if(flagId > 0) viewHolder.flag.setImageResource(flagId);

        //Log.e(TAG, c.toString());

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        ImageView flag;
        int position=0;
    }
}
