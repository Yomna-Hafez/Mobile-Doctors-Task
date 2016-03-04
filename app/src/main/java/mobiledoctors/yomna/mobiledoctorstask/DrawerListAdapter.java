package mobiledoctors.yomna.mobiledoctorstask;

/**
 * Created by yomna on 3/3/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class DrawerListAdapter extends ArrayAdapter<NavDrawerItem> {

    Context context;
    int layoutResourceId;
    NavDrawerItem data[] = null;

    public DrawerListAdapter(Context context, int layoutResourceId, NavDrawerItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ListItemHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ListItemHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.icon);
            holder.txtTitle = (TextView)row.findViewById(R.id.title);

            row.setTag(holder);
        }
        else
        {
            holder = (ListItemHolder)row.getTag();
        }

        NavDrawerItem navDrawerItem = data[position];
        holder.txtTitle.setText(navDrawerItem.title);
        holder.imgIcon.setImageResource(navDrawerItem.icon);

        return row;
    }

        static class ListItemHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}