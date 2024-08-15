package com.aididalam.endorsevalidator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.aididalam.endorsevalidator.pojo.Login;
import com.aididalam.endorsevalidator.pojo.User;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.DBHelper;
import com.aididalam.endorsevalidator.util.Method;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        DBHelper dbHelper=new DBHelper(SplashActivity.this);

        if(dbHelper.getData(1)==null){
            Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
        Call<User> call=api.profile(dbHelper.getData(1).get("token_type").toString()+" "+dbHelper.getData(1).get("access_token").toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code()==401){
                    Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                if(response.code()==200){
                    Intent intent=new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Method.dangerAlert(SplashActivity.this,"Please check your internet connection");
            }
        });
    }
}