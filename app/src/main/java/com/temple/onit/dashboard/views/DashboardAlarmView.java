package com.temple.onit.dashboard.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.temple.onit.R;

public class DashboardAlarmView extends ConstraintLayout{

    public DashboardAlarmView(@NonNull Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.dash_alarm_view, this, true);
    }
}
