package com.temple.onit.authentication;

import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.temple.onit.R;
import com.temple.onit.databinding.ActivityAuthenticationBinding;

public class AuthenticationActivity extends AppCompatActivity {

    ActivityAuthenticationBinding activityAuthenticationBinding;
    AppBarConfiguration config;
    NavController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAuthenticationBinding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        View view  = activityAuthenticationBinding.getRoot();
        setContentView(view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        controller = navHostFragment.getNavController();
        config = new AppBarConfiguration.Builder(controller.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, controller, config);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(controller, config) || super.onSupportNavigateUp();
    }
}