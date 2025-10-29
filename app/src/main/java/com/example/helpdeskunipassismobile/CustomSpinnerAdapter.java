package com.example.helpdeskunipassismobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> items;

    public CustomSpinnerAdapter(Context context, List<String> items) {
        super(context, R.layout.spinner_item_custom, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_custom, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.textViewItem);
        textView.setText(items.get(position));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_item_custom, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.textViewDropdown);
        textView.setText(items.get(position));
        return convertView;
    }
}
