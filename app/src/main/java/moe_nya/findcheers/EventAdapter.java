package moe_nya.findcheers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import moe_nya.findcheers.artifacts.Events;


public class EventAdapter extends BaseAdapter {
    Context context;
    List<Events> eventData;

    public EventAdapter(Context context) {
        this.context = context;
        eventData = DataService.getEventData();
    }

    @Override
    public int getCount() {
        return eventData.size();
    }

    @Override
    public Events getItem(int position) {
        return eventData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.event_item,
                    parent, false);
        }//先check convertview是不是空，如果是就重新创建一个。减少对空间的浪费
        //inflater是将xml表格语言java化

        TextView eventTitle = (TextView) convertView.findViewById(
                R.id.event_title);
        TextView eventAddress = (TextView) convertView.findViewById(
                R.id.event_address);
        TextView eventDescription = (TextView) convertView.findViewById(
                R.id.event_description);

        //下面一行以及if else的逻辑表示的是如何将图片每一个显示都不一样
        ImageView imageView = (ImageView) convertView.findViewById(R.id.event_thumbnail);

        //关键在于如何显示，记住图片所在位置，然后调用名字即可，不用在意类型
        if (position % 4 == 0) {
            imageView.setImageDrawable(context.getDrawable(R.drawable.event_thumbnail));
        } else if (position % 4 == 1) {
            imageView.setImageDrawable(context.getDrawable(R.drawable.banana));
        } else if (position % 4 == 2) {
            imageView.setImageDrawable(context.getDrawable(R.drawable.peach));
        } else {
            imageView.setImageDrawable(context.getDrawable(R.drawable.pear));
        }

        Events r = eventData.get(position);
        eventTitle.setText(r.getTitle());
        eventAddress.setText(r.getAddress());
        eventDescription.setText(r.getDescription());
        return convertView;
    }

}
