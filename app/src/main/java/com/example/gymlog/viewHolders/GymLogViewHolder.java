package com.example.gymlog.viewHolders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.database.GymLogRepository;

public class GymLogViewHolder extends RecyclerView.ViewHolder {

    private final TextView gymLogViewItem; //reference to xml

    private GymLogViewHolder(View gymLogView) {
        super(gymLogView);
        gymLogViewItem = gymLogView.findViewById(R.id.recyclerItemTextview); //get a reference to items in recyclerItemTextView
    }

    /**
     * set text ot be displayed on the item
     * @param text
     */
    public void bind (String text) {
        gymLogViewItem.setText(text);
    }

    static GymLogViewHolder create(ViewGroup parent) { //reference to recycler object we put in the activity_main xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gymlog_recycler_item, parent, false);
        return new GymLogViewHolder(view);
    }
}
