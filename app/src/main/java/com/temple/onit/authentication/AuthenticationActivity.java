package com.temple.onit.authentication;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.temple.onit.databinding.ActivityAuthenticationBinding;

public class AuthenticationActivity extends AppCompatActivity {

    ActivityAuthenticationBinding activityAuthenticationBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAuthenticationBinding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        View view  = activityAuthenticationBinding.getRoot();
        setContentView(view);
    }
}