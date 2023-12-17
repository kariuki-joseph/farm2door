package com.example.farm2door.helpers;

import android.content.Context;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.farm2door.R;

public class ToolBarHelper {
    public static void setupToolBar (AppCompatActivity activity, Toolbar toolbar, String title, boolean showBackButton) {
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
    
        TextView toolbarTitle  = toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(title);

        if (showBackButton) {
            toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
            toolbar.setNavigationOnClickListener(v -> {
                activity.finish();
            });
        }
    }
}
