package com.aididalam.endorsevalidator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.aididalam.endorsevalidator.fragment.AddAsset;
import com.aididalam.endorsevalidator.fragment.AllAsset;
import com.aididalam.endorsevalidator.fragment.ChangePassword;
import com.aididalam.endorsevalidator.fragment.PendingAssetSignature;
import com.aididalam.endorsevalidator.fragment.PendingSignature;
import com.aididalam.endorsevalidator.fragment.Profile;
import com.aididalam.endorsevalidator.fragment.ScanAsset;
import com.aididalam.endorsevalidator.pojo.UserFullDetails;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Constant;
import com.aididalam.endorsevalidator.util.DBHelper;
import com.aididalam.endorsevalidator.util.Method;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    Intent intent;
    ImageView profileImg;
    TextView name;
    TextView type;
    View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.my_assets);

        header=navigationView.getHeaderView(0);
        profileImg=header.findViewById(R.id.profileImg);
        name=header.findViewById(R.id.name);
        type=header.findViewById(R.id.type);
        intent = getIntent();

        loadProfileData();

        HashMap user = new DBHelper(MainActivity.this).getData(1);
        if (user.get("type").toString().equals("3")) {
            navigationView.getMenu().findItem(R.id.add_asset).setVisible(true);
            navigationView.getMenu().findItem(R.id.pending_asset_sign).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.add_asset).setVisible(false);
            navigationView.getMenu().findItem(R.id.pending_asset_sign).setVisible(false);
        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        AllAsset fragment = new AllAsset(MainActivity.this, "Current");
        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();

        String prev_menu = intent.getStringExtra("menu");
        if (prev_menu != null) {
            if (prev_menu.equals("pending_asset_sign")) {
                fragmentManager.beginTransaction().replace(R.id.frameLayout, new PendingAssetSignature(MainActivity.this)).commit();
            }
        }
    }

    private void loadProfileData() {
        APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
        Call<UserFullDetails> call=api.profileFullInfo(Method.getToken(MainActivity.this));
        call.enqueue(new Callback<UserFullDetails>() {
            @Override
            public void onResponse(Call<UserFullDetails> call, Response<UserFullDetails> response) {
                if(response.code()==200){
                    name.setText(response.body().name.replace("\"",""));
                    type.setText(response.body().type.replace("\"",""));
                    Picasso.get().load(Constant.api+"/public/profileIMG/"+response.body().img)
                            .error(R.mipmap.ic_launcher_img)
                            .placeholder(R.mipmap.ic_launcher_img)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(profileImg);
                }
            }

            @Override
            public void onFailure(Call<UserFullDetails> call, Throwable t) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = new AllAsset(MainActivity.this, "Current");
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.my_assets) {
            fragment = new AllAsset(MainActivity.this,"Current");
        } else if (id == R.id.my_consumed_assets) {
            fragment = new AllAsset(MainActivity.this,"Previous");
        } else if (id == R.id.check_asset) {
            fragment = new ScanAsset(MainActivity.this);
        } else if (id == R.id.add_asset) {
            fragment = new AddAsset();
        } else if (id == R.id.pending_sign) {
            fragment = new PendingSignature();
        } else if (id == R.id.pending_asset_sign) {
            fragment = new PendingAssetSignature(MainActivity.this);
        } else if (id == R.id.profile) {
            fragment = new Profile();
        } else if (id == R.id.changePassword) {
            fragment = new ChangePassword();
        } else if (id == R.id.logout) {
            new DBHelper(MainActivity.this).deleteData();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            fragment = new AllAsset(MainActivity.this, "Current");
        }


        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


}