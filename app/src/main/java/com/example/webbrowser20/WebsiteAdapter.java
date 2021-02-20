package com.example.webbrowser20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class WebsiteAdapter extends ArrayAdapter<Website> {

    List<Website> websites = new ArrayList<>();

    public WebsiteAdapter (Context context, int layout, ArrayList<Website> websites){
        super(context, layout);
        this.websites = websites;
    }

    public class ViewHolder{
        TextView textView;
        TextView textView2;
    }

    @Override
    public int getCount() {
        return websites.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row;
        row = convertView;
        ViewHolder viewHolder;
        if(row==null){
            row = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = row.findViewById(R.id.textView1);
            viewHolder.textView2 = row.findViewById(R.id.textView2);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        viewHolder.textView.setText(websites.get(position).title);
        viewHolder.textView2.setText(websites.get(position).url);

        return row;
    }

}
