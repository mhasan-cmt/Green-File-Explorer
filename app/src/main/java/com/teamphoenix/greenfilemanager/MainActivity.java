package com.teamphoenix.greenfilemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.teamphoenix.greenfilemanager.databinding.ActivityMainBinding;
import com.teamphoenix.greenfilemanager.fragments.CardFragment;
import com.teamphoenix.greenfilemanager.fragments.HomeFragment;
import com.teamphoenix.greenfilemanager.fragments.InternalStorageFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding mainBinding;
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        mainBinding.mainNavView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                mainBinding.mainDrawer,
                mainBinding.mainToolbar,
                R.string.Open_Drawer,
                R.string.Close_Drawer);
        mainBinding.mainDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, homeFragment)
                .addToBackStack(null)
                .commit();
        mainBinding.mainNavView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                HomeFragment homeFragment = new HomeFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, homeFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_card:
                CardFragment cardFragment = new CardFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, cardFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_internal:
                InternalStorageFragment internalStorageFragment = new InternalStorageFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, internalStorageFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.about:
                Toast.makeText(this, "About Section!", Toast.LENGTH_SHORT).show();
                break;
        }
        mainBinding.mainDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStackImmediate();
        if (mainBinding.mainDrawer.isDrawerOpen(GravityCompat.START)) {
            mainBinding.mainDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}