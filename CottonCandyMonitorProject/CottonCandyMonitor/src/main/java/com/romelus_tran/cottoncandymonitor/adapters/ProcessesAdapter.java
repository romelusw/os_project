package com.romelus_tran.cottoncandymonitor.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.romelus_tran.cottoncandymonitor.R;
import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;
import com.romelus_tran.cottoncandymonitor.utils.FontUtils;
import com.romelus_tran.cottoncandymonitor.utils.Pair;

import java.util.List;

/**
 * Created by Brian on 12/1/13.
 */
public class ProcessesAdapter extends ArrayAdapter<MetricUnit> {

    // need reference to inflater since ArrayAdapter's inflater is private.
    private LayoutInflater _inflater;

    public ProcessesAdapter(Context context, int textViewResourceId, List<MetricUnit> processes) {
        super(context, textViewResourceId, processes);
        _inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // If there's no recycled view, inflate one and tag each of the views you'll want to modify later
        if (convertView == null) {
            convertView = _inflater.inflate(R.layout.processes_list, parent, false);

            // This assumes layout/processes_list.xml includes ImageView with an id of "process_icon"
            convertView.setTag(R.id.process_icon, convertView.findViewById(R.id.process_icon));

            // This assumes layout/processes_list.xml includes TextView with an id of "process_name"
            convertView.setTag(R.id.process_name, convertView.findViewById(R.id.process_name));

            // This assumes layout/processes_list.xml includes TextView with an id of "process_id"
            convertView.setTag(R.id.process_id, convertView.findViewById(R.id.process_id));
        }

        MetricUnit mu = getItem(position);
        Pair<Drawable, String> pair = (Pair) mu.getMetricValue();

        ImageView icon = (ImageView) convertView.getTag(R.id.process_icon);
        icon.setImageDrawable(pair.getLeft());

        TextView processName = (TextView) convertView.getTag(R.id.process_name);
        processName.setTypeface(FontUtils.loadFontFromAssets(FontUtils.FONT_CAVIAR_DREAMS_BOLD));
        processName.setText(mu.getMetricAttr());

        TextView processId = (TextView) convertView.getTag(R.id.process_id);
        processId.setTypeface(FontUtils.loadFontFromAssets(FontUtils.FONT_CAVIAR_DREAMS_BOLD));
        processId.setText(pair.getRight());

        return convertView;
    }
}
