package com.temple.onit.dashboard.views;

import android.content.Context;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.temple.onit.R;

public class DashboardAboutView extends ConstraintLayout {
    public DashboardAboutView(@NonNull Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.dash_about_view, this, true);

    }
}
