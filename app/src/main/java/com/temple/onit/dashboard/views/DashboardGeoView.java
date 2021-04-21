package com.temple.onit.dashboard.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.temple.onit.R;

public class DashboardGeoView extends ConstraintLayout implements View.OnClickListener{

    OnClickListener listener;

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.listener = l;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view);
    }
    public DashboardGeoView(@NonNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {

        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.dash_geo_view, this, true);
    }
}
