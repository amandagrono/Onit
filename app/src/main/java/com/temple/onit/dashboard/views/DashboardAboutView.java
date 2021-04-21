package com.temple.onit.dashboard.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.temple.onit.R;

public class DashboardAboutView extends ConstraintLayout implements View.OnClickListener{
    OnClickListener listener;
    public DashboardAboutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.dash_settings_view, this, true);

    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.listener = l;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view);
        Log.i("clicked", "onClick: clicked view");
    }



    /*
    OnClickListener listener;

    public DashboardAboutView(@NonNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.dash_settings_view, this, true);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        this.listener = l;
    }


    @Override
    public void onClick(View view) {
        listener.onClick(view);
        Log.i("clicked", "onClick: clicked view");
    }*/
}
