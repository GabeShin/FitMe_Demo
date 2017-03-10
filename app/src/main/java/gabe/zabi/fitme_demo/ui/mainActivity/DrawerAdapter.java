package gabe.zabi.fitme_demo.ui.mainActivity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gabe.zabi.fitme_demo.R;

/**
 * Created by Gabe on 2017-03-10.
 */

public class DrawerAdapter extends BaseAdapter {

    List<String> items;
    Context context;
    TextView title;

    public DrawerAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.single_navigation_list_item, null);
        }
        title = (TextView) convertView.findViewById(R.id.navigation_list_title);
        title.setText(items.get(position));

        return convertView;
    }
}
