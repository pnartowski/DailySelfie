package com.example.DailySelfie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RowAdapter extends ArrayAdapter<DailySelfie> {

    private Context context;
    private int resourceId;
    private final List<DailySelfie> values;
    private boolean checkbox = false;

    public RowAdapter(Context context, int resourceId, List<DailySelfie> objects) {
        super(context, resourceId, objects);
        this.context = context;
        this.resourceId = resourceId;
        this.values = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflanter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflanter.inflate(resourceId, parent, false);

        TextView label = (TextView) row.findViewById(R.id.label);
        ImageView image = (ImageView) row.findViewById(R.id.image);
        CheckBox box = (CheckBox) row.findViewById(R.id.checkbox);

        DailySelfie selfie = values.get(position);
        manageCheckBox(box, selfie);

        image.setImageBitmap(selfie.getThumbnail());
        label.setText(selfie.getLabel());

        return row;
    }

    private void manageCheckBox(final CheckBox box, final DailySelfie selfie) {
        if (checkbox) {
            box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selfie.setChecked(box.isChecked());
                }
            });
            box.setVisibility(View.VISIBLE);
            box.setChecked(selfie.isChecked());
            box.setWillNotDraw(false);
        } else {
            box.setVisibility(View.INVISIBLE);
            box.setWillNotDraw(true);
        }
    }

    public void setCheckbox(boolean checkbox) {
        this.checkbox = checkbox;
    }

    public boolean isCheckbox() {
        return checkbox;
    }

    public void switchCheckBoxes() {
        checkbox = !checkbox;
    }
}

